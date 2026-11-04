package com.couchbase.intellij.tree;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TestDialog;
import com.intellij.openapi.ui.TestDialogManager;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.After;
import org.junit.Test;

public class DeleteConfirmationTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private TestDialog previous;

    private void answer(int option) {
        previous = TestDialogManager.setTestDialog(message -> option);
    }

    @After
    public void restore() {
        if (previous != null) TestDialogManager.setTestDialog(previous);
        PropertiesComponent.getInstance().unsetValue(DeleteConfirmation.ASK_BEFORE_DELETING_DOCUMENTS);
    }

    @Test
    public void theFirstButtonConfirmsAndTheSecondCancels() {
        answer(0);
        assertEquals(Messages.YES, DeleteConfirmation.ask(getProject(), "Drop it?", "Drop Index", "Drop"));
        answer(1);
        assertEquals(Messages.NO, DeleteConfirmation.ask(getProject(), "Drop it?", "Drop Index", "Drop"));
    }

    @Test
    public void documentDeletesAskUntilTheUserOptsOut() {
        answer(1);
        assertEquals(Messages.NO, DeleteConfirmation.askForDocument(getProject(), "airline_10"));
        answer(0);
        assertEquals(Messages.YES, DeleteConfirmation.askForDocument(getProject(), "airline_10"));

        PropertiesComponent.getInstance().setValue(DeleteConfirmation.ASK_BEFORE_DELETING_DOCUMENTS, false, true);
        previous = TestDialogManager.setTestDialog(message -> {
            throw new AssertionError("no dialog may be shown once the user opted out: " + message);
        });
        assertEquals(Messages.YES, DeleteConfirmation.askForDocument(getProject(), "airline_10"));
    }
}
