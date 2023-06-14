package com.couchbase.intellij.workbench;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.ProjectManager;

public class Log {

    private static ConsoleView console;

    //1 - errors, 2 - errors and info, 3-debug,infos,errors
    public static int logLevel = 2;

    public static void setLevel(int level) {
        logLevel = level;
    }

    public static ConsoleView getLogger() {
        if (console == null) {
            console = TextConsoleBuilderFactory.getInstance().createBuilder(
                    ProjectManager.getInstance().getDefaultProject()).getConsole();
        }
        return console;
    }

    public static void info(String message) {
        if (logLevel >= 2) {
            getLogger().print(message, ConsoleViewContentType.LOG_INFO_OUTPUT);
        }
    }

    public static void debug(String message) {
        if (logLevel >= 3) {
            getLogger().print(message, ConsoleViewContentType.LOG_DEBUG_OUTPUT);
        }
    }

    public static void error(Class c, String message, Exception e) {

        String exception = "";
        if (e.getCause() != null) {
            exception = e.getCause().getMessage();
        } else {
            exception = e.getMessage();
        }

        getLogger().print(c.getSimpleName() + ":" + message + " error: " + exception, ConsoleViewContentType.LOG_ERROR_OUTPUT);
    }

    public static void error(Exception e) {

        String exception = "";
        if (e.getCause() != null) {
            exception = e.getCause().getMessage();
        } else {
            exception = e.getMessage();
        }
        getLogger().print("error: " + exception, ConsoleViewContentType.LOG_ERROR_OUTPUT);
    }

    public static void error(String message) {
        getLogger().print(message, ConsoleViewContentType.LOG_ERROR_OUTPUT);
    }

}
