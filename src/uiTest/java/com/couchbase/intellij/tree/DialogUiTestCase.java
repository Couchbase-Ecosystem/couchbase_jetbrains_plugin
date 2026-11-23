package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.impl.DialogWrapperPeerImpl;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.common.ThreadLeakTracker;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.util.ThrowableRunnable;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.timing.Condition;
import org.assertj.swing.timing.Pause;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import java.awt.Dialog;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** Runs real dialogs in the IntelliJ test application, with native mouse/keyboard input. */
public abstract class DialogUiTestCase extends LightPlatformCodeInsightFixture4TestCase {
    static {
        ThreadLeakTracker.longRunningThreadCreated(Disposer.newDisposable("UI test AWT lifetime"),
                "SystemPropertyWatcher");
    }

    /** A test (with its setUp and tearDown) that runs longer than this is hung: dump the threads so CI shows where. */
    private static final long WATCHDOG_SECONDS = Long.getLong("ui.test.watchdog.seconds", 300);

    protected Robot robot;
    private final List<DialogWrapper> dialogs = new ArrayList<>();
    /**
     * One scheduler for the whole run. A test's watchdog acts only while that test is still the current one, so a
     * watchdog that outlives its test (whatever kept tearDown from cancelling it) cannot interrupt the test thread
     * or close the dialogs of a later test.
     */
    private static final ScheduledExecutorService WATCHDOG = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "UI test watchdog");
        thread.setDaemon(true);
        return thread;
    });
    private static final AtomicReference<Object> CURRENT_TEST = new AtomicReference<>();
    private Object testToken;
    private ScheduledFuture<?> watchdog;

    @Override
    protected boolean runInDispatchThread() {
        // Robot waits for the EDT, so the test itself must run on the JUnit thread.
        return false;
    }

    @Override
    protected void setUp() throws Exception {
        assertFalse("UI tests need a display; on Linux use xvfb-run -a ./gradlew uiTest",
                GraphicsEnvironment.isHeadless());
        // An interrupt left over from an earlier test would end the next wait in this one at once.
        boolean wasInterrupted = Thread.interrupted();
        trace("setUp", "interrupted=" + wasInterrupted + " activeCluster=" + ActiveCluster.INSTANCE.getValue());
        startWatchdog();
        super.setUp();
        robot = BasicRobot.robotWithCurrentAwtHierarchy();
        robot.settings().delayBetweenEvents(30);
    }

    protected DialogFixture showDialog(Supplier<? extends DialogWrapper> factory) {
        DialogWrapper dialog = GuiActionRunner.execute(() -> {
            // The platform normally substitutes headless dialog peers in unit-test mode,
            // even with java.awt.headless=false. Allocate the production Swing peer.
            // This override is scoped to construction on the EDT; all dialog behavior is real.
            try (MockedStatic<DialogWrapperPeerImpl> peers = Mockito.mockStatic(
                    DialogWrapperPeerImpl.class, Mockito.CALLS_REAL_METHODS)) {
                peers.when(DialogWrapperPeerImpl::isHeadlessEnv).thenReturn(false);
                return factory.get();
            }
        });
        dialogs.add(dialog);
        JDialog window = (JDialog) dialog.getPeer().getWindow();
        // show() enters a modal event loop: schedule it without blocking the test thread.
        SwingUtilities.invokeLater(dialog::show);
        await("dialog is visible", () -> GuiActionRunner.execute(window::isShowing));
        return new DialogFixture(robot, window);
    }

    protected static void await(String description, BooleanSupplier condition) {
        Pause.pause(new Condition(description) {
            @Override
            public boolean test() {
                return condition.getAsBoolean();
            }
        }, 15_000);
    }

    @Override
    protected void runTestRunnable(ThrowableRunnable<Throwable> test) throws Throwable {
        try {
            super.runTestRunnable(test);
        } catch (Throwable failure) {
            try {
                Path artifacts = Path.of(System.getProperty("ui.test.artifacts"));
                Files.createDirectories(artifacts);
                ImageIO.write(new java.awt.Robot().createScreenCapture(
                                new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())), "png",
                        artifacts.resolve(getClass().getSimpleName() + "-" + getName() + ".png").toFile());
            } catch (Exception screenshotFailure) {
                failure.addSuppressed(screenshotFailure);
            }
            throw failure;
        }
    }

    /**
     * Close modal peers before subclass cleanup invokes non-modal platform actions. A failed test can also leave
     * dialogs it did not open itself (an error message, a search dialog); one of them still modal would block the
     * next test, so dispose every dialog that is still showing.
     */
    protected void closeDialogs() {
        GuiActionRunner.execute(() -> {
            for (DialogWrapper dialog : dialogs) {
                if (!dialog.isDisposed()) {
                    dialog.close(DialogWrapper.CANCEL_EXIT_CODE);
                }
            }
            disposeShowingDialogs();
        });
    }

    private static void disposeShowingDialogs() {
        for (Window window : Window.getWindows()) {
            if (window instanceof Dialog && window.isShowing()) {
                window.dispose();
            }
        }
    }

    private void startWatchdog() {
        Thread testThread = Thread.currentThread();
        String test = getClass().getSimpleName() + "-" + getName();
        Object token = new Object();
        testToken = token;
        CURRENT_TEST.set(token);
        watchdog = WATCHDOG.schedule(() -> {
            if (CURRENT_TEST.get() != token) {
                return;
            }
            StringBuilder dump = new StringBuilder(test + " still runs after " + WATCHDOG_SECONDS + "s. Threads:\n");
            for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
                Thread thread = entry.getKey();
                dump.append('\n').append('"').append(thread.getName()).append("\" ").append(thread.getState()).append('\n');
                for (StackTraceElement frame : entry.getValue()) {
                    dump.append("    at ").append(frame).append('\n');
                }
            }
            System.err.println(dump);
            try {
                Path artifacts = Path.of(System.getProperty("ui.test.artifacts"));
                Files.createDirectories(artifacts);
                Files.writeString(artifacts.resolve(test + "-threads.txt"), dump);
            } catch (Exception ignored) {
                // The dump is on stderr already.
            }
            // Unblock what can be unblocked, but only while this test is still the one running.
            if (CURRENT_TEST.get() == token) {
                SwingUtilities.invokeLater(DialogUiTestCase::disposeShowingDialogs);
                testThread.interrupt();
            }
        }, WATCHDOG_SECONDS, TimeUnit.SECONDS);
    }

    private void stopWatchdog() {
        CURRENT_TEST.compareAndSet(testToken, null);
        if (watchdog != null) {
            watchdog.cancel(false);
        }
    }

    /** One line per setUp and tearDown in artifacts/ui-test-trace.txt, to tell what a hung or failed run did. */
    private void trace(String phase, String details) {
        try {
            Path artifacts = Path.of(System.getProperty("ui.test.artifacts"));
            Files.createDirectories(artifacts);
            Files.writeString(artifacts.resolve("ui-test-trace.txt"), java.time.Instant.now() + " " + phase + " "
                            + getClass().getSimpleName() + "." + getName() + " " + details + "\n",
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception ignored) {
            // Tracing must not fail a test.
        }
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            closeDialogs();
        } finally {
            try {
                if (robot != null) {
                    robot.cleanUpWithoutDisposingWindows();
                }
            } finally {
                try {
                    super.tearDown();
                } finally {
                    stopWatchdog();
                    trace("tearDown", "activeCluster=" + ActiveCluster.INSTANCE.getValue());
                }
            }
        }
    }
}
