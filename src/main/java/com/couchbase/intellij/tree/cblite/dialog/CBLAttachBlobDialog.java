package com.couchbase.intellij.tree.cblite.dialog;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jetbrains.annotations.Nullable;

import com.couchbase.lite.Blob;
import com.couchbase.lite.Collection;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Scope;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

public class CBLAttachBlobDialog extends DialogWrapper {
    private final Project project;
    private final Scope scope;
    private final Collection collection;
    private final Document document;
    private JComboBox<FileType> fileTypeComboBox;
    private TextFieldWithBrowseButton fileField;
    private JTextField blobFieldTextField;

    public CBLAttachBlobDialog(Project project, Scope scope, Collection collection, Document document) {
        super(project);
        setTitle("Attach Blob to Document");
        this.project = project;
        this.scope = scope;
        this.collection = collection;
        this.document = document;
        init();
        setSize(1000, 200);
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
                        file -> file.getExtension() != null && file.getExtension().matches(selectedFileType.getExtension()));

        fileField.addBrowseFolderListener("Select File", null, project, descriptor);
    }

    @Override
    protected void doOKAction() {
        VirtualFile selectedFile = fileField.getText().isEmpty() ? null
                : LocalFileSystem.getInstance().findFileByPath(fileField.getText());

        if (selectedFile == null) {
            Messages.showMessageDialog(project, "No file selected.", "Error", Messages.getErrorIcon());
            return;
        }

        // Check if the file size exceeds 20 MB
        long fileSizeInMB = selectedFile.getLength() / (1024 * 1024);
        if (fileSizeInMB > 20) {
            Messages.showMessageDialog(project, "The selected file exceeds the 20 MB size limit.", "Error",
                    Messages.getErrorIcon());
            return;
        }

        String blobFieldName = blobFieldTextField.getText();

        try {
            MutableDocument mutableDocument = document.toMutable();
            Blob blob = new Blob(selectedFile.getExtension(), selectedFile.contentsToByteArray());
            mutableDocument.setBlob(blobFieldName, blob);
            collection.save(mutableDocument);

        } catch (Exception e) {
            Log.error(e.getMessage());
            Messages.showMessageDialog(project, "Error attaching blob to document: " + e.getMessage(), "Error",
                    Messages.getErrorIcon());
            return;
        }

        super.doOKAction();
    }

}

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

    public String getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }
}