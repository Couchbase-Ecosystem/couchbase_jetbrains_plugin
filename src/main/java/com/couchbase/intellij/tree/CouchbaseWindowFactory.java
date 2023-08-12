package com.couchbase.intellij.tree;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;
import org.jetbrains.annotations.NotNull;


public class CouchbaseWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CouchbaseWindowContent couchbaseWindowContent = new CouchbaseWindowContent(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(couchbaseWindowContent, "Explorer", false);
        toolWindow.getContentManager().addContent(content);
    }
}
