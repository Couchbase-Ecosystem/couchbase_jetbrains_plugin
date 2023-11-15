package com.couchbase.intellij.tree.cblite.sync;

import com.couchbase.intellij.tree.cblite.CBLDataLoader;
import com.couchbase.intellij.tree.cblite.nodes.CBLFileNodeDescriptor;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.ListTableModel;
import utils.CBIcons;

import javax.swing.*;
import javax.swing.table.TableCellEditor;

public class RefreshIconEditor extends AbstractCellEditor implements TableCellEditor {
    private final JButton button = new JButton(CBIcons.OPEN_DOCUMENT);
    private Object value;
    private String collectionName;
    private String scopeName;


    private final ListTableModel<JsonEntry> model;

    public RefreshIconEditor(Project project, Tree tree, ListTableModel<JsonEntry> model) {
        this.model = model;
        
        button.addActionListener(e -> {
            if (value != null && collectionName != null && scopeName != null) {

                String fileName = value + ".json";
                CBLFileNodeDescriptor descriptor = new CBLFileNodeDescriptor(fileName, scopeName, collectionName, value.toString(), null);
                CBLDataLoader.loadDocument(project, descriptor, tree, true);

                VirtualFile virtualFile = descriptor.getVirtualFile();
                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                fileEditorManager.openFile(virtualFile, true);

            } else {
                Log.error("Error opening the document. value=" + value + ", scope=" + scopeName + ", collection=" + collectionName);
            }
            fireEditingStopped();
        });
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }

    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.value = value;
        this.collectionName = model.getItem(row).collectionName;
        this.scopeName = model.getItem(row).scopeName;
        return button;
    }
}
