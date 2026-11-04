package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.collections.CollectionSource;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.junit.Test;
import org.mockito.Mockito;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CollectionSearchDialogUiTest extends DialogUiTestCase {
    private final List<String> chosen = new CopyOnWriteArrayList<>();
    private final AtomicInteger nameLoads = new AtomicInteger();

    private DialogFixture searchDialog() {
        CollectionSource source = Mockito.mock(CollectionSource.class);
        Mockito.when(source.page(0, Integer.MAX_VALUE)).thenAnswer(call -> {
            nameLoads.incrementAndGet();
            return List.of("airline", "airport", "hotel");
        });
        return showDialog(() -> new CollectionSearchDialog(getProject(), source, chosen::add));
    }

    private void resultsAre(DialogFixture dialog, String... expected) {
        await("filtered collections", () -> GuiActionRunner.execute(() ->
                java.util.Arrays.equals(expected, dialog.list("collectionSearchResults").contents())));
    }

    @Test
    public void typingAndKeyboardSelectionChooseOneCollection() {
        DialogFixture dialog = searchDialog();
        dialog.label("collectionSearchStatus").requireText("Type a collection name");
        dialog.list("collectionSearchResults").requireItemCount(0);
        assertEquals(0, nameLoads.get());
        dialog.textBox("collectionSearchField").click().enterText("air");
        resultsAre(dialog, "airline", "airport");
        dialog.label("collectionSearchStatus").requireText("2 collections");
        dialog.textBox("collectionSearchField").pressAndReleaseKeys(KeyEvent.VK_DOWN, KeyEvent.VK_ENTER);
        await("selection closes dialog", () -> chosen.equals(List.of("airport")));
        dialog.requireNotVisible();
        assertEquals(1, nameLoads.get());
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
        assertEquals(1, nameLoads.get());
    }

    @Test
    public void escapeCancelsWithoutChoosingThePreselectedResult() {
        DialogFixture dialog = searchDialog();
        dialog.textBox("collectionSearchField").click().enterText("air");
        resultsAre(dialog, "airline", "airport");
        dialog.textBox("collectionSearchField").click().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
        dialog.requireNotVisible();
        assertTrue(chosen.isEmpty());
    }

    @Test
    public void cancellingBeforeTypingDoesNotFetchCollectionNames() {
        DialogFixture dialog = searchDialog();
        dialog.textBox("collectionSearchField").click().pressAndReleaseKeys(KeyEvent.VK_ESCAPE);
        dialog.requireNotVisible();
        assertEquals(0, nameLoads.get());
        assertTrue(chosen.isEmpty());
    }
}
