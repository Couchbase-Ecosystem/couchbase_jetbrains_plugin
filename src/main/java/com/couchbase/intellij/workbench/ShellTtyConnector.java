package com.couchbase.intellij.workbench;

import com.jediterm.core.util.TermSize;
import com.jediterm.terminal.ProcessTtyConnector;
import com.pty4j.PtyProcess;
import com.pty4j.WinSize;

import java.nio.charset.Charset;
import java.util.List;

/** Keeps PTY resizing independent of IntelliJ's changing connector constructors. */
final class ShellTtyConnector extends ProcessTtyConnector {
    private final PtyProcess process;

    ShellTtyConnector(PtyProcess process, Charset charset, List<String> command) {
        super(process, charset, command);
        this.process = process;
    }

    @Override public String getName() {
        return "CB Shell";
    }

    @Override public void resize(TermSize size) {
        if (isConnected()) process.setWinSize(new WinSize(size.getColumns(), size.getRows()));
    }
}
