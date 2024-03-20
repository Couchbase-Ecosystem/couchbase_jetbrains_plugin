package com.couchbase.intellij.tree.cblite.sync;

import com.couchbase.intellij.workbench.Log;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SyncGatewayTablePanel extends JPanel {

    private final ListTableModel<JsonEntry> model;
    private final JBTable table;
    private List<JsonEntry> entries = new ArrayList<>();
    private int pushCounter;
    private int pullCounter;
    private JLabel pushCountLabel;
    private JLabel pullCountLabel;
    private JLabel syncStatus;

    private final SyncFilters filters = new SyncFilters();

    @Setter
    private List<String> scopes = new ArrayList<>();

    @Setter
    private List<String> collections = new ArrayList<>();


    private static final ColumnInfo[] COLUMNS = {new ColumnInfo<JsonEntry, String>("") {
        @Override
        public String valueOf(JsonEntry jsonEntry) {
            return jsonEntry.getId();
        }

        public boolean isCellEditable(JsonEntry jsonEntry) {
            return true;
        }
    }, new ColumnInfo<JsonEntry, String>("Id") {
        @Override
        public String valueOf(JsonEntry jsonEntry) {
            return jsonEntry.getId();
        }
    }, new ColumnInfo<JsonEntry, Icon>("Direction") {
        @Override
        public Icon valueOf(JsonEntry jsonEntry) {
            return jsonEntry.isPush() ? AllIcons.Chooser.Top : AllIcons.Chooser.Bottom;
        }
    }, new ColumnInfo<JsonEntry, String>("Scope") {
        @Override
        public String valueOf(JsonEntry jsonEntry) {
            return jsonEntry.getScopeName();
        }
    }, new ColumnInfo<JsonEntry, String>("Collection") {
        @Override
        public String valueOf(JsonEntry jsonEntry) {
            return jsonEntry.getCollectionName();
        }
    }, new ColumnInfo<JsonEntry, Icon>("Access Removed") {
        @Override
        public Icon valueOf(JsonEntry jsonEntry) {
            return jsonEntry.isAccessRemoved ? AllIcons.Actions.SetDefault : AllIcons.General.Remove;
        }
    }, new ColumnInfo<JsonEntry, Icon>("Deletion") {
        @Override
        public Icon valueOf(JsonEntry jsonEntry) {
            return jsonEntry.isDeletion ? AllIcons.Actions.SetDefault : AllIcons.General.Remove;
        }
    }

    };


    public SyncGatewayTablePanel(Project project, Tree tree) {

        JPanel centeredTopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centeredTopPanel.add(createStatsPanel());

        setLayout(new BorderLayout());
        add(centeredTopPanel, BorderLayout.NORTH);

        model = new ListTableModel<>(COLUMNS);
        table = new JBTable(model);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(JBUI.Borders.emptyTop(10));

        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        JBScrollPane scrollPane = new JBScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);


        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleHeaderFilters(e);
            }
        });

        TableCellRenderer headerRenderer = getTableCellRenderer();
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        table.getTableHeader().addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (table.columnAtPoint(e.getPoint()) != -1) {
                    table.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    table.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });


        TableColumn actionColumn = table.getColumnModel().getColumn(0);
        actionColumn.setPreferredWidth(30);
        actionColumn.setMaxWidth(30);
        actionColumn.setMinWidth(30);
        actionColumn.setCellRenderer(new RefreshIconRenderer());
        actionColumn.setCellEditor(new RefreshIconEditor(project, tree, model));

        setSizeAndIconCellRender("Direction", 85);
        setSizeAndIconCellRender("Deletion", 80);
        setSizeAndIconCellRender("Access Removed", 125);

        table.setCellSelectionEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);


        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                if (column == 0) {
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        add(mainPanel, BorderLayout.CENTER);

    }

    private JPanel createStatsPanel() {
        pushCountLabel = new JLabel("0");
        pullCountLabel = new JLabel("0");
        scopes.add("_default");
        collections.add("_default._default");

        syncStatus = new JLabel("Disconnected");

        JLabel pushesLabel = new JLabel("<html><b>Pushes: </b></html>");
        JLabel pullsLabel = new JLabel("<html><b>Pulls: </b></html>");
        JLabel syncStatusLabel = new JLabel("<html><b>Sync Status: </b></html>");

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel1.add(pushesLabel);
        panel1.add(pushCountLabel);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel2.add(pullsLabel);
        panel2.add(pullCountLabel);

        JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel3.add(syncStatusLabel);
        panel3.add(syncStatus);

        topPanel.add(panel1);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(panel2);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(panel3);

        return topPanel;
    }

    private void setSizeAndIconCellRender(String columnName, int size) {

        TableColumn column = table.getColumnModel().getColumn(findColumnIndex(columnName));
        column.setCellRenderer(new IconRenderer());
        column.setPreferredWidth(size);
        column.setMaxWidth(size);
        column.setMinWidth(size);
    }

    private void handleHeaderFilters(MouseEvent e) {
        try {
            int columnIndex = table.columnAtPoint(e.getPoint());
            if (columnIndex == -1) return;

            JPopupMenu filterPopup = new JPopupMenu();
            if (columnIndex == 1) {
                handleIdFilter(e, filterPopup);

            } else if (columnIndex == 2) {
                handleSyncDirectionFilter(e, filterPopup);

            } else if (columnIndex == 3) {
                handleScopeFilter(e);

            } else if (columnIndex == 4) {
                handleCollectionFilter(e);

            } else if (columnIndex == 5) {
                handleAccessRemovedFilter(e, filterPopup);

            } else if (columnIndex == 6) {
                handleDeletionFilter(e, filterPopup);

            }

        } catch (Exception ex) {
            Log.error(ex);
        }
    }

    private void handleDeletionFilter(MouseEvent e, JPopupMenu filterPopup) {
        JPanel panel = createFitlerPanel();
        JCheckBox deletionFlag = new JCheckBox("Deletion Flag", (filters.deletion == null || filters.deletion));
        JCheckBox missingDeletionFlag = new JCheckBox("Missing Deletion Flag", (filters.deletion == null || !filters.deletion));
        panel.add(deletionFlag);
        panel.add(missingDeletionFlag);
        filterPopup.add(panel);

        ActionListener actionListener = e1 -> {

            if (deletionFlag.isSelected() && missingDeletionFlag.isSelected()
                    || !deletionFlag.isSelected() && !missingDeletionFlag.isSelected()) {
                filters.deletion = null;
            } else {
                filters.deletion = deletionFlag.isSelected();
            }

            hidePopup(filterPopup);
        };

        deletionFlag.addActionListener(actionListener);
        missingDeletionFlag.addActionListener(actionListener);
        filterPopup.show(table.getTableHeader(), e.getX(), e.getY());
    }

    private void handleAccessRemovedFilter(MouseEvent e, JPopupMenu filterPopup) {
        JPanel panel = createFitlerPanel();
        JCheckBox accessRemovedFlag = new JCheckBox("Access Removed Flag", (filters.accessRemoved == null || filters.accessRemoved));
        JCheckBox missingAccessRemovedFlag = new JCheckBox("Missing Access Removed Flag", (filters.accessRemoved == null || !filters.accessRemoved));
        panel.add(accessRemovedFlag);
        panel.add(missingAccessRemovedFlag);
        filterPopup.add(panel);

        ActionListener actionListener = e1 -> {

            if (accessRemovedFlag.isSelected() && missingAccessRemovedFlag.isSelected()
                    || !accessRemovedFlag.isSelected() && !missingAccessRemovedFlag.isSelected()) {
                filters.accessRemoved = null;
            } else {
                filters.accessRemoved = accessRemovedFlag.isSelected();
            }

            hidePopup(filterPopup);
        };

        accessRemovedFlag.addActionListener(actionListener);
        missingAccessRemovedFlag.addActionListener(actionListener);
        filterPopup.show(table.getTableHeader(), e.getX(), e.getY());
    }

    private void handleCollectionFilter(MouseEvent e) {
        JDialog filterDialog = createDialog();

        JPanel panel = createFitlerPanel();

        ComboBox<String> collectionCombo = new ComboBox<>();
        collectionCombo.addItem("All Collections");
        collections.forEach(collectionCombo::addItem);
        panel.add(collectionCombo);

        if (!filters.collections.isEmpty()) {
            collectionCombo.setSelectedItem(filters.collections.get(0));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton button = new JButton("Apply");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                filters.collections = new ArrayList<>();
                if (collectionCombo.getSelectedItem() != null && !"All Collections".equals(collectionCombo.getSelectedItem().toString())) {
                    filters.collections.add(collectionCombo.getSelectedItem().toString());
                }

                ApplicationManager.getApplication().invokeLater(() -> {
                    table.getTableHeader().revalidate();
                    table.revalidate();
                    reapplyFilters();
                });
                filterDialog.setVisible(false);
                filterDialog.dispose();
            }
        });
        buttonPanel.add(button);
        panel.add(buttonPanel);

        filterDialog.add(panel);
        filterDialog.pack();

        Point point = table.getTableHeader().getLocationOnScreen();
        filterDialog.setLocation(point.x + e.getX(), point.y + e.getY());

        filterDialog.setVisible(true);
    }

    private void handleScopeFilter(MouseEvent e) {
        JDialog filterDialog = createDialog();
        JPanel panel = createFitlerPanel();

        ComboBox<String> scopeCombo = new ComboBox<>();
        scopeCombo.addItem("All Scopes");
        scopes.forEach(scopeCombo::addItem);
        panel.add(scopeCombo);

        if (!filters.scopes.isEmpty()) {
            scopeCombo.setSelectedItem(filters.scopes.get(0));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton button = new JButton("Apply");
        button.addActionListener(e13 -> {

            filters.scopes = new ArrayList<>();
            if (scopeCombo.getSelectedItem() != null && !"All Scopes".equals(scopeCombo.getSelectedItem().toString())) {
                filters.scopes.add(scopeCombo.getSelectedItem().toString());
            }

            ApplicationManager.getApplication().invokeLater(() -> {
                table.getTableHeader().revalidate();
                table.revalidate();
                reapplyFilters();
            });
            filterDialog.setVisible(false);
            filterDialog.dispose();
        });
        buttonPanel.add(button);
        panel.add(buttonPanel);

        filterDialog.add(panel);
        filterDialog.pack();

        Point point = table.getTableHeader().getLocationOnScreen();
        filterDialog.setLocation(point.x + e.getX(), point.y + e.getY());

        filterDialog.setVisible(true);
    }

    private void handleSyncDirectionFilter(MouseEvent e, JPopupMenu filterPopup) {
        JPanel panel = createFitlerPanel();
        JCheckBox isPushCheckBox = new JCheckBox("Push", (filters.direction == null || filters.direction));
        JCheckBox isPullCheckBox = new JCheckBox("Pull", (filters.direction == null || !filters.direction));
        panel.add(isPushCheckBox);
        panel.add(isPullCheckBox);
        filterPopup.add(panel);

        ActionListener actionListener = e1 -> {

            if (isPushCheckBox.isSelected() && isPullCheckBox.isSelected()
                    || !isPushCheckBox.isSelected() && !isPullCheckBox.isSelected()) {
                filters.direction = null;
            } else {
                filters.direction = isPushCheckBox.isSelected();
            }

            hidePopup(filterPopup);
        };

        isPushCheckBox.addActionListener(actionListener);
        isPullCheckBox.addActionListener(actionListener);
        filterPopup.show(table.getTableHeader(), e.getX(), e.getY());
    }

    private void handleIdFilter(MouseEvent e, JPopupMenu filterPopup) {
        JPanel panel = createFitlerPanel();

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Inform the document id:");
        labelPanel.add(label);
        panel.add(labelPanel);

        JBTextField filterField = new JBTextField();
        filterField.setText(filters.id == null ? "" : filters.id);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton button = new JButton("Apply");
        button.addActionListener(e12 -> {
            if (!filterField.getText().isBlank()) {
                filters.id = filterField.getText().trim();
            } else {
                filters.id = null;
            }
            hidePopup(filterPopup);
        });
        buttonPanel.add(button);


        panel.add(filterField);
        panel.add(buttonPanel);

        filterPopup.add(panel);
        filterPopup.show(table.getTableHeader(), e.getX(), e.getY());
    }

    private void hidePopup(JPopupMenu filterPopup) {
        ApplicationManager.getApplication().invokeLater(() -> {
            table.getTableHeader().revalidate();
            table.revalidate();
            reapplyFilters();
        });
        filterPopup.setVisible(false);
    }

    @NotNull
    private static JPanel createFitlerPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(JBUI.Borders.empty(8));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    @NotNull
    private static JDialog createDialog() {
        JDialog filterDialog = new JDialog();
        filterDialog.setUndecorated(true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                filterDialog.dispose();
            }
        });
        return filterDialog;
    }

    @NotNull
    private TableCellRenderer getTableCellRenderer() {
        TableCellRenderer defaultRenderer = table.getTableHeader().getDefaultRenderer();
        return (table1, value, isSelected, hasFocus, row, column) -> {
            Component c = defaultRenderer.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel label) {
                if (!label.getText().isBlank()) {

                    if (("Id".equals(label.getText()) && filters.id != null)
                            || ("Direction".equals(label.getText()) && filters.direction != null)
                            || ("Scope".equals(label.getText()) && !filters.scopes.isEmpty())
                            || ("Collection".equals(label.getText()) && !filters.collections.isEmpty())
                            || ("Access Removed".equals(label.getText()) && filters.accessRemoved != null)
                            || ("Deletion".equals(label.getText()) && filters.deletion != null)) {

                        label.setIcon(AllIcons.General.Filter);
                        label.setHorizontalTextPosition(SwingConstants.RIGHT);
                    }
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }
            }
            return c;
        };
    }


    private boolean shouldShowOnTable(JsonEntry entry) {

        if (filters.id != null && !entry.id.equals(filters.id)) {
            return false;
        }

        if (filters.direction != null && filters.direction != entry.isPush) {
            return false;
        }

        if (filters.deletion != null && filters.deletion != entry.isDeletion) {
            return false;
        }

        if (filters.accessRemoved != null && filters.accessRemoved != entry.isAccessRemoved) {
            return false;
        }

        if (!filters.scopes.isEmpty() && !filters.scopes.contains(entry.scopeName)) {
            return false;
        }

        if (!filters.collections.isEmpty() && !filters.collections.contains(entry.scopeName + "." + entry.collectionName)) {
            return false;
        }

        return true;
    }

    public void reapplyFilters() {
        List<JsonEntry> filtered = entries.stream()
                .filter(this::shouldShowOnTable)
                .collect(Collectors.toList());

        ApplicationManager.getApplication().invokeLater(() -> {
            model.setItems(filtered);
            model.fireTableDataChanged();
        });
    }

    public void addEntry(JsonEntry entry) {

        boolean oldestWasRemoved = false;
        entries.add(entry);
        if (entries.size() >= 100) {
            oldestWasRemoved = true;
            entries.remove(0);
        }

        if (entry.isPush()) {
            pushCounter++;
            pushCountLabel.setText(String.valueOf(pushCounter));
        } else {
            pullCounter++;
            pullCountLabel.setText(String.valueOf(pullCounter));
        }

        if (oldestWasRemoved && model.getRowCount() > 0) {
            model.removeRow(0);
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            if (shouldShowOnTable(entry)) {
                model.addRow(entry);
                model.fireTableDataChanged();
            }
            pushCountLabel.revalidate();
            pullCountLabel.revalidate();
        });
    }

    public void clearTable() {
        entries = new ArrayList<>();
        model.setItems(entries);
        pushCounter = 0;
        pullCounter = 0;
        pullCountLabel.setText("0");
        pushCountLabel.setText("0");

        ApplicationManager.getApplication().invokeLater(() -> {
            model.fireTableDataChanged();
            pushCountLabel.revalidate();
            pullCountLabel.revalidate();
        });
    }


    private int findColumnIndex(String columnName) {
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            if (table.getColumnName(columnIndex).equals(columnName)) {
                return columnIndex;
            }
        }
        return -1;
    }

    static class IconRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);
            if (value instanceof Icon) {
                setIcon((Icon) value);
                setText("");
            }
            return this;
        }
    }

    static class SyncFilters {

        private String id;
        private Boolean direction;
        private List<String> scopes = new ArrayList<>();
        private List<String> collections = new ArrayList<>();

        private Boolean accessRemoved;
        private Boolean deletion;

    }

    public void updateSyncStatus(String text) {

        ApplicationManager.getApplication().invokeLater(() -> {
            syncStatus.setText(text);
            syncStatus.revalidate();
        });
    }

}