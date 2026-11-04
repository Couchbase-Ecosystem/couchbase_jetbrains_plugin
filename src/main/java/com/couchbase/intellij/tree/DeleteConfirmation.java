package com.couchbase.intellij.tree;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Confirmations for destructive tree actions: Cancel is the default button, so Enter never deletes by accident. */
public final class DeleteConfirmation {
    /** Property holding whether document deletes still ask; false once "Don't ask again" was ticked. */
    public static final String ASK_BEFORE_DELETING_DOCUMENTS = "couchbase.confirm.delete.document";

    private DeleteConfirmation() {}

    /** @return {@link Messages#YES} to proceed, {@link Messages#NO} otherwise. */
    public static int ask(@Nullable Project project, String message, String title, String confirmText) {
        int choice = Messages.showDialog(project, message, title, new String[]{confirmText, Messages.getCancelButton()}, 1,
                Messages.getWarningIcon());
        return choice == 0 ? Messages.YES : Messages.NO;
    }

    /** Document deletes can be remembered: once "Don't ask again" is ticked they proceed without a prompt. */
    @SuppressWarnings("deprecation") // Messages.showDialog with a default button index only takes the DialogWrapper variant
    public static int askForDocument(@Nullable Project project, String id) {
        PropertiesComponent properties = PropertiesComponent.getInstance();
        if (!properties.getBoolean(ASK_BEFORE_DELETING_DOCUMENTS, true)) {
            return Messages.YES;
        }
        int choice = Messages.showDialog(project, "<html>Delete the document <strong>" + id + "</strong>?</html>", "Delete Document",
                new String[]{"Delete", Messages.getCancelButton()}, 1, Messages.getWarningIcon(), new DialogWrapper.DoNotAskOption.Adapter() {
                    @Override
                    public void rememberChoice(boolean isSelected, int exitCode) {
                        if (isSelected && exitCode == 0) {
                            properties.setValue(ASK_BEFORE_DELETING_DOCUMENTS, false, true);
                        }
                    }

                    @Override
                    public @NotNull String getDoNotShowMessage() {
                        return "Don't ask again when deleting documents";
                    }
                });
        return choice == 0 ? Messages.YES : Messages.NO;
    }
}
