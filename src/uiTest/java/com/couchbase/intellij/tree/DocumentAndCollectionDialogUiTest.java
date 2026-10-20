package com.couchbase.intellij.tree;

import com.intellij.ui.treeStructure.Tree;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.junit.Test;

public class DocumentAndCollectionDialogUiTest extends DialogUiTestCase {
    @Test
    public void invalidDocumentIdsKeepTheDialogOpen() {
        DialogFixture dialog = showDialog(() -> new OpenDocumentDialog(false, getProject(), null,
                "unused", "_default", "_default"));
        dialog.button(JButtonMatcher.withText("OK")).click();
        dialog.label("documentIdError").requireText("The id specified contains invalid characters.");
        dialog.textBox("documentId").enterText("x".repeat(251));
        dialog.button(JButtonMatcher.withText("OK")).click();
        dialog.label("documentIdError").requireText("The id specified contains invalid characters.");
        dialog.requireVisible();
    }

    @Test
    public void collectionNameValidationAllowsRetry() {
        DialogFixture dialog = showDialog(() -> new NewCollectionDialog(getProject(), "unused", "_default",
                null, new Tree()));
        dialog.button(JButtonMatcher.withText("OK")).click();
        dialog.label("newCollectionError").requireText("Please enter a name for the collection");
        dialog.textBox("newCollectionName").enterText("invalid name!");
        dialog.button(JButtonMatcher.withText("OK")).click();
        dialog.label("newCollectionError").requireText("Special characters are not allowed in collection creation");
        dialog.requireVisible();
    }
}
