package com.couchbase.intellij.workbench;

import com.intellij.execution.ui.ConsoleViewContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogTest {

    private final List<String> messages = new ArrayList<>();
    private final List<ConsoleViewContentType> types = new ArrayList<>();

    @Before
    public void capture() {
        Log.setLevel(2);
        Log.setPrinter((message, type) -> {
            messages.add(message);
            types.add(type);
        });
    }

    @After
    public void reset() {
        Log.setLevel(2);
        Log.setPrinter(null);
    }

    @Test
    public void infoLevelHasNoDebugFlag() {
        Log.setLevel(2);
        assertFalse(Log.isDebug());
    }

    @Test
    public void debugFlagOnlyAtLevelThree() {
        Log.setLevel(3);
        assertTrue(Log.isDebug());
    }

    @Test
    public void infoIsPrintedWithLeadingNewlineAndInfoType() {
        Log.info("hello");

        assertEquals(1, messages.size());
        assertEquals("\nhello", messages.get(0));
        assertEquals(ConsoleViewContentType.LOG_INFO_OUTPUT, types.get(0));
    }

    @Test
    public void infoFormatsArgumentsWhenPresent() {
        Log.info("count=%d name=%s", 3, "x");

        assertEquals("\ncount=3 name=x", messages.get(0));
    }

    @Test
    public void infoIsSuppressedAtErrorOnlyLevel() {
        Log.setLevel(1);
        Log.info("hidden");
        Log.warning("hidden too");
        Log.debug("hidden as well");

        assertTrue(messages.isEmpty());
    }

    @Test
    public void warningIsPrintedAtDefaultLevel() {
        Log.warning("careful");

        assertEquals("\ncareful", messages.get(0));
        assertEquals(ConsoleViewContentType.LOG_WARNING_OUTPUT, types.get(0));
    }

    @Test
    public void debugIsOnlyPrintedAtLevelThree() {
        Log.debug("dbg");
        assertTrue(messages.isEmpty());

        Log.setLevel(3);
        Log.debug("dbg");
        assertEquals("\ndbg", messages.get(0));
        assertEquals(ConsoleViewContentType.LOG_DEBUG_OUTPUT, types.get(0));
    }

    @Test
    public void errorAlwaysPrintedEvenAtLevelOne() {
        Log.setLevel(1);
        Log.error("bad");

        assertEquals("\nbad", messages.get(0));
        assertEquals(ConsoleViewContentType.LOG_ERROR_OUTPUT, types.get(0));
    }

    @Test
    public void errorWithThrowableAppendsStackTrace() {
        Log.error("failed", new IllegalStateException("boom"));

        String out = messages.get(0);
        assertTrue(out.startsWith("\nfailed error: java.lang.IllegalStateException: boom"));
        assertTrue(out.contains("at com.couchbase.intellij.workbench.LogTest"));
    }

    @Test
    public void errorWithClassPrefixesSimpleName() {
        Log.error(LogTest.class, "ctx", new RuntimeException("x"));

        assertTrue(messages.get(0).startsWith("\nLogTest:ctx error: java.lang.RuntimeException: x"));
    }

    @Test
    public void throwableOnlyErrorPrefixesGenericLabel() {
        Log.error(new RuntimeException("y"));

        assertTrue(messages.get(0).startsWith("\nerror: java.lang.RuntimeException: y"));
    }

    @Test
    public void stdoutPrinterIsUsedWhenNoPrinterSet() {
        // StdoutPrinter must not require an IDE console; it simply prints to System.out.
        Log.StdoutPrinter printer = new Log.StdoutPrinter();
        java.io.PrintStream original = System.out;
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(buffer, true));
        try {
            printer.print("line", ConsoleViewContentType.LOG_INFO_OUTPUT);
        } finally {
            System.setOut(original);
        }
        assertEquals("line" + System.lineSeparator(), buffer.toString());
    }
}
