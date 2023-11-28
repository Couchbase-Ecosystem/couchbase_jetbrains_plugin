package com.couchbase.intellij.tree.iq.text;

public interface CodeFragmentFormatter {

    String format(CodeFragment cf);

    CodeFragmentFormatter withoutDescription();
}
