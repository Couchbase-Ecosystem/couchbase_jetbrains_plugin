package com.couchbase.intellij.workbench;

import com.couchbase.intellij.persistence.FavoriteQuery;
import com.couchbase.intellij.persistence.storage.FavoriteQueryStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.intellij.sdk.language.SQLPPFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteQueryDialog extends DialogWrapper {
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JBList<String> list = new JBList<>(listModel);
    private final JBScrollPane scrollPane = new JBScrollPane(list);

    private final DefaultListModel<String> builtinListModel = new DefaultListModel<>();
    private final JBList<String> builtinList = new JBList<>(builtinListModel);
    private final JBScrollPane builtinScrollPane = new JBScrollPane(builtinList);

    private final EditorEx editor;
    private final EditorEx editorBuiltIn;
    private final Document document;
    private JTabbedPane tabbedPane;

    private JEditorPane queryDesc;

    private List<BuiltinQuery> queries;

    protected FavoriteQueryDialog(Project project, Document document) {
        super(project);
        setTitle("Favorite Queries List");
        this.document = document;

        final Document doc = EditorFactory.getInstance().createDocument("");
        editor = (EditorEx) EditorFactory.getInstance().createEditor(doc, null, SQLPPFileType.INSTANCE, false);

        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setIndentGuidesShown(false);

        final Document builtinDoc = EditorFactory.getInstance().createDocument("");
        editorBuiltIn = (EditorEx) EditorFactory.getInstance().createEditor(builtinDoc, null, SQLPPFileType.INSTANCE,
                false);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5)); // Set left padding
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
                            favList =
                                    favList.stream().filter(f -> !f.getName().equals(value)).collect(Collectors.toList());
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

        try {
            InputStream inputStream =
                    FavoriteQueryDialog.class.getClassLoader().getResourceAsStream("builtinQueries" +
                            "/defaultFavoriteQueries.json");
            ObjectMapper objectMapper = new ObjectMapper();
            queries = objectMapper.readValue(inputStream, new TypeReference<List<BuiltinQuery>>() {
            });

            for (BuiltinQuery query : queries) {
                builtinListModel.addElement(query.getName());
            }

            builtinList.addListSelectionListener(evt -> {
                if (!evt.getValueIsAdjusting()) {
                    JBList list = (JBList) evt.getSource();
                    int index = list.getSelectedIndex();
                    if (index != -1) {
                        ApplicationManager.getApplication().runWriteAction(() -> editorBuiltIn.getDocument().setText(queries.get(index).getQuery()));
                        queryDesc.setText(queries.get(index).getDescription());
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
            e.printStackTrace();
        }

        init();
        getPeer().getWindow().setMinimumSize(new Dimension(700, 700));
    }

    public void addItem(String item) {
        listModel.addElement(item);
    }

    @Override
    protected JComponent createCenterPanel() {

        tabbedPane = new JTabbedPane();

        JPanel yourQueriesPanel = new JPanel(new BorderLayout());
        yourQueriesPanel.add(scrollPane, BorderLayout.WEST);
        yourQueriesPanel.add(editor.getComponent(), BorderLayout.CENTER);
        tabbedPane.addTab("Your Queries", yourQueriesPanel);


        HyperlinkLabel hyperlinkLabel = new HyperlinkLabel("Help us to expand this list");
        hyperlinkLabel.addHyperlinkListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/couchbaselabs/couchbase_jetbrains_plugin" +
                        "/issues"));
            } catch (Exception ex) {
                Log.error(ex);
            }
        });

        queryDesc = new JEditorPane("text/html", "<html></html>");
        queryDesc.setEditable(false);
        queryDesc.setOpaque(false);
        queryDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        queryDesc.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel builtinQueryPanel = new JPanel(new BorderLayout());
        builtinQueryPanel.add(editorBuiltIn.getComponent(), BorderLayout.CENTER);
        builtinQueryPanel.add(queryDesc, BorderLayout.SOUTH);


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bottomPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        bottomPanel.add(hyperlinkLabel);

        JPanel builtInQueriesPanel = new JPanel(new BorderLayout());
        builtInQueriesPanel.add(builtinScrollPane, BorderLayout.WEST);
        builtInQueriesPanel.add(builtinQueryPanel, BorderLayout.CENTER);
        builtInQueriesPanel.add(bottomPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Built-in Queries", builtInQueriesPanel);

        tabbedPane.addChangeListener(e -> {
            list.clearSelection();
            builtinList.clearSelection();
            ApplicationManager.getApplication().runWriteAction(() -> editor.getDocument().setText(""));
            ApplicationManager.getApplication().runWriteAction(() -> editorBuiltIn.getDocument().setText(""));
        });

        return tabbedPane;
    }

    protected Action @NotNull [] createActions() {
        Action cancelAction = getCancelAction();
        Action pasteAction = new DialogWrapperAction("Paste") {
            @Override
            protected void doAction(ActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(() -> {

                    if (tabbedPane.getSelectedIndex() == 1) {
                        if (!editorBuiltIn.getDocument().getText().isEmpty()) {
                            document.setText(editorBuiltIn.getDocument().getText());
                            close(0);
                        }
                    } else {
                        if (!editor.getDocument().getText().isEmpty()) {
                            document.setText(editor.getDocument().getText());
                            close(0);
                        }
                    }
                });
            }
        };

        return new Action[]{cancelAction, pasteAction};
    }
}