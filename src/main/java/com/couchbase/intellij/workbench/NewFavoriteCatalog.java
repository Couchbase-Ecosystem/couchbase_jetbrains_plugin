package com.couchbase.intellij.workbench;

import com.couchbase.intellij.persistence.FavoriteQueries;
import com.couchbase.intellij.persistence.FavoriteQuery;
import com.couchbase.intellij.persistence.storage.FavoriteQueryStorage;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class NewFavoriteCatalog extends DialogWrapper {
    private JTextField textField;

    private JLabel errorLabel;
    private final AnAction anAction;

    private final DefaultActionGroup favoriteActionGroup;
    private final ActionToolbar favToolbar;
    private final Document document;

    protected NewFavoriteCatalog(Document document, AnAction anAction, DefaultActionGroup favoriteActionGroup, ActionToolbar favToolbar) {
        super(null);
        this.document = document;
        this.anAction = anAction;
        this.favoriteActionGroup = favoriteActionGroup;
        this.favToolbar = favToolbar;
        init();
        setTitle("New Favorite Query");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Name of the favorite  query: ");
        panel.add(nameLabel, gbc);

        gbc.gridy = 1;
        textField = new JTextField(20);
        panel.add(textField, gbc);

        gbc.gridy = 2;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.decode("#FF4444"));
        panel.add(errorLabel, gbc);

        return panel;
    }

    @Override
    protected void doOKAction() {

        FavoriteQueries fav = FavoriteQueryStorage.getInstance().getValue();

        if (textField.getText().trim().isEmpty()) {
            errorLabel.setText("The name of the favorite query is required.");
            return;
        }

        if (document.getText().trim().isEmpty()) {
            errorLabel.setText("The query that you want to favorite can't be empty");
            return;
        }

        if (fav.getList().stream().anyMatch(e -> e.getName().equalsIgnoreCase(textField.getText()))) {
            errorLabel.setText("This name already exists");
            return;
        }

        FavoriteQueryStorage.getInstance().getValue().getList().add(new FavoriteQuery(textField.getText(),
                document.getText()));

        SwingUtilities.invokeLater(() -> {
            AnAction updatedAction = new AnAction("Favorite Query", "Favorite query", IconLoader.findIcon("./assets/icons/star-filled.svg")) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    //do nothing
                }
            };
            favoriteActionGroup.remove(anAction);
            favoriteActionGroup.add(updatedAction);
            Container parentContainer = favToolbar.getComponent().getParent();
            parentContainer.revalidate();
            DocumentListener documentListener = new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    document.removeDocumentListener(this);

                    AnAction newAction = new AnAction("Favorite Query", "Favorite query", IconLoader.findIcon("./assets/icons/star-empty.svg")) {
                        @Override
                        public void actionPerformed(@NotNull AnActionEvent e) {
                            NewFavoriteCatalog dialog = new NewFavoriteCatalog(document, this, favoriteActionGroup, favToolbar);
                            dialog.show();
                        }
                    };

                    favoriteActionGroup.remove(updatedAction);
                    favoriteActionGroup.add(newAction);
                    Container parentContainer = favToolbar.getComponent().getParent();
                    parentContainer.revalidate();
                }
            };
            document.addDocumentListener(documentListener);
        });
        super.doOKAction();
    }
}
