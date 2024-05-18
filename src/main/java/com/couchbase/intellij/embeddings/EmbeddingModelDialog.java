package com.couchbase.intellij.embeddings;

import com.couchbase.intellij.config.CouchbaseSettingsStorage;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmbeddingModelDialog extends DialogWrapper {

    private ComboBox<String> providerComboBox;
    private ComboBox<String> embeddingModelComboBox;
    private JTextArea textArea;

    private final Editor editor;

    private JBLabel errorLabel;

    private final List<EmbeddingProvider> providers = Arrays.asList(new OpenApiEmbeddingProvider(), new GoogleGeminiEmbeddingProvider()
    );

    public EmbeddingModelDialog(Editor editor) {
        super(true);
        this.editor = editor;
        init();
        setTitle("Embedding Generator");
        getPeer().getWindow().setMinimumSize(new Dimension(500, 450));
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        formPanel.add(new JLabel("Provider:"));

        List<String> availableProviders = new ArrayList<>();
        for (EmbeddingProvider provider : providers) {
            if (provider.isAvailable()) {
                availableProviders.add(provider.getName());
            }
        }
        errorLabel = new JBLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        errorLabel.setBorder(JBUI.Borders.emptyTop(5));

        if (availableProviders.isEmpty()) {
            errorLabel.setText("<html>You have no providers configured yet.<br> Go to Settings and add them under the 'Couchbase Plugin' option</html>");
            errorLabel.revalidate();
        }
        providerComboBox = new ComboBox<>(availableProviders.toArray(new String[0]));
        formPanel.add(providerComboBox);
        providerComboBox.setSelectedItem(null);

        formPanel.add(new JLabel("Embedding Model:"));
        embeddingModelComboBox = new ComboBox<>();
        embeddingModelComboBox.setEnabled(false);
        formPanel.add(embeddingModelComboBox);


        CouchbaseSettingsStorage.State state = CouchbaseSettingsStorage.getInstance().getState();
        if (state.getPreferredProvider() != null && !state.getPreferredProvider().isEmpty()) {
            providerComboBox.setSelectedItem(state.getPreferredProvider());

            if (state.getPreferredModel() != null && !state.getPreferredModel().isEmpty()) {
                updateEmbeddingModels(state.getPreferredModel());
            }
        }

        providerComboBox.addActionListener(e -> updateEmbeddingModels(null));

        panel.add(formPanel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel(new BorderLayout());
        JLabel text = new JLabel("Text:");
        text.setBorder(JBUI.Borders.emptyBottom(5));
        textPanel.add(text, BorderLayout.NORTH);

        textArea = new JTextArea();
        SelectionModel selectionModel = editor.getSelectionModel();
        if (selectionModel.hasSelection()) {
            String targetText = selectionModel.getSelectedText();
            if (targetText.startsWith("\"")) {
                targetText = targetText.replace("\"", "");
            }
            if (targetText.endsWith("\"")) {
                targetText = targetText.substring(0, targetText.length() - 1);
            }

            textArea.setText(targetText);
        }
        textPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private void updateEmbeddingModels(String selected) {
        String selectedProvider = (String) providerComboBox.getSelectedItem();

        EmbeddingProvider target = null;
        for (EmbeddingProvider provider : providers) {
            if (provider.getName().equals(selectedProvider)) {
                target = provider;
                break;
            }
        }

        assert target != null;

        embeddingModelComboBox.removeAllItems();
        try {
            for (String model : target.listModels()) {
                embeddingModelComboBox.addItem(model);
            }
            embeddingModelComboBox.setEnabled(true);

            if (selected != null) {
                embeddingModelComboBox.setSelectedItem(selected);
            }
        } catch (IOException e) {
            errorLabel.setText("An error occurred while loading the models. Check the logs for details.");
            Log.error("An error occurred while loading the models", e);
        }
    }

    @Override
    protected JComponent createSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> close(DialogWrapper.CANCEL_EXIT_CODE));

        JButton generateButton = new JButton("Generate Vector");
        generateButton.addActionListener(e -> {
            if (textArea.getText().trim().isEmpty()) {
                errorLabel.setText("Please provide a text that you would like to generate a vector.");
                errorLabel.revalidate();
                return;
            }

            if (embeddingModelComboBox.getSelectedItem() == null) {
                errorLabel.setText("Please select the embedding model");
                errorLabel.revalidate();
                return;
            }

            EmbeddingProvider target = null;
            for (EmbeddingProvider provider : providers) {
                if (provider.getName().equals(providerComboBox.getSelectedItem())) {
                    target = provider;
                    break;
                }
            }
            assert target != null;

            CouchbaseSettingsStorage.getInstance().getState().setPreferredProvider(target.getName());
            CouchbaseSettingsStorage.getInstance().getState().setPreferredModel(embeddingModelComboBox.getSelectedItem().toString());

            try {
                String vector = target.generateEmbedding(embeddingModelComboBox.getSelectedItem().toString(), textArea.getText());
                insertTextWithFolding(editor, vector);
                close(DialogWrapper.OK_EXIT_CODE);
            } catch (IOException ex) {
                Log.error("An error occurred while generating the vector", ex);
                errorLabel.setText("An error occurred while generating the vector. Check the logs for details.");
                errorLabel.revalidate();
            }
        });

        southPanel.add(cancelButton);
        southPanel.add(generateButton);


        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(southPanel, BorderLayout.SOUTH);
        bottomPanel.add(errorLabel, BorderLayout.NORTH);


        return bottomPanel;
    }


    public void insertTextWithFolding(Editor editor, String vector) {
        SelectionModel selectionModel = editor.getSelectionModel();
        CaretModel caretModel = editor.getCaretModel();
        Document document = editor.getDocument();

        Runnable runnable = () -> {
            int startOffset;
            int endOffset;
            if (selectionModel.hasSelection()) {
                startOffset = selectionModel.getSelectionStart();
                endOffset = selectionModel.getSelectionEnd();
                document.replaceString(startOffset, endOffset, vector);
                endOffset = startOffset + vector.length();
            } else {
                startOffset = caretModel.getOffset();
                document.insertString(startOffset, vector);
                endOffset = startOffset + vector.length();
            }

            final int end = endOffset;
            FoldingModel foldingModel = editor.getFoldingModel();
            foldingModel.runBatchFoldingOperation(() -> {
                FoldRegion foldRegion = foldingModel.addFoldRegion(startOffset, end, "[...]");
                if (foldRegion != null) {
                    foldRegion.setExpanded(false);
                }
            });

        };

        WriteCommandAction.runWriteCommandAction(editor.getProject(), runnable);
    }

}
