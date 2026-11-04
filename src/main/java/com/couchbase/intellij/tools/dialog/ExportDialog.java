package com.couchbase.intellij.tools.dialog;

import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.ClusterUiTask;
import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.database.collections.CollectionSources;
import com.couchbase.intellij.persistence.FavoriteCollections;
import com.couchbase.intellij.persistence.storage.FavoriteCollectionsStorage;
import com.couchbase.intellij.tools.CBExport;
import com.couchbase.intellij.tree.CollectionSearchDialog;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import utils.HelpIcon;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@SuppressWarnings("rawtypes")
public class ExportDialog extends DialogWrapper {
    private final String ALL_SCOPES = "All Scopes";
    private static final String ALL_COLLECTIONS_OF = "All collections of ";
    private JComboCheckBox scopeCombo;
    private JComboCheckBox colCombo;
    private JButton colSearchButton;
    private int collectionLoadGeneration;
    private ComboBox bucketCombo;
    private JBLabel errorLabel;

    private JBTextField idFieldName;

    private JBTextField scopeFieldName;

    private JBTextField colFieldName;

    private ComboBox<String> formatOptions;

    private JSpinner threadsField;

    private JBCheckBox verbose;

    private TextFieldWithBrowseButton fileDestination;


    public ExportDialog() {
        super(true);
        init();
        setTitle("Export Data");
        getWindow().setMinimumSize(new Dimension(600, 380));
        setResizable(true);
        setOKButtonText("Export");
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel targetPanelWrapper = new JPanel(new BorderLayout());
        targetPanelWrapper.add(createPanel(), BorderLayout.NORTH);
        mainPanel.add(targetPanelWrapper);

        JPanel panel = createAdvancedOptionsPanel();
        panel.setBorder(JBUI.Borders.empty(0, 10));
        CollapsiblePanel col = new CollapsiblePanel("Advanced Settings", panel);
        col.setBorder(JBUI.Borders.emptyTop(15));
        col.setPanelToggleListener(this::pack);
        mainPanel.add(col);

        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        errorLabel = new JBLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        errorPanel.add(errorLabel);
        mainPanel.add(errorPanel);

        return mainPanel;
    }


