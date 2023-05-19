package com.couchbase.intellij.workbench;

import com.couchbase.intellij.database.SQLExecutor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CustomSqlConsoleAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project != null) {
            // Create SQL editor
            Document document = EditorFactory.getInstance().createDocument("");
            LightVirtualFile virtualFile = new LightVirtualFile("Custom SQL Console", PlainTextLanguage.INSTANCE, document.getText());
            virtualFile.setCharset(CharsetToolkit.UTF8_CHARSET);
            virtualFile.setWritable(true);

            // Open the SQL editor in a new tab
            FileEditorManager.getInstance(project).openFile(virtualFile, true);

            // Create execute button
            JButton executeButton = new JButton("Execute");
            executeButton.addActionListener(e -> {
                String query = document.getText();
                List<Map<String, String>> result = SQLExecutor.query(query);

                // Convert the result to a string
                StringBuilder resultText = new StringBuilder();
                for (Map<String, String> row : result) {
                    for (Map.Entry<String, String> entry : row.entrySet()) {
                        resultText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    }
                    resultText.append("--------\n");
                }

                // Display the result in a popup
                Messages.showMessageDialog(project, resultText.toString(), "Query Result", Messages.getInformationIcon());
            });

            // Note: You will need to find a way to add the execute button to the editor or somewhere else in the IDE
            // Adding it here will not have any effect because the editor tab does not contain a place for this button.
            // You might consider adding the execute button to a toolbar or a context (right-click) menu.
        }
    }
}
