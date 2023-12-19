package com.couchbase.intellij.workbench.chart;

import com.couchbase.intellij.workbench.Log;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

public class CustomDisplayHandler extends CefDisplayHandlerAdapter {
    @Override
    public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level,
                                    String message, String source, int line) {
        Log.debug("JS console: " + message + " (from " + source + " line " + line + ")");
        return true;
    }
}