    private JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new TitledSeparator("Target"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setBorder(JBUI.Borders.empty(0, 10));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        // Bucket label and combobox
        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(new JBLabel("Bucket:"), c);
        c.weightx = 0.7;
        c.gridx = 1;

        Set<String> bucketSet = ActiveCluster.getInstance().get().buckets().getAllBuckets().keySet();
        String[] buckets = bucketSet.toArray(new String[0]);

        bucketCombo = new ComboBox<>(buckets);
        bucketCombo.addActionListener(e -> {
            if (bucketCombo.getSelectedItem() == null) {
                return;
            }
            String bucketId = (String) bucketCombo.getSelectedItem();
            List<String> scopes = ActiveCluster.getInstance().get().bucket(bucketId).collections().getAllScopes().stream().map(ScopeSpec::name).collect(Collectors.toList());
            List<String> list = new ArrayList<>();
            list.add(ALL_SCOPES);
            list.addAll(scopes);

            scopeCombo.removeAllItems();
            scopeCombo.setHint("Select one or more items");
            scopeCombo.setEnabled(true);
            list.forEach(item -> scopeCombo.addItem(item));

            colCombo.removeAllItems();
            colCombo.setEnabled(false);
        });
        bucketCombo.setSelectedItem(null);
        formPanel.add(bucketCombo, c);

        // Scope label and combobox
        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(new JBLabel("Scope:"), c);
        c.weightx = 0.7;
        c.gridx = 1;


        scopeCombo = new JComboCheckBox();
        scopeCombo.setEnabled(false);
        scopeCombo.setItemListener(this::refreshCollectionOptions);

        formPanel.add(scopeCombo, c);

        c.gridy = 2;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(getLabelWithHelp("Collections:", "<html>The collections that you would like to include. You can select <strong>All</strong> or more than one option.</html>"), c);
        c.weightx = 0.7;
        c.gridx = 1;
        colCombo = new JComboCheckBox();
        colCombo.setEnabled(false);
        colSearchButton = new JButton(AllIcons.Actions.Find);
        colSearchButton.setToolTipText("Search for a collection by name and add it to the selection");
        colSearchButton.setMargin(JBUI.insets(2));
        colSearchButton.setEnabled(false);
        colSearchButton.addActionListener(e -> searchCollectionToExport());

        JPanel colRow = new JPanel(new BorderLayout(4, 0));
        colRow.add(colCombo, BorderLayout.CENTER);
        colRow.add(colSearchButton, BorderLayout.EAST);
        formPanel.add(colRow, c);

        c.gridy = 3;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(getLabelWithHelp("Document's key field:", "<html>In Couchbase, the document's key is not " + "part of the body of the document. But when you are exporting the dataset, it is recommended to also include " + "the original keys. This property defines the name of the attribute in the final exported file that will contain the document's key.</html>"), c);
        c.weightx = 0.7;
        c.gridx = 1;
        idFieldName = new JBTextField();
        idFieldName.setText("cbmid");
        formPanel.add(idFieldName, c);

        c.gridy = 4;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(getLabelWithHelp("Scope field name:", "<html>This filed will be used to store the name of the scope the document came from. It will be created on each JSON document.</html>"), c);
        c.weightx = 0.7;
        c.gridx = 1;
        scopeFieldName = new JBTextField();
        scopeFieldName.setText("cbms");
        formPanel.add(scopeFieldName, c);

        c.gridy = 5;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(getLabelWithHelp("Collection field name:", "<html>This filed will be used to store the name of the collection the document came from. It will be created on each JSON document.</html>"), c);
        c.weightx = 0.7;
        c.gridx = 1;
        colFieldName = new JBTextField();
        colFieldName.setText("cbmc");
        formPanel.add(colFieldName, c);

        // Format label and combobox
        c.gridy = 6;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(getLabelWithHelp("Format:", "<html>The format of the dataset specified (lines or list). See the <a href='https://docs.couchbase.com/server/current/tools/cbexport-json.html#dataset-formats'>DATASET FORMATS</a> section for more details on the formats supported.</html>"), c);
        c.weightx = 0.7;
        c.gridx = 1;


        String[] formatOpt = {"JSON Array", "JSON Lines"};
        formatOptions = new ComboBox<>(formatOpt);
        formPanel.add(formatOptions, c);

        c.gridy = 7;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(getLabelWithHelp("File destination folder:", "<html>Folder where the file will be stored</html>"), c);
        c.weightx = 0.7;
        c.gridx = 1;
        fileDestination = new TextFieldWithBrowseButton();
        fileDestination.setText(System.getProperty("user.home"));
        fileDestination.addBrowseFolderListener(null, FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor().withTitle("Select File Destination"));
        formPanel.add(fileDestination, c);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }


    private JPanel createAdvancedOptionsPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(getLabelWithHelp("Threads:", "<html>Specifies the number of concurrent clients to use when exporting data. Fewer clients means exports will take longer, but there will be less cluster resources used to complete the export. More clients means faster exports, but at the cost of more cluster resource usage. This parameter defaults to 1 if it is not specified and it is recommended that this parameter is not set to be higher than the number of CPUs on the machine where the export is taking place..</html>"), c);

        c.weightx = 0.7;
        c.gridx = 1;
        threadsField = new JSpinner(new SpinnerNumberModel(4, 1, 100, 1));
        formPanel.add(threadsField, c);


        c.gridy = 1;
        c.weightx = 0.3;
        c.gridx = 0;
        formPanel.add(getLabelWithHelp("Verbose Log:", "Specifies that logging should be sent to stdout"), c);

        c.weightx = 0.7;
        c.gridx = 1;
        verbose = new JBCheckBox();
        formPanel.add(verbose, c);


