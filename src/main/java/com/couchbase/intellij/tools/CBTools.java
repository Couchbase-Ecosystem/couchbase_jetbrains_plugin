package com.couchbase.intellij.tools;

public class CBTools {
    public static CBTool shell = new CBTool();
    public static CBTool cbImport = new CBTool();
    public static CBTool cbExport = new CBTool();

    public static CBTool getShell() {
        return shell;
    }

    public static CBTool getCbImport() {
        return cbImport;
    }

    public static CBTool getCbExport() {
        return cbExport;
    }
    
}
