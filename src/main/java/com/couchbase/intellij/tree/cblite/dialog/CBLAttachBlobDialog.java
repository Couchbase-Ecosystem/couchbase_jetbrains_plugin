package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.util.*;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.couchbase.intellij.workbench.Log;
import com.couchbase.lite.Blob;
import com.couchbase.lite.Collection;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import lombok.Getter;

@Getter
enum FileType {
    AUDIO("Audio", "mp3|wav|aac|flac|ogg|wma"),
    VIDEO("Video", "mp4|avi|mov|flv|wmv|mkv"),
    IMAGE("Image", "jpeg|jpg|png|gif|bmp|svg|webp"),
    TEXT("Text", "txt|doc|docx|pdf"),
    DATA("Data", "csv|xls|xlsx|xml|json|sql");

    private final String type;
    private final String extension;

    FileType(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }
}

public class CBLAttachBlobDialog extends DialogWrapper {
    private final Project project;
    private final Collection collection;
    private final Document document;
    private JComboBox<FileType> fileTypeComboBox;
    private TextFieldWithBrowseButton fileField;
    private JTextField blobFieldTextField;
    private JLabel errorLabel;

    public CBLAttachBlobDialog(Project project, Collection collection, Document document) {
        super(project);
        setTitle("Attach Blob to Document");
        this.project = project;
        this.collection = collection;
        this.document = document;

        init();
        setSize(600, 200);
    }

    public static String isValidName(String name, String type) {
        if (name == null || name.trim().isEmpty()) {
            return type + " name cannot be empty";
        }
        if (name.length() > 251) {
            return type + " name cannot be more than 251 characters";
        }
        String regex = "^[A-Za-z0-9_.-]+$";
        if (!name.matches(regex)) {
            return type + " name contains invalid characters";
        }
        if (name.startsWith("_") || name.startsWith("%")) {
            return type + " name cannot start with _ or %";
        }
        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;

        JLabel fileTypeLabel = new JLabel("Select file type:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(fileTypeLabel, constraints);

        fileTypeComboBox = new JComboBox<>(FileType.values());
        constraints.gridx = 1;
        panel.add(fileTypeComboBox, constraints);

        JLabel fileLabel = new JLabel("Select file:");
        constraints.gridy = 1;
        constraints.gridx = 0;
        panel.add(fileLabel, constraints);

        fileField = new TextFieldWithBrowseButton();
        constraints.gridx = 1;
        panel.add(fileField, constraints);

        JLabel blobFieldLabel = new JLabel("Blob field name:");
        constraints.gridy = 2;
        constraints.gridx = 0;
        panel.add(blobFieldLabel, constraints);

        blobFieldTextField = new JTextField();
        constraints.gridx = 1;
        panel.add(blobFieldTextField, constraints);

        errorLabel = new JLabel();
        errorLabel.setForeground(Color.decode("#FF4444"));
        constraints.gridy = 3;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        panel.add(errorLabel, constraints);


        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false)
                .withFileFilter(file -> {
                    if (file.isDirectory()) {
                        return true;
                    }
                    FileType selectedFileType = (FileType) fileTypeComboBox.getSelectedItem();
                    if (selectedFileType == null) {
                        return false;
                    }
                    String[] extensions = selectedFileType.getExtension().split("\\|");
                    return Arrays.asList(extensions).contains(file.getExtension());
                });
        fileField.addBrowseFolderListener("Select File", null, project, descriptor);


        return panel;
    }

    @Override
    protected void doOKAction() {
        StringBuilder errorMessage = new StringBuilder();
        VirtualFile selectedFile = fileField.getText().isEmpty() ? null
                : LocalFileSystem.getInstance().findFileByPath(fileField.getText());

        if (selectedFile == null) {
            errorMessage.append("No file selected.<br>");
        }

        long fileSizeInMB = Objects.requireNonNull(selectedFile).getLength() / (1024 * 1024);
        if (fileSizeInMB > 20) {
            errorMessage.append("The selected file exceeds the 20 MB size limit.<br>");
        }

        String blobFieldName = blobFieldTextField.getText();
        String validationError = isValidName(blobFieldName, "Blob");
        if (validationError != null) {
            errorMessage.append(validationError).append("<br>");
        }

        FileType selectedFileType = (FileType) fileTypeComboBox.getSelectedItem();
        if (selectedFile.getExtension() == null
                || !selectedFile.getExtension().matches(Objects.requireNonNull(selectedFileType).getExtension())) {
            errorMessage
                    .append("The selected file type does not match the chosen file type. Supported file types are: ")
                    .append(Objects.requireNonNull(selectedFileType).getExtension().replace("|", ", ")).append("<br>");
        }

        if (!errorMessage.isEmpty()) {
            errorLabel.setText("<html>" + errorMessage + "</html>");
            return;
        }

        try {
            MutableDocument mutableDocument = document.toMutable();
            String contentType = getContentType(selectedFileType, selectedFile);
            Blob blob = new Blob(contentType, selectedFile.contentsToByteArray());
            mutableDocument.setBlob(blobFieldName, blob);
            collection.save(mutableDocument);

        } catch (Exception e) {
            Log.error(e.getMessage());
            errorLabel.setText("Error attaching blob to document: " + e.getMessage());
            return;
        }

        errorLabel.setText("");
        super.doOKAction();
    }

    @NotNull
    private static String getContentType(FileType selectedFileType, VirtualFile selectedFile) {
        String contentType;
        switch (Objects.requireNonNull(selectedFileType)) {
            case IMAGE -> contentType = "image/" + selectedFile.getExtension();
            case AUDIO -> contentType = "audio/" + selectedFile.getExtension();
            case VIDEO -> contentType = "video/" + selectedFile.getExtension();
            case TEXT -> contentType = "text/" + selectedFile.getExtension();
            case DATA -> contentType = "application/" + selectedFile.getExtension();
            default -> throw new IllegalArgumentException("Unsupported file type: " + selectedFileType);
        }
        return contentType;
    }
}
