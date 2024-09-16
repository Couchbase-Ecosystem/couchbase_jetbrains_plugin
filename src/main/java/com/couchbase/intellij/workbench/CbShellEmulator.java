package com.couchbase.intellij.workbench;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTool;
import com.couchbase.intellij.tools.CBTools;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.terminal.JBTerminalSystemSettingsProviderBase;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.terminal.pty.PtyProcessTtyConnector;
import com.intellij.ui.speedSearch.ElementFilter;
import com.jediterm.core.util.TermSize;
import com.jediterm.terminal.ProcessTtyConnector;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CbShellEmulator  {

    public static final String CONNECT_TO_A_CLUSTER_FOR_CBSHELL = "Connect to a cluster for cbshell";
    private JBTerminalWidget terminalWidget;
    private PtyProcess process;
    private StringBuilder inputBuffer = new StringBuilder();
    private PtyProcessTtyConnector connector;
    private JPanel parentPanel;
    private Project project;

    public CbShellEmulator(Project project, JPanel parentPanel) {
        this.project = project;
        this.parentPanel = parentPanel;
        initTerminal();
        ActiveCluster.subscribe(this::onClusterConnected);
    }

    private void initTerminal() {
        // Create terminal widget with system settings
        JBTerminalSystemSettingsProviderBase settingsProvider = new JBTerminalSystemSettingsProviderBase();

        // Initialize JBTerminalWidget, passing the required parameters
        terminalWidget = new JBTerminalWidget(project, settingsProvider, Disposer.newDisposable());

        terminalWidget.getComponent().requestFocusInWindow();
        parentPanel.add(terminalWidget.getComponent(), BorderLayout.CENTER);
        parentPanel.invalidate();

        terminalWidget.writePlainMessage(CONNECT_TO_A_CLUSTER_FOR_CBSHELL);
    }

    private void onClusterConnected(ActiveCluster activeCluster) {
        clearTerminal();

        ArrayList<String> command = new ArrayList<>();
        command.add(CBTools.getTool(CBTools.Type.SHELL).getPath());
        command.add("--connstr");
        command.add(activeCluster.getClusterURL());
        command.add("-u");
        command.add(activeCluster.getUsername());
        command.add("-p");
        command.add("--no-motd");
        command.add("--disable-config-prompt");

        if (!activeCluster.isSSLEnabled()) {
            command.add("--disable-tls");
        }

        try {
            process = new PtyProcessBuilder().setCommand(command.toArray(String[]::new)).start();
            OutputStream os = process.getOutputStream();
            InputStream is = process.getInputStream();
            byte[] buffer = new byte[9];
            is.read(buffer);
            os.write((activeCluster.getPassword() + "\r\n").getBytes(StandardCharsets.UTF_8));
            os.flush();
            is.read();
            connector = new PtyProcessTtyConnector(process, Charset.defaultCharset());
            terminalWidget.start(connector);
            SwingUtilities.invokeLater(() -> {
                clearTerminal();
                terminalWidget.writePlainMessage("Connecting to cluster '" + activeCluster.getClusterURL()+"'...\n");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        activeCluster.onDisconnect(() -> {
            SwingUtilities.invokeLater(() -> {
                if (process != null) {
                    process.destroyForcibly();
                    terminalWidget.close();
                    parentPanel.remove(terminalWidget);
                    parentPanel.invalidate();
                }
                initTerminal();
            });
        });
    }

    private void clearTerminal() {
        terminalWidget.getTerminal().clearScreen();
        terminalWidget.writePlainMessage("\r");
    }

    public void requestFocus() {
        SwingUtilities.invokeLater(() -> terminalWidget.getComponent().requestFocusInWindow());
    }

    private class Connector extends ProcessTtyConnector {

        public Connector(@NotNull Process process, @NotNull Charset charset) {
            super(process, charset);
        }

        @Override
        public String getName() {
            return "cbshell_terminal";
        }

        @Override
        public void resize(TermSize termSize) {
            System.out.println(
                    "Terminal resized to: " + termSize.getColumns() + " columns, " + termSize.getRows() + " rows");
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            String typed = new String(bytes, StandardCharsets.UTF_8);

            for (char ch : typed.toCharArray()) {
                switch (ch) {
                    case '\b':  // Backspace key
                        handleBackspace();
                        break;
                    case 127:  // Delete key (ASCII 127)
                        handleDelete();
                        break;
                    case '\n':  // Enter key
                    case '\r':  // Carriage return
                        sendInputToProcess();
                        break;
                    default:
                        addCharacterToBuffer(ch);
                        break;
                }
            }
        }
    }

    private void handleBackspace() {
        if (inputBuffer.length() > 0) {
            // Remove the last character from the buffer
            inputBuffer.deleteCharAt(inputBuffer.length() - 1);
            // Simulate backspace effect: move cursor back, overwrite with space, then move back again
            SwingUtilities.invokeLater(() -> terminalWidget.writePlainMessage("\b \b"));
        }
    }

    private void handleDelete() {
        if (inputBuffer.length() > 0) {
            // Remove the last character from the input buffer
            inputBuffer.deleteCharAt(inputBuffer.length() - 1);

            SwingUtilities.invokeLater(() -> {
                // Calculate the length of the current line (including the deleted character)
                int lineLength = inputBuffer.length() + 1; // +1 for the deleted character

                // Create a string of spaces to overwrite the current line
                String clearLine = " ".repeat(lineLength);

                // Write the string of spaces to clear the current content
                terminalWidget.writePlainMessage(clearLine);

                // Now, rewrite the updated content of the buffer
                terminalWidget.writePlainMessage("\b" + inputBuffer.toString());
            });
        }
    }

    private void sendInputToProcess() throws IOException {
        // Send the buffered input to the process
        process.getOutputStream().write(inputBuffer.toString().getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().flush();
        inputBuffer.setLength(0);  // Clear the buffer after sending
    }

    private void addCharacterToBuffer(char ch) {
        // Add the character to the input buffer
        inputBuffer.append(ch);
        // Echo the character to the terminal
        SwingUtilities.invokeLater(() -> terminalWidget.writePlainMessage(Character.toString(ch)));
    }

    public void dispose() {
        Disposer.dispose(terminalWidget);
        if (process != null) {
            process.destroy();
        }
    }

    private void readStream(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Process output: " + line);  // Log the output for debugging
                terminalWidget.writePlainMessage(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
