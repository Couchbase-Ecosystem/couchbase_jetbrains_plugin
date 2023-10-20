package com.couchbase.intellij.tree.iq.chat;

import com.couchbase.intellij.tree.iq.text.TextContent;

import java.util.Optional;

public interface InputContextEntry {

    Optional<TextContent> getTextContent();
}
