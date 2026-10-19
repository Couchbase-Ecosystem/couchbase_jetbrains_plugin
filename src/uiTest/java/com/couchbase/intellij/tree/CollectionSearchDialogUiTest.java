package com.couchbase.intellij.tree;

import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.collections.ManifestCollectionSource;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.junit.Test;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollectionSearchDialogUiTest extends DialogUiTestCase {
    private final List<String> chosen = new CopyOnWriteArrayList<>();

    private DialogFixture searchDialog() {
        return showDialog(() -> new CollectionSearchDialog(getProject(), new ManifestCollectionSource(
                () -> ScopeSpec.create("travel", java.util.Set.of(
                        CollectionSpec.create("airline", "travel"),
                        CollectionSpec.create("airport", "travel"),
                        CollectionSpec.create("hotel", "travel")))), chosen::add));
    }

    private void resultsAre(DialogFixture dialog, String... expected) {
        await("filtered collections", () -> GuiActionRunner.execute(() ->
                java.util.Arrays.equals(expected, dialog.list("collectionSearchResults").contents())));
    }

    @Test
    public void typingAndKeyboardSelectionChooseOneCollection() {
        DialogFixture dialog = searchDialog();
        resultsAre(dialog, "airline", "airport", "hotel");
        dialog.textBox("collectionSearchField").click().enterText("air");
        resultsAre(dialog, "airline", "airport");
        dialog.label("collectionSearchStatus").requireText("2 collections");
        dialog.textBox("collectionSearchField").pressAndReleaseKeys(KeyEvent.VK_DOWN, KeyEvent.VK_ENTER);
        await("selection closes dialog", () -> chosen.equals(List.of("airport")));
        dialog.requireNotVisible();
    }

    @Test
    public void noMatchesCannotBeAcceptedAndClearingTheFilterRecovers() {
        DialogFixture dialog = searchDialog();
        dialog.textBox("collectionSearchField").click().enterText("does-not-exist");
        await("no matches message", () -> GuiActionRunner.execute(() ->
                "No collections match".equals(dialog.label("collectionSearchStatus").text())));
        dialog.list("collectionSearchResults").requireItemCount(0);
        dialog.button(JButtonMatcher.withText("Add to List")).click();
        dialog.requireVisible();
        assertTrue(chosen.isEmpty());
        dialog.textBox("collectionSearchField").deleteText().enterText("HOT");
        resultsAre(dialog, "hotel");
        dialog.button(JButtonMatcher.withText("Add to List")).click();
        assertEquals(List.of("hotel"), chosen);
        dialog.requireNotVisible();
    }

    @Test
    public void escapeCancelsWithoutChoosingThePreselectedResult() {
        DialogFixture dialog = searchDialog();
        resultsAre(dialog, "airline", "airport", "hotel");
        dialog.textBox("collectionSearchField").click().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
        dialog.requireNotVisible();
        assertTrue(chosen.isEmpty());
    }
}
