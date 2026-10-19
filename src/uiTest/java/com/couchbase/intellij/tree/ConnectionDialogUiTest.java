package com.couchbase.intellij.tree;

import com.intellij.ui.treeStructure.Tree;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.junit.Test;

public class ConnectionDialogUiTest extends DialogUiTestCase {
    private DialogFixture connectionDialog() {
        return showDialog(() -> new NewConnectionDialog(getProject(), new Tree(), null, null));
    }

    @Test
    public void certificateModeReplacesPasswordFieldsAndRequiresOnlyTheKeystore() {
        DialogFixture dialog = connectionDialog();
        dialog.comboBox("connectionAuthentication").selectItem("Client certificate");
        dialog.checkBox("connectionSsl").requireSelected().requireDisabled();
        assertFalse(GuiActionRunner.execute(() -> robot.finder().findByName(dialog.target(), "connectionUsername", javax.swing.text.JTextComponent.class, false).isShowing()));
        dialog.textBox("connectionKeyStore").requireVisible();
        dialog.button(JButtonMatcher.withText("Test Connection")).click();
        dialog.label("connectionError").requireText("<html>Name can't be empty<br>"
                + "Connection String can't be empty<br>Client keystore can't be empty</html>");
        dialog.comboBox("connectionAuthentication").selectItem("Username and password");
        dialog.textBox("connectionUsername").requireVisible();
        assertFalse(GuiActionRunner.execute(() -> robot.finder().findByName(dialog.target(), "connectionKeyStore", javax.swing.text.JTextComponent.class, false).isShowing()));
        dialog.checkBox("connectionSsl").requireEnabled();
    }

    @Test
    public void saveShowsAllRequiredFieldsAndAllowsCorrection() {
        DialogFixture dialog = connectionDialog();
        dialog.button(JButtonMatcher.withText("Save")).click();
        dialog.label("connectionError").requireText("<html>Name can't be empty<br>"
                + "Connection String can't be empty<br>Username can't be empty<br>Password can't be empty</html>");
        dialog.button(JButtonMatcher.withText("Save")).requireEnabled();
        dialog.textBox("connectionName").enterText("Local test");
        dialog.textBox("connectionHost").enterText("localhost");
        dialog.textBox("connectionUsername").enterText("Administrator");
        dialog.button(JButtonMatcher.withText("Save")).click();
        dialog.label("connectionError").requireText("<html>Password can't be empty</html>");
        dialog.requireVisible();
        dialog.button(JButtonMatcher.withText("Cancel")).click();
        dialog.requireNotVisible();
    }

    @Test
    public void testConnectionValidatesBeforeConnecting() {
        DialogFixture dialog = connectionDialog();
        dialog.button(JButtonMatcher.withText("Test Connection")).click();
        dialog.label("connectionError").requireText("<html>Name can't be empty<br>"
                + "Connection String can't be empty<br>Username can't be empty<br>Password can't be empty</html>");
        dialog.label("connectionStatus").requireText("");
        dialog.button(JButtonMatcher.withText("Test Connection")).requireEnabled();
    }

    @Test
    public void secureConnectionStringSelectsSslDuringValidation() {
        DialogFixture dialog = connectionDialog();
        dialog.textBox("connectionHost").enterText("couchbases://localhost");
        dialog.button(JButtonMatcher.withText("Test Connection")).click();
        dialog.checkBox("connectionSsl").requireSelected();
        dialog.textBox("connectionHost").requireText("localhost");
        dialog.requireVisible();
    }
}
