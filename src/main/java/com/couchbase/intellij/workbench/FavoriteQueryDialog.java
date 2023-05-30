package com.couchbase.intellij.workbench;

import com.couchbase.intellij.persistence.FavoriteQuery;
import com.couchbase.intellij.persistence.storage.FavoriteQueryStorage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.intellij.sdk.language.SQLPPFileType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteQueryDialog extends DialogWrapper {
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JBList<String> list = new JBList<>(listModel);
    private final JBScrollPane scrollPane = new JBScrollPane(list);
    private final EditorEx editor;
    private final Editor targetEditor;

    protected FavoriteQueryDialog(Editor targetEditor) {
        super(null);
        setTitle("Favorite Queries List");
        this.targetEditor = targetEditor;

        final Document doc = EditorFactory.getInstance().createDocument("");
        editor = (EditorEx) EditorFactory.getInstance().createEditor(doc, null, SQLPPFileType.INSTANCE, false);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setIndentGuidesShown(false);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Set left padding
                return this;
            }
        });

        list.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Delete Item");
                    deleteItem.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int index = list.getSelectedIndex();
                            String value = list.getSelectedValue();
                            if (index != -1) {
                                listModel.remove(index);
                            }
                            List<FavoriteQuery> favList = FavoriteQueryStorage.getInstance().getValue().getList();
                            favList = favList.stream().filter(f -> !f.getName().equals(value)).collect(Collectors.toList());
                            FavoriteQueryStorage.getInstance().getValue().setList(favList);
                            ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText(""));
                        }
                    });
                    menu.add(deleteItem);
                    menu.show(list, e.getX(), e.getY());
                }
            }
        });

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedValue = list.getSelectedValue();
                if (selectedValue != null) {
                    List<FavoriteQuery> favList = FavoriteQueryStorage.getInstance().getValue().getList();
                    for (FavoriteQuery fav : favList) {
                        if (fav.getName().equals(selectedValue)) {
                            ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText(fav.getQuery()));
                            break;
                        }
                    }
                }
            }
        });

        FavoriteQueryStorage.getInstance().getValue().getList().forEach(e -> addItem(e.getName()));
        init();
        getPeer().getWindow().setMinimumSize(new Dimension(700, 700));
    }

    public void addItem(String item) {
        listModel.addElement(item);
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.WEST);
        panel.add(editor.getComponent(), BorderLayout.CENTER);
        return panel;
    }

    protected Action[] createActions() {
        Action cancelAction = getCancelAction();
        Action pasteAction = new DialogWrapperAction("Paste") {
            @Override
            protected void doAction(ActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        if (!editor.getDocument().getText().isEmpty()) {
                            targetEditor.getDocument().setText(editor.getDocument().getText());
                            close(0);
                        }
                    }
                });
            }
        };

        return new Action[]{cancelAction, pasteAction};
    }
}