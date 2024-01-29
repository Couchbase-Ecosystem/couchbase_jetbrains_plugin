package com.couchbase.intellij.workbench;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.opencsv.bean.processor.ConvertEmptyOrBlankStringsToNull;

import java.io.Console;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {

    //1 - errors, 2 - errors and info, 3-debug,infos,errors
    public static int logLevel = 2;
    private static Printer printer;
    private static ConsoleView console;

    public static void setLevel(int level) {
        logLevel = level;
    }

    public static boolean isDebug() {
        return logLevel >= 3;
    }

    public static void setPrinter(Printer printer) {
        Log.printer = printer;
    }

    public static Printer getLogger() {
        if (printer == null) {
            try {
                printer = new ConsoleViewPrinter(getView());
            } catch (Exception e) {
                // falling back to console logger
                printer = new StdoutPrinter();
            }
        }
        return printer;
    }

    public static ConsoleView getView() {
        if (console == null) {
            ProjectManager projectManager = ProjectManager.getInstance();
            Project[] openProjects = projectManager.getOpenProjects();

            if (openProjects.length == 0) {
                throw new IllegalStateException("No non-default projects are open");
            }

            Project nonDefaultProject = openProjects[0];
            console = TextConsoleBuilderFactory.getInstance().createBuilder(nonDefaultProject).getConsole();
        }
        return console;
    }

    public static void info(String message) {
        if (logLevel >= 2) {
            getLogger().print("\n" + message, ConsoleViewContentType.LOG_INFO_OUTPUT);
        }
    }

    public static void warning(String message) {
        if (logLevel >= 2) {
            getLogger().print("\n" + message, ConsoleViewContentType.LOG_WARNING_OUTPUT);
        }
    }

    public static void debug(String message) {
        if (logLevel >= 3) {
            getLogger().print("\n" + message, ConsoleViewContentType.LOG_DEBUG_OUTPUT);
        }
    }

    public static void debug(String message, Exception e) {
        if (logLevel >= 3) {
            getLogger().print("\n" + message + " error: " + convertToString(e), ConsoleViewContentType.LOG_ERROR_OUTPUT);
        }
    }

    public static void error(Class c, String message, Exception e) {

        String exception = convertToString(e);
        getLogger().print("\n" + c.getSimpleName() + ":" + message + " error: " + exception, ConsoleViewContentType.LOG_ERROR_OUTPUT);
    }

    public static void error(Throwable e) {

        String exception = convertToString(e);
        getLogger().print("\n" + "error: " + exception, ConsoleViewContentType.LOG_ERROR_OUTPUT);
    }

    public static void error(String message, Throwable e) {
        getLogger().print("\n" + message + " error: " + convertToString(e), ConsoleViewContentType.LOG_ERROR_OUTPUT);
    }

    public static void error(String message) {
        getLogger().print("\n" + message, ConsoleViewContentType.LOG_ERROR_OUTPUT);
    }

    private static String convertToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        return stackTrace;
    }

    public interface Printer {
        public void print(String message, ConsoleViewContentType type);
    }

    public static class ConsoleViewPrinter implements Printer {

        private final ConsoleView console;

        public ConsoleViewPrinter(ConsoleView console) {
            this.console = console;
        }

        @Override
        public void print(String message, ConsoleViewContentType type) {
            Log.console.print(message, type);
        }
    }

    public static class StdoutPrinter implements Printer {

        @Override
        public void print(String message, ConsoleViewContentType type) {
            System.out.println(message);
        }
    }
}
