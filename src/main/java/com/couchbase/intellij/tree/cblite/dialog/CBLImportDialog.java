package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

import com.intellij.openapi.ui.ComboBox;

import utils.TemplateUtil;

public class CBLImportDialog extends DialogWrapper {
    private JTextArea keyPreviewArea;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private int currentPage = 1;
    private TextFieldWithBrowseButton datasetField;

    private JButton nextButton;
    private JButton prevButton;

    private JPanel mainPanel;
    private JPanel southPanel;
    private JPanel datasetPanel;
    private JPanel datasetFormPanel;
    private JPanel datasetLabelHelpPanel;
    private JPanel universalErrorPanel;
    private JBLabel universalErrorLabel;

    ComboBox<String> scopeComboBox;
    ComboBox<String> collectionComboBox;

    public CBLImportDialog(Project project, Tree tree) {
        super(project, true);
        init();
        setTitle("CBLite Import");
    }

    @Override
    protected JComponent createCenterPanel() {
        mainPanel = new JPanel(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createDatasetPanel(), "1");
        cardPanel.add(createTargetPanel(), "2");
        cardPanel.add(createKeyPanel(), "3");
        // cardPanel.add(createSummaryPanel(), "4");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        universalErrorPanel = new JPanel();
        universalErrorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        universalErrorLabel = new JBLabel();
        universalErrorLabel.setForeground(Color.decode("#FF4444"));
        universalErrorLabel.setVisible(false);
        universalErrorPanel.add(universalErrorLabel);
        mainPanel.add(universalErrorPanel, BorderLayout.SOUTH);

        addListeners();
        return mainPanel;
    }

    protected JPanel createDatasetPanel() {
        datasetPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints d = new GridBagConstraints();
        d.fill = GridBagConstraints.HORIZONTAL;
        d.gridy = 0;
        d.weightx = 0.5;
        d.gridx = 0;
        d.insets = JBUI.insets(5);

        JPanel listHeadingPanel = TemplateUtil.getLabelWithHelp(
                "Lists",
                "<html>[<br>" +
                        "{<br>" +
                        "\"key\": \"mykey1\",<br>" +
                        "\"value\": \"myvalue1\"<br>" +
                        "},<br>" +
                        "{\"key\": \"mykey2\", \"value\": \"myvalue2\"}<br>" +
                        "{\"key\": \"mykey3\", \"value\": \"myvalue3\"}<br>" +
                        "{\"key\": \"mykey4\", \"value\": \"myvalue4\"}<br>" +
                        "]</html>");
        JLabel listDefinitionLabel = new JLabel(
                "<html>The list format specifies a file which contains a JSON list where<br>" +
                        "each element in the list is a JSON document. The file may only contain a<br>" +
                        "single list, but the list may be specified over multiple lines. This format<br>" +
                        "is specified by setting the --format option to \"list\". Below is an example<br>" +
                        "of a file in list format.</html>");

        JPanel linesHeadingPanel = TemplateUtil.getLabelWithHelp(
                "Lines",
                "<html>{\"key\": \"mykey1\", \"value\": \"myvalue1\"}<br>" +
                        "{\"key\": \"mykey2\", \"value\": \"myvalue2\"}<br>" +
                        "{\"key\": \"mykey3\", \"value\": \"myvalue3\"}<br>" +
                        "{\"key\": \"mykey4\", \"value\": \"myvalue4\"}<br>" +
                        "</html>");
        JLabel linesDefinitionLabel = new JLabel(
                "<html>The lines format specifies a file that contains one JSON document on<br>" +
                        "every line in the file. This format is specified by setting the --format<br>" +
                        "option to \"lines\". Below is an example of a file in lines format.</html>");

        contentPanel.add(listHeadingPanel, d);
        d.gridy++;
        contentPanel.add(listDefinitionLabel, d);
        d.gridy++;
        contentPanel.add(linesHeadingPanel, d);
        d.gridy++;
        contentPanel.add(linesDefinitionLabel, d);

        datasetFormPanel = new JPanel();
        datasetFormPanel.setBorder(JBUI.Borders.empty(0, 10));
        datasetFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(5);

        c.gridy = 0;
        c.weightx = 0.3;
        c.gridx = 0;

        datasetLabelHelpPanel = TemplateUtil.getLabelWithHelp(
                "Select the Dataset:",
                "<html>Select the file containing the data to import. The file must be in either JSON or CSV format.</html>");
        datasetFormPanel.add(datasetLabelHelpPanel, c);
        c.weightx = 0.7;
        c.gridx = 1;

        datasetField = new TextFieldWithBrowseButton();
        datasetFormPanel.add(datasetField, c);

        d.gridy++;
        contentPanel.add(datasetFormPanel, d);

        datasetPanel.add(contentPanel, BorderLayout.NORTH);

        return datasetPanel;
    }

