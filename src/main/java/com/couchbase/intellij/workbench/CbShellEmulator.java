package com.couchbase.intellij.workbench;


import com.couchbase.intellij.tools.CBTools;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class CbShellEmulator extends JPanel {
    private Screen screen;
    private Process cliProcess;
    private BufferedReader cliOutputReader;
    private PrintWriter cliInputWriter;

    public CbShellEmulator() {
        setLayout(new BorderLayout());

        try {
            // Initialize the SwingTerminal and set a preferred size
            SwingTerminal terminal = new SwingTerminal();
            terminal.setPreferredSize(new Dimension(800, 600));  // Set a preferred size for the terminal
            screen = new TerminalScreen(terminal);

            // Add the terminal directly to the JPanel
            add(terminal, BorderLayout.CENTER);

            // Revalidate and repaint to ensure the component is laid out properly
            revalidate();
            repaint();


            // Delay screen start to ensure layout is done
            SwingUtilities.invokeLater(() -> {
                try {
                    screen.startScreen();
                    screen.setCursorPosition(null);  // Hide the cursor

                    // Start a thread to handle terminal input and output
                    new Thread(this::handleTerminal).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startCLIProcess() {
        try {
            // Start the CLI process
            String cliPath = CBTools.getTool(CBTools.Type.SHELL).getPath();
            ProcessBuilder builder = new ProcessBuilder(cliPath);
            cliProcess = builder.start();

            // Set up streams to capture output and send input
            cliOutputReader = new BufferedReader(new InputStreamReader(cliProcess.getInputStream()));
            cliInputWriter = new PrintWriter(new OutputStreamWriter(cliProcess.getOutputStream()), true);

            // Start a thread to read the CLI output and display it in the Lanterna terminal
            new Thread(this::readCLIOutput).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readCLIOutput() {
        try {
            String line;
            while ((line = cliOutputReader.readLine()) != null) {
                appendToTerminal(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendToTerminal(String line) {
        try {
            TextGraphics textGraphics = screen.newTextGraphics();
            TerminalPosition currentPosition = screen.getCursorPosition();
            if (currentPosition == null) {
                currentPosition = new TerminalPosition(0, 0);
            }

            // Move the cursor to the next line if needed
            TerminalSize screenSize = screen.getTerminalSize();
            if (currentPosition.getRow() >= screenSize.getRows() - 1) {
                screen.scrollLines(0, screenSize.getRows(), 1);  // Scroll terminal upwards
                currentPosition = new TerminalPosition(0, screenSize.getRows() - 1);
            }

            textGraphics.putString(currentPosition, line);
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleTerminal() {
        try {
            while (true) {
                // Read input from the user using Lanterna's KeyStroke
                KeyStroke keyStroke = screen.readInput();
                if (keyStroke != null && keyStroke.getCharacter() != null) {
                    char typedChar = keyStroke.getCharacter();

                    // Send the input to the CLI process
                    cliInputWriter.print(typedChar);
                    cliInputWriter.flush();

                    // Display the typed character on the terminal
                    appendToTerminal(String.valueOf(typedChar));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        try {
            if (cliProcess != null) {
                cliProcess.destroy();
            }
            if (screen != null) {
                screen.stopScreen();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//import com.couchbase.intellij.tools.CBTools;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.util.Disposer;
//import com.intellij.terminal.JBTerminalSystemSettingsProviderBase;
//import com.intellij.terminal.JBTerminalWidget;
//import com.jediterm.core.util.TermSize;
//import com.jediterm.terminal.ProcessTtyConnector;
//import com.jediterm.terminal.TtyConnector;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//
//public class CbShellEmulator {
//
//    private final JBTerminalWidget terminalWidget;
//    private Process process;
//    private StringBuilder inputBuffer = new StringBuilder();
//
//
//    public CbShellEmulator(Project project, JPanel parentPanel) {
//        // Create terminal widget with system settings
//        JBTerminalSystemSettingsProviderBase settingsProvider = new JBTerminalSystemSettingsProviderBase();
//
//        // Initialize JBTerminalWidget, passing the required parameters
//        terminalWidget = new JBTerminalWidget(project, settingsProvider, Disposer.newDisposable());
//
//        // Add the terminal widget to your parent panel
//        parentPanel.add(terminalWidget.getComponent(), BorderLayout.CENTER);
//        terminalWidget.getComponent().requestFocusInWindow();
//
//        // Start your CLI process and connect to the terminal widget
//        startCliProcess();
//    }
//
//    public void requestFocus() {
//        System.out.println("---------chamou");
//        SwingUtilities.invokeLater(() -> terminalWidget.getComponent().requestFocusInWindow());
//    }
//
//    private void startCliProcess() {
//        try {
//            ProcessBuilder builder = new ProcessBuilder(CBTools.getTool(CBTools.Type.SHELL).getPath());
//            process = builder.start();
//
//            new Thread(() -> readStream(process.getInputStream())).start();
//            new Thread(() -> readStream(process.getErrorStream())).start();
//
//            TtyConnector ttyConnector = new ProcessTtyConnector(process, StandardCharsets.UTF_8) {
//                @Override
//                public String getName() {
//                    return "cbshell_terminal";
//                }
//
//                @Override
//                public void resize(TermSize termSize) {
//                    System.out.println(
//                            "Terminal resized to: " + termSize.getColumns() + " columns, " + termSize.getRows() + " rows");
//                }
//
//                @Override
//                public void write(byte[] bytes) throws IOException {
//                    String typed = new String(bytes, StandardCharsets.UTF_8);
//
//                    for (char ch : typed.toCharArray()) {
//                        switch (ch) {
//                            case '\b':  // Backspace key
//                                handleBackspace();
//                                break;
//                            case 127:  // Delete key (ASCII 127)
//                                handleDelete();
//                                break;
//                            case '\n':  // Enter key
//                            case '\r':  // Carriage return
//                                sendInputToProcess();
//                                break;
//                            default:
//                                addCharacterToBuffer(ch);
//                                break;
//                        }
//                    }
//                }
//
//                private void handleBackspace() {
//                    if (inputBuffer.length() > 0) {
//                        // Remove the last character from the buffer
//                        inputBuffer.deleteCharAt(inputBuffer.length() - 1);
//                        // Simulate backspace effect: move cursor back, overwrite with space, then move back again
//                        SwingUtilities.invokeLater(() -> terminalWidget.writePlainMessage("\b \b"));
//                    }
//                }
//
//                private void handleDelete() {
//                    if (inputBuffer.length() > 0) {
//                        // Remove the last character from the input buffer
//                        inputBuffer.deleteCharAt(inputBuffer.length() - 1);
//
//                        SwingUtilities.invokeLater(() -> {
//                            // Calculate the length of the current line (including the deleted character)
//                            int lineLength = inputBuffer.length() + 1; // +1 for the deleted character
//
//                            // Create a string of spaces to overwrite the current line
//                            String clearLine = " ".repeat(lineLength);
//
//                            // Write the string of spaces to clear the current content
//                            terminalWidget.writePlainMessage(clearLine);
//
//                            // Now, rewrite the updated content of the buffer
//                            terminalWidget.writePlainMessage("\b" + inputBuffer.toString());
//                        });
//                    }
//                }
//
//                private void sendInputToProcess() throws IOException {
//                    // Send the buffered input to the process
//                    process.getOutputStream().write(inputBuffer.toString().getBytes(StandardCharsets.UTF_8));
//                    process.getOutputStream().flush();
//                    inputBuffer.setLength(0);  // Clear the buffer after sending
//                }
//
//                private void addCharacterToBuffer(char ch) {
//                    // Add the character to the input buffer
//                    inputBuffer.append(ch);
//                    // Echo the character to the terminal
//                    SwingUtilities.invokeLater(() -> terminalWidget.writePlainMessage(Character.toString(ch)));
//                }
//            };
//
//            terminalWidget.start(ttyConnector);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void dispose() {
//        Disposer.dispose(terminalWidget);
//        if (process != null) {
//            process.destroy();
//        }
//    }
//
//    private void readStream(InputStream inputStream) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println("Process output: " + line);  // Log the output for debugging
//                terminalWidget.writePlainMessage(line + "\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
