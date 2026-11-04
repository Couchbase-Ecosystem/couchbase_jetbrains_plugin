package com.couchbase.intellij.testutil;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.common.ThreadLeakTracker;

/**
 * The JetBrains Runtime's AWT starts a JDK-internal "SystemPropertyWatcher" daemon thread the first time the
 * toolkit is touched. It lives for the whole JVM, so the platform's per-test thread-leak check (stricter since
 * IntelliJ 2025.2) must be told it is expected.
 */
public final class AllowedThreads {

    private static final Disposable JVM_LIFETIME = Disposer.newDisposable("AllowedThreads");

    private AllowedThreads() {
    }

    public static void allowJdkAwtThreads() {
        ThreadLeakTracker.longRunningThreadCreated(JVM_LIFETIME, "SystemPropertyWatcher");
    }
}