    protected JPanel createTargetPanel() {
        JPanel targetPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(5);
    
        scopeComboBox = new ComboBox<>();
        collectionComboBox = new ComboBox<>();
    
        c.gridy = 0;
        c.gridx = 0;
        targetPanel.add(new JLabel("Scope:"), c);
        c.gridx = 1;
        targetPanel.add(scopeComboBox, c);
    
        c.gridy = 1;
        c.gridx = 0;
        targetPanel.add(new JLabel("Collection:"), c);
        c.gridx = 1;
        targetPanel.add(collectionComboBox, c);
    
        return targetPanel;
    }
    
    protected JPanel createKeyPanel() {
        JPanel keyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = JBUI.insets(5);
    
        JTextField keyField = new JTextField(25);
        keyPreviewArea = new JTextArea(10, 25);
        keyPreviewArea.setEditable(false);
    
        c.gridy = 0;
        c.gridx = 0;
        keyPanel.add(new JLabel("Key:"), c);
        c.gridx = 1;
        keyPanel.add(keyField, c);
    
        c.gridy = 1;
        c.gridx = 0;
        keyPanel.add(new JLabel("Projected Names:"), c);
        c.gridx = 1;
        keyPanel.add(new JScrollPane(keyPreviewArea), c);
    
        return keyPanel;
    }
    

    @Override
    // South panel
    protected JComponent createSouthPanel() {
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            if (currentPage < 4) {
                nextPage();
                prevButton.setEnabled(currentPage > 1);
                nextButton.setText((currentPage == 4) ? "Import" : "Next");
            } else {
                doOKAction();
            }
        });

        prevButton = new JButton("Previous");
        prevButton.addActionListener(e -> {
            previousPage();
            prevButton.setEnabled(currentPage > 1);
            nextButton.setText((currentPage == 4) ? "Import" : "Next");
        });

        prevButton.setEnabled(currentPage > 1);
        southPanel.add(prevButton);
        southPanel.add(nextButton);

        return southPanel;
    }

    private void nextPage() {
        currentPage++;
        cardLayout.next(cardPanel);
    }

    private void previousPage() {
        currentPage--;
        cardLayout.previous(cardPanel);
    }

    @Override
    protected void doOKAction() {
        // Perform the import operation
    }

    private void addListeners() {

        datasetField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                // Update the key preview
            }
        });

        scopeComboBox.addActionListener(e -> {
            String selectedScope = (String) scopeComboBox.getSelectedItem();

            // Update collectionComboBox based on selectedScope
            // ...
        });

        // java.lang.NullPointerException: Cannot invoke "javax.swing.JTextArea.getDocument()" because "this.keyPreviewArea" is null
        // keyPreviewArea.getDocument().addDocumentListener(new DocumentAdapter() {
        //     @Override
        //     protected void textChanged(@NotNull DocumentEvent e) {
        //         // Update the key preview
        //     }
        // });

    }
}
