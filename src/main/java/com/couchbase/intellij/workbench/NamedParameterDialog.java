package com.couchbase.intellij.workbench;

import com.couchbase.intellij.persistence.NamedParams;
import com.couchbase.intellij.persistence.storage.NamedParamsStorage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.StatusText;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class NamedParameterDialog extends DialogWrapper {
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JBList<String> list = new JBList<>(listModel);
    private final JBScrollPane scrollPane = new JBScrollPane(list);

    private final DefaultListModel<String> builtinListModel = new DefaultListModel<>();
    private final JBList<String> builtinList = new JBList<>(builtinListModel);
    private final JBScrollPane builtinScrollPane = new JBScrollPane(builtinList);

    private final EditorEx editor;
    private final EditorEx editorBuiltIn;
    private final Map<String, String> editorContentMap = new HashMap<>();
    private final JLabel errorLabel;
    private Map<String, String> projectNamedParams;


    protected NamedParameterDialog(Project project) {
        super(project);
        setTitle("Named Parameter List");
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.decode("#FF4444"));

        final Document doc = EditorFactory.getInstance().createDocument("");
        editor = (EditorEx) EditorFactory.getInstance().createEditor(doc, null, PlainTextFileType.INSTANCE, true);
        editor.getDocument().addDocumentListener(new com.intellij.openapi.editor.event.DocumentListener() {
            @Override
            public void documentChanged(@NotNull com.intellij.openapi.editor.event.DocumentEvent event) {
                String selectedValue = list.getSelectedValue();
                if (selectedValue != null) {
                    editorContentMap.put(selectedValue, editor.getDocument().getText());
                }
            }
        });

        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setIndentGuidesShown(false);

        final Document builtinDoc = EditorFactory.getInstance().createDocument("");
        editorBuiltIn = (EditorEx) EditorFactory.getInstance().createEditor(builtinDoc, null,
                PlainTextFileType.INSTANCE, true);

        StatusText emptyText = list.getEmptyText();
        emptyText.setText("Right-click here to start");

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                if (list.getModel().getSize() == 0) {
                    JLabel label = new JLabel("Right-click here to add a new Named Parameter");
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    return label;
                }
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5)); // Set left padding
                return this;
            }
        });

        list.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();

                    JMenuItem addItem = new JMenuItem("Add Item");
                    addItem.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Show input dialog to get the new item name using JetBrains' Messages component
                            String newItemName = Messages.showInputDialog("Enter new name of the New Parameter:",
                                    "Add New Named Parameter", Messages.getQuestionIcon());
                            if (newItemName != null && !newItemName.trim().isEmpty()) {
                                // Add the new item to the list
                                listModel.addElement(newItemName);
                                int newIndex = listModel.getSize() - 1;
                                list.setSelectedIndex(newIndex);
                                list.ensureIndexIsVisible(newIndex);
                                saveNewItem(newItemName);
                            }
                        }
                    });
                    menu.add(addItem);
                    menu.addSeparator();

                    if (!listModel.isEmpty() && list.getSelectedValue() != null) {
                        JMenuItem deleteItem = new JMenuItem("Delete Item");
                        deleteItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int index = list.getSelectedIndex();
                                String value = list.getSelectedValue();
                                if (index != -1) {
                                    listModel.remove(index);
                                }
                                NamedParams params = NamedParamsStorage.getInstance().getValue();
                                params.getParams().remove(value);
                                NamedParamsStorage.getInstance().setValue(params);
                                ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText(""));
                                editorContentMap.remove(value);
                            }
                        });
                        menu.add(deleteItem);
                    }
                    menu.show(list, e.getX(), e.getY());
                }
            }
        });


        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedValue = list.getSelectedValue();
                if (selectedValue != null) {
                    NamedParams params = NamedParamsStorage.getInstance().getValue();
                    String content;
                    if (editorContentMap.containsKey(selectedValue)) {
                        content = editorContentMap.get(selectedValue);
                    } else {
                        content = params.getParams().getOrDefault(selectedValue, "");
                    }
                    ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText(content));
                    editor.setViewer(false);
                } else {
                    ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText(""));
                    editor.setViewer(true); // Make the editor read-only when no item is selected
                }
            }
        });

        NamedParams params = NamedParamsStorage.getInstance().getValue();
        TreeMap<String, String> sortedMap = new TreeMap<>(params.getParams());
        sortedMap.forEach((key, value) -> addItem(key));

        try {
            projectNamedParams = NamedParametersUtil.readProjectNamedParameters(project);
            TreeMap<String, String> treeMap = new TreeMap<>(projectNamedParams);

            for (String name : treeMap.keySet()) {
                builtinListModel.addElement(name);
            }

            builtinList.addListSelectionListener(evt -> {
                if (!evt.getValueIsAdjusting()) {
                    JBList list = (JBList) evt.getSource();
                    if (list.getSelectedIndex() != -1) {
                        ApplicationManager.getApplication().runWriteAction(() -> {

                            editorBuiltIn.getDocument().setText(projectNamedParams.get(list.getSelectedValue().toString()));

                        });
                    }
                }
            });

            builtinList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
                    return this;
                }
            });


        } catch (Exception e) {
            Log.error("Could not load the built-in queries", e);
        }

        init();
        getPeer().getWindow().setMinimumSize(new Dimension(700, 700));
    }

    private void saveNewItem(String itemName) {
        if (itemName != null && !itemName.trim().isEmpty()) {
            editorContentMap.put(itemName, "");
        } else {
            listModel.remove(listModel.getSize() - 1);
        }
    }

    public void addItem(String item) {
        listModel.addElement(item);
    }

    @Override
    protected JComponent createCenterPanel() {

        JTabbedPane tabbedPane = new JBTabbedPane();

        JPanel yourQueriesPanel = new JPanel(new BorderLayout());
        yourQueriesPanel.add(scrollPane, BorderLayout.WEST);
        yourQueriesPanel.add(editor.getComponent(), BorderLayout.CENTER);
        tabbedPane.addTab("My Named Params", yourQueriesPanel);


        JBLabel infoLabel =
                new JBLabel("<html><strong>Note: </strong><small>Project's named parameters are loaded " + "directly "
                        + "from the <b>.cbNamedParams.properties</b> file in your project's root folder<small>" +
                        "<br" + ">You can edit them directly from that file</html>");

        JPanel builtinQueryPanel = new JPanel(new BorderLayout());
        builtinQueryPanel.add(editorBuiltIn.getComponent(), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bottomPanel.setBorder(JBUI.Borders.empty(5, 0));
        bottomPanel.add(infoLabel);

        JPanel builtInQueriesPanel = new JPanel(new BorderLayout());
        builtInQueriesPanel.add(builtinScrollPane, BorderLayout.WEST);
        builtInQueriesPanel.add(builtinQueryPanel, BorderLayout.CENTER);
        builtInQueriesPanel.add(bottomPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Project's Named Params", builtInQueriesPanel);

        tabbedPane.addChangeListener(e -> {
            list.clearSelection();
            builtinList.clearSelection();
            ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText(""));
            ApplicationManager.getApplication().runWriteAction(() -> editorBuiltIn.getDocument().setText(""));
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(errorLabel, BorderLayout.SOUTH);
        return mainPanel;
    }

    protected Action @NotNull [] createActions() {
        Action cancelAction = getCancelAction();
        DialogWrapper dialog = this;
        Action pasteAction = new DialogWrapperAction("Ok") {
            @Override
            protected void doAction(ActionEvent e) {

                if (!editorContentMap.isEmpty()) {
                    for (Map.Entry<String, String> entry : editorContentMap.entrySet()) {
                        if (entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                            errorLabel.setText("The value of the named parameter '" + entry.getKey() + "' can't be " + "empty");
                            errorLabel.revalidate();
                            return;
                        }
                    }

                    NamedParams params = NamedParamsStorage.getInstance().getValue();
                    for (Map.Entry<String, String> entry : editorContentMap.entrySet()) {
                        params.getParams().put(entry.getKey(), entry.getValue().trim());
                    }
                    NamedParamsStorage.getInstance().setValue(params);
                }

                dialog.close(DialogWrapper.OK_EXIT_CODE);
            }
        };

        return new Action[]{cancelAction, pasteAction};
    }
}