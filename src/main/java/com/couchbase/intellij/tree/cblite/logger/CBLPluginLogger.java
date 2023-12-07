package com.couchbase.intellij.tree.cblite.logger;

import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.LogDomain;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CBLPluginLogger implements Logger {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LogLevel getLevel() {
        switch (Log.logLevel) {
            case 3:
                return LogLevel.DEBUG;
            case 1:
                return LogLevel.ERROR;
            default:
                return LogLevel.INFO;
        }
    }

    @Override
    public void log(LogLevel level, LogDomain domain, String message) {

        String output = LocalDateTime.now().format(formatter) + " [CBLITE] [" + domain.name() + "] - " + message;
        if (level == LogLevel.DEBUG || level == LogLevel.VERBOSE) {
            Log.debug(output);
        } else if (level == LogLevel.INFO) {
            Log.info(output);
        } else if (level == LogLevel.WARNING) {
            Log.warning(output);
        } else if (level == LogLevel.ERROR) {
            Log.error(output);
        }
    }
}
