package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.Objects;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.decode("#FF4444"));
        init();
        setSize(600, 200);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));

        JLabel fileTypeLabel = new JLabel("Select file type:");
        fileTypeComboBox = new JComboBox<>(FileType.values());
        panel.add(fileTypeLabel);
        panel.add(fileTypeComboBox);

        JLabel fileLabel = new JLabel("Select file:");
        fileField = new TextFieldWithBrowseButton();
        panel.add(fileLabel);
        panel.add(fileField);

        JLabel blobFieldLabel = new JLabel("Blob field name:");
        blobFieldTextField = new JTextField();
        panel.add(blobFieldLabel);
        panel.add(blobFieldTextField);

        panel.add(errorLabel); 

        fileTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateFileChooserDescriptor();
            }
        });
        return panel;
    }

    private void updateFileChooserDescriptor() {
        FileType selectedFileType = (FileType) fileTypeComboBox.getSelectedItem();
        FileChooserDescriptor descriptor;

        descriptor = new FileChooserDescriptor(true, false, false, false, false, false)
                .withFileFilter(
                        file -> file.getExtension() != null && file.getExtension()
                                .matches(Objects.requireNonNull(selectedFileType).getExtension()));

        fileField.addBrowseFolderListener("Select File", null, project, descriptor);
    }

    @Override
    protected void doOKAction() {
        VirtualFile selectedFile = fileField.getText().isEmpty() ? null
                : LocalFileSystem.getInstance().findFileByPath(fileField.getText());

        if (selectedFile == null) {
            errorLabel.setText("No file selected.");
            return;
        } else {
            errorLabel.setText(""); 
        }

        long fileSizeInMB = selectedFile.getLength() / (1024 * 1024);
        if (fileSizeInMB > 20) {
            errorLabel.setText("The selected file exceeds the 20 MB size limit."); 
            return;
        } else {
            errorLabel.setText(""); 
        }

        String blobFieldName = blobFieldTextField.getText();

        try {
            MutableDocument mutableDocument = document.toMutable();
            Blob blob = new Blob(Objects.requireNonNull(selectedFile.getExtension()),
                    selectedFile.contentsToByteArray());
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
}

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