        return formPanel;
    }


    private JPanel getLabelWithHelp(String text, String help) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(new JBLabel(text));
        panel.add(HelpIcon.createHelpIcon(help, 5));
        return panel;
    }


    /**
     * Offers "All collections of" per selected scope plus that scope's favorites and first alphabetical page. A
     * scope may hold thousands of collections, so the rest is added through the search button. Names are fetched
     * in the background.
     */
    private void refreshCollectionOptions(List<String> selectedScopes) {
        int mine = ++collectionLoadGeneration;
        colCombo.removeAllItems();
        colSearchButton.setEnabled(false);
        if (selectedScopes.isEmpty() || selectedScopes.contains(ALL_SCOPES)) {
            colCombo.setEnabled(false);
            return;
        }
        String bucket = (String) bucketCombo.getSelectedItem();
        List<String> scopes = new ArrayList<>(selectedScopes);
        ActiveCluster active = ActiveCluster.getInstance();
        FavoriteCollections favorites = FavoriteCollectionsStorage.getInstance().getValue();
        java.util.Map<String, List<String>> favoritesByScope = new java.util.HashMap<>();
        for (String scope : scopes) {
            favoritesByScope.put(scope, favorites.getFavorites(active.getId(), bucket, scope));
        }
        colCombo.setEnabled(false);
        colCombo.setHint("Loading collections...");
        ClusterUiTask.submit(null, active, "Listing collections of " + bucket, connection -> {
            List<String> all = new ArrayList<>();
            List<String> partial = new ArrayList<>();
            for (String scope : scopes) {
                all.add(ALL_COLLECTIONS_OF + scope);
                CollectionListing listing = CollectionListing.build(CollectionSources.forScope(bucket, scope),
                        favoritesByScope.get(scope), List.of(), CollectionListing.PAGE_SIZE);
                for (String name : listing.getNames()) {
                    partial.add(scope + "." + name);
                }
            }
            all.addAll(partial);
            return all;
        }, items -> {
            if (mine != collectionLoadGeneration) {
                return;
            }
            colCombo.setEnabled(true);
            colCombo.setHint("Select one or more items");
            items.forEach(colCombo::addItem);
            colSearchButton.setEnabled(true);
        }, error -> {
            if (mine != collectionLoadGeneration) {
                return;
            }
            errorLabel.setText("<html>Could not list the collections: " + error.getMessage() + "</html>");
        });
    }

    private void searchCollectionToExport() {
        List<String> scopes = scopeCombo.getSelectedItems().stream().filter(s -> !ALL_SCOPES.equals(s)).toList();
        if (scopes.size() == 1) {
            openCollectionSearch(scopes.get(0));
        } else if (!scopes.isEmpty()) {
            JBPopupFactory.getInstance().createPopupChooserBuilder(scopes)
                    .setTitle("Search in Scope")
                    .setItemChosenCallback(this::openCollectionSearch)
                    .createPopup()
                    .showUnderneathOf(colSearchButton);
        }
    }

    private void openCollectionSearch(String scope) {
        String bucket = (String) bucketCombo.getSelectedItem();
        new CollectionSearchDialog(null, CollectionSources.forScope(bucket, scope),
                name -> colCombo.addSelectedItem(scope + "." + name)).show();
    }

    @Override
    protected void doOKAction() {

        List<String> errors = new ArrayList<>();
        if (bucketCombo.getSelectedItem() == null) {
            errors.add("Please select a bucket");
        } else {
            if (scopeCombo.getSelectedItems().isEmpty()) {
                errors.add("Select one or more scopes");
            } else {
                if (!scopeCombo.getSelectedItems().contains(ALL_SCOPES) && colCombo.getSelectedItems().isEmpty()) {
                    errors.add("Select one or more collections");
                }
            }
        }
        if (idFieldName.getText().trim().isEmpty()) {
            errors.add("Please specify the document's key field name");
        }

        if (scopeFieldName.getText().trim().isEmpty()) {
            errors.add("Please specify the scope field name");
        }

        if (colFieldName.getText().trim().isEmpty()) {
            errors.add("Please specify the collection field name");
        }

        if (fileDestination.getText().trim().isEmpty()) {
            errors.add("Inform the file destination folder");
        } else if (!Files.exists(Path.of(fileDestination.getText()))) {
            errors.add("The folder specified does not exists");
        }

        if (!errors.isEmpty()) {
            String errorMsg = String.join("<br>", errors);
            errorLabel.setText("<html>" + errorMsg + "</html>");
            return;
        } else {
            String format = "list";
            if ("JSON Lines".equals(formatOptions.getSelectedItem())) {
                format = "lines";
            }

            CBExport.export((String) bucketCombo.getSelectedItem(), scopeCombo.getSelectedItems(), colCombo.getSelectedItems(), fileDestination.getText(), idFieldName.getText(),
                    scopeFieldName.getText(), colFieldName.getText(), format, threadsField.getValue().toString(), verbose.isSelected());
        }

        super.doOKAction();
    }
}

