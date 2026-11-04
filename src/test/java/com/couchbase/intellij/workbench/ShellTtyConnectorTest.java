package com.couchbase.intellij.workbench;

import com.jediterm.core.util.TermSize;
import com.pty4j.PtyProcess;
import com.pty4j.WinSize;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;

public class ShellTtyConnectorTest {
    private PtyProcess process(boolean alive) {
        PtyProcess process = mock(PtyProcess.class);
        when(process.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(process.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(process.isAlive()).thenReturn(alive);
        return process;
    }

    @Test public void resizesTheRunningShellPty() {
        PtyProcess process = process(true);
        var connector = new ShellTtyConnector(process, StandardCharsets.UTF_8, List.of("cbshell"));
        connector.resize(new TermSize(120, 40));
        verify(process).setWinSize(new WinSize(120, 40));
    }

    @Test public void doesNotResizeAnExitedShell() {
        PtyProcess process = process(false);
        var connector = new ShellTtyConnector(process, StandardCharsets.UTF_8, List.of("cbshell"));
        connector.resize(new TermSize(120, 40));
        verify(process, never()).setWinSize(any());
    }
}
