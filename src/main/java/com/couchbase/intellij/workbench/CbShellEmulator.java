package com.couchbase.intellij.workbench;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.terminal.JBTerminalSystemSettingsProviderBase;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.terminal.pty.PtyProcessTtyConnector;
import com.intellij.ui.components.panels.Wrapper;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CbShellEmulator  {

    public static final String CONNECT_TO_A_CLUSTER_FOR_CBSHELL = "Connect to a cluster for cbshell";
    private JBTerminalWidget terminalWidget;
    private PtyProcess process;
    private StringBuilder inputBuffer = new StringBuilder();
    private PtyProcessTtyConnector connector;
    private Wrapper wrapper;
    private Project project;

    public CbShellEmulator(Project project, JPanel parentPanel) {
        this.project = project;
        this.wrapper = new Wrapper(new BorderLayout());
        parentPanel.add(wrapper, BorderLayout.CENTER);
        initTerminal();
        ActiveCluster.subscribe(this::onClusterConnected);
    }

    private void initTerminal() {
        // Create terminal widget with system settings
        JBTerminalSystemSettingsProviderBase settingsProvider = new JBTerminalSystemSettingsProviderBase();

        // Initialize JBTerminalWidget, passing the required parameters
        terminalWidget = new JBTerminalWidget(project, settingsProvider, Disposer.newDisposable());

        terminalWidget.getComponent().requestFocusInWindow();
        wrapper.setContent(terminalWidget.getComponent());

        terminalWidget.writePlainMessage(CONNECT_TO_A_CLUSTER_FOR_CBSHELL);
        wrapper.invalidate();
        terminalWidget.getComponent().invalidate();
        wrapper.repaint();
        wrapper.requestFocusInWindow();
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
        command.add("-");
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
            SwingUtilities.invokeLater(() -> {
                clearTerminal();
                terminalWidget.writePlainMessage("Connecting to cluster '" + activeCluster.getClusterURL()+"'...\n");
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    // read password back
                    while (true) {
                        String line = reader.readLine();
                        if (line.isEmpty()) {
                            break;
                        }
                    }

                    connector = new PtyProcessTtyConnector(process, Charset.defaultCharset());
                    terminalWidget.start(connector);
                    terminalWidget.getTerminalTextBuffer().clearHistory();
                    clearTerminal();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        activeCluster.onDisconnect(() -> {
            SwingUtilities.invokeLater(() -> {
                if (process != null) {
                    process.destroyForcibly();
                    terminalWidget.close();
                }
                initTerminal();
            });
        });

        process.onExit().thenAccept(p -> {
            // restart cbshell on `exit`
            if (ActiveCluster.getInstance() != null && ActiveCluster.getInstance().get() != null) {
                terminalWidget.close();
                initTerminal();
                onClusterConnected(ActiveCluster.getInstance());
            }
        });
    }

    private void clearTerminal() {
        terminalWidget.getTerminal().clearScreen();
        terminalWidget.getTerminal().reset(true);
    }

    public void dispose() {
        Disposer.dispose(terminalWidget);
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
        }
    }
}
