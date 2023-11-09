package com.couchbase.intellij.tree.cblite;

import com.couchbase.lite.CouchbaseLiteException;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Date;


public class DocumentExpirationDialog extends DialogWrapper {

    private final JSpinner dateTimeSpinner;
    private final JRadioButton noExpireRadioButton;
    private final JRadioButton expireAtRadioButton;

    private final String scope;
    private final String collection;
    private final String docId;

    protected DocumentExpirationDialog(String scope, String collection, String docId) throws CouchbaseLiteException {
        super(true);
        this.scope = scope;
        this.collection = collection;
        this.docId = docId;
        setTitle("Document Expiration for " + docId);
        getPeer().getWindow().setMinimumSize(new Dimension(540, 150));

        Date initialDate = ActiveCBLDatabase.getInstance().getDatabase()
                .getScope(scope)
                .getCollection(collection)
                .getDocumentExpiration(docId);

        dateTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateTimeSpinner, "yyyy-MM-dd HH:mm:ss");
        dateTimeSpinner.setEditor(editor);
        if (initialDate != null) {
            dateTimeSpinner.setValue(initialDate);
        }

        noExpireRadioButton = new JRadioButton("Document does not expire", (initialDate == null));
        expireAtRadioButton = new JRadioButton("Document expires at:", (initialDate != null));
        ButtonGroup expirationButtonGroup = new ButtonGroup();
        expirationButtonGroup.add(noExpireRadioButton);
        expirationButtonGroup.add(expireAtRadioButton);

        noExpireRadioButton.addActionListener(e -> dateTimeSpinner.setEnabled(false));
        expireAtRadioButton.addActionListener(e -> dateTimeSpinner.setEnabled(true));

        dateTimeSpinner.setEnabled(initialDate != null);

        init();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(540, 150);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(noExpireRadioButton, gbc);

        gbc.gridy++;
        panel.add(expireAtRadioButton, gbc);

        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dateTimeSpinner, gbc);

        return panel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (expireAtRadioButton.isSelected() && dateTimeSpinner.getValue() == null) {
            return new ValidationInfo("Please specify the expiration date.", dateTimeSpinner);
        }
        return null;
    }

    public Date getSelectedDate() {
        if (noExpireRadioButton.isSelected()) {
            return null;
        } else {
            return (Date) dateTimeSpinner.getValue();
        }
    }


    @Override
    protected void doOKAction() {
        CBLDataLoader.setDocumentExpiration(scope, collection, docId, getSelectedDate());
        super.doOKAction();
    }
    
}
