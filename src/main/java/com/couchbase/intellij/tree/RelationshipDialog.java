package com.couchbase.intellij.tree;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.couchbase.intellij.tree.node.SchemaDataNodeDescriptor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import utils.TemplateUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;


@SuppressWarnings("rawtypes")
public class RelationshipDialog extends DialogWrapper {
    private ComboBox fieldCombo;
    private ComboBox collectionCombo;
    private JBLabel errorLabel;
    private String target;
    private SchemaDataNodeDescriptor node;
    private Tree tree;


    public RelationshipDialog(String target, SchemaDataNodeDescriptor node, Tree tree) {
        super(true);
        this.target = target;
        this.node = node;
        this.tree = tree;
        init();
        setTitle("Add Relationship Reference");
        getWindow().setMinimumSize(new Dimension(500, 200));
        setResizable(true);
        setOKButtonText("Add");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 200);
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel targetPanelWrapper = new JPanel(new BorderLayout());
        targetPanelWrapper.add(createPanel(), BorderLayout.NORTH);
        mainPanel.add(targetPanelWrapper);

        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        errorLabel = new JBLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        errorPanel.add(errorLabel);
        mainPanel.add(errorPanel);

        return mainPanel;
    }


    private JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] path = target.split("\\.");
        JPanel formPanel = new JPanel();
        formPanel.setBorder(JBUI.Borders.empty(0, 10));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;


        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;

        formPanel.add(TemplateUtil.createComponentWithBalloon(new JBLabel("Target field:"),
                "The field to which the relationship will be applied (e.g. the foreign key)"), c);
        c.weightx = 0.7;
        c.gridx = 1;
        JLabel lbl = new JBLabel(target.replace(path[0] + "." + path[1] + "." + path[2] + ".", ""));
        lbl.setBorder(JBUI.Borders.empty(0, 2, 6, 0));
        formPanel.add(lbl, c);


        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(TemplateUtil.createComponentWithBalloon(new JBLabel("Linked collection:"),
                "The source collection which contains the key or reference attribute."), c);
        c.weightx = 0.7;
        c.gridx = 1;


        collectionCombo = new ComboBox<>(listCollections(target).toArray());
        collectionCombo.addActionListener(e -> {
            if (collectionCombo.getSelectedItem() == null) {
                return;
            }

            Optional<JsonObject> obj = ActiveCluster.getInstance().getChild(path[0])
                    .flatMap(bucket -> bucket.getChild(path[1]))
                    .flatMap(scope -> scope.getChild(collectionCombo.getSelectedItem().toString()))
                    .map(col -> ((CouchbaseCollection) col).generateDocument())
                    .filter(Objects::nonNull)
                    .findFirst();


            fieldCombo.removeAllItems();
            if (obj.isPresent()) {
                List<String> paths = generatePaths(obj.get(), "");
                Collections.sort(paths);
                fieldCombo.addItem("meta().id");
                paths.forEach(k -> fieldCombo.addItem(k));
            }
            fieldCombo.revalidate();
            fieldCombo.setEnabled(true);
            fieldCombo.setSelectedItem(null);

        });
        collectionCombo.setSelectedItem(null);
        formPanel.add(collectionCombo, c);

        // Scope label and combobox
        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(TemplateUtil.createComponentWithBalloon(new JBLabel("Linked field:"),
                "The source attribute that will be referenced by the 'Target field'"), c);
        c.weightx = 0.7;
        c.gridx = 1;


        fieldCombo = new ComboBox<>();
        fieldCombo.setEnabled(false);
        formPanel.add(fieldCombo, c);

        Color backgroundColor = UIManager.getColor("Panel.background");
        String bgHex = String.format("#%02x%02x%02x", backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());

        Color borderColor = UIManager.getColor("Panel.borderColor"); // Attempt to get theme border color
        if (borderColor == null) {
            borderColor = new Color(176, 176, 176); // Default border color (e.g., light gray)
        }
        String borderHex = String.format("#%02x%02x%02x", borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue());

        JLabel banner = new JLabel("<html><div style='padding: 10px; background-color: " + bgHex + "; border: 1px solid " + borderHex + ";'>This relationship mapping helps the IDE to give better autocomplete recommendations.<br> It won't add any constraints in Couchbase.</div></html>");

        banner.setBorder(JBUI.Borders.empty(4, 0, 14, 0));
        panel.add(banner, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private List<String> listCollections(String target) {
        String[] path = target.split("\\.");
        List<ScopeSpec> scopes = ActiveCluster.getInstance().get().bucket(path[0]).collections().getAllScopes();
        List<String> collections = new ArrayList<>();
        for (ScopeSpec spec : scopes) {
            if (spec.name().equals(path[1])) {
                for (CollectionSpec colSpec : spec.collections()) {
                    if (!colSpec.name().equals(path[2])) {
                        collections.add(colSpec.name());
                    }
                }
            }
        }

        return collections;
    }


    private static List<String> generatePaths(JsonObject jsonObject, String prefix) {
        List<String> paths = new ArrayList<>();
        jsonObject.getNames().forEach(key -> {
            Object value = jsonObject.get(key);
            String currentPath = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof JsonObject) {
                paths.addAll(generatePaths((JsonObject) value, currentPath));
            } else if (value instanceof JsonArray) {
                JsonArray array = (JsonArray) value;
                for (int i = 0; i < array.size(); i++) {
                    if (array.get(i) instanceof JsonObject) {
                        paths.addAll(generatePaths((JsonObject) array.get(i), currentPath + "[*]"));
                    }
                }
            } else {
                paths.add(currentPath);
            }
        });
        return paths;
    }


    @Override
    protected void doOKAction() {

        List<String> errors = new ArrayList<>();
        if (collectionCombo.getSelectedItem() == null) {
            errors.add("Please select the linked collection");
        }

        if (fieldCombo.getSelectedItem() == null) {
            errors.add("Please select the linked attribute");
        }

        if (!errors.isEmpty()) {
            String errorMsg = String.join("<br>", errors);
            errorLabel.setText("<html>" + errorMsg + "</html>");
            return;
        } else {

            RelationshipStorage.getInstance().getValue().getRelationships()
                    .computeIfAbsent(ActiveCluster.getInstance().getId(), k -> new HashMap<>());

            String[] split = target.split("\\.");
            String reference = split[0] + "." + split[1] + "." + collectionCombo.getSelectedItem().toString() + "." + fieldCombo.getSelectedItem().toString();
            RelationshipStorage.getInstance().getValue().getRelationships()
                    .get(ActiveCluster.getInstance().getId())
                    .put(target, reference);


            node.setReference(reference);
            tree.revalidate();
        }

        super.doOKAction();
    }
}

