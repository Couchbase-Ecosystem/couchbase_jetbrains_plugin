package com.couchbase.intellij.tree;

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
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** Runs real dialogs in the IntelliJ test application, with native mouse/keyboard input. */
public abstract class DialogUiTestCase extends LightPlatformCodeInsightFixture4TestCase {
    static {
        ThreadLeakTracker.longRunningThreadCreated(Disposer.newDisposable("UI test AWT lifetime"),
                "SystemPropertyWatcher");
    }

    protected Robot robot;
    private final List<DialogWrapper> dialogs = new ArrayList<>();

    @Override
    protected boolean runInDispatchThread() {
        // Robot waits for the EDT, so the test itself must run on the JUnit thread.
        return false;
    }

    @Override
    protected void setUp() throws Exception {
        assertFalse("UI tests need a display; on Linux use xvfb-run -a ./gradlew uiTest",
                GraphicsEnvironment.isHeadless());
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

    @Override
    protected void tearDown() throws Exception {
        try {
            GuiActionRunner.execute(() -> {
                for (DialogWrapper dialog : dialogs) {
                    if (!dialog.isDisposed()) {
                        dialog.close(DialogWrapper.CANCEL_EXIT_CODE);
                    }
                }
            });
        } finally {
            try {
                if (robot != null) {
                    robot.cleanUpWithoutDisposingWindows();
                }
            } finally {
                super.tearDown();
            }
        }
    }
}
