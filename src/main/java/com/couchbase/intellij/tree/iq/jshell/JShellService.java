package com.couchbase.intellij.tree.iq.jshell;

import jdk.jshell.SnippetEvent;

import java.util.List;

public interface JShellService {

    List<SnippetEvent> eval(String input);

}
