package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.intellij.tree.iq.text.CodeFragment;
import com.intellij.openapi.project.Project;

import java.util.List;

public interface ContextAwareSnippetizer {

    List<CodeFragment> fetchSnippets(Project project);
}
