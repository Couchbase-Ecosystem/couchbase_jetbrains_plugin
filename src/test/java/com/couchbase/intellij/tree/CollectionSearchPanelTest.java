package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.collections.FakeCollectionSource;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CollectionSearchPanelTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private final FakeCollectionSource source =
            new FakeCollectionSource("airline", "airport", "hotel", "landmark", "route");
    private final List<String> chosen = new ArrayList<>();

    private CollectionSearchPanel panel(Function<String, List<String>> searcher) {
        return new CollectionSearchPanel(searcher, chosen::add, Runnable::run, Runnable::run, 0);
    }

    private CollectionSearchPanel panel() {
        return panel(t -> source.search(t, 20));
    }

    private static <T extends Component> T find(Container c, Class<T> type, String name) {
        for (Component child : c.getComponents()) {
            if (type.isInstance(child) && name.equals(child.getName())) {
                return type.cast(child);
            }
            if (child instanceof Container) {
                T nested = find((Container) child, type, name);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private static void press(JComponent target, int keyCode) {
        KeyEvent e = new KeyEvent(target, KeyEvent.KEY_PRESSED, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED);
        for (java.awt.event.KeyListener l : target.getKeyListeners()) {
            l.keyPressed(e);
        }
    }

    @Test
    public void buildsAFieldAListAndAStatusLine() {
        CollectionSearchPanel p = panel();
        assertNotNull(find(p, JBTextField.class, "collectionSearchField"));
        assertNotNull(find(p, JBList.class, "collectionSearchResults"));
        assertNotNull(find(p, JLabel.class, "collectionSearchStatus"));
    }

    @Test
    public void typingFiltersTheListAsYouGo() {
        CollectionSearchPanel p = panel();

        p.setQuery("air");
        assertEquals(List.of("airline", "airport"), p.getResults());
        assertEquals("2 collections", p.getStatusText());

        p.setQuery("airl");
        assertEquals(List.of("airline"), p.getResults());
        assertEquals("1 collection", p.getStatusText());
    }

    @Test
    public void firstResultIsPreselected() {
        CollectionSearchPanel p = panel();
        p.setQuery("a");
        assertEquals("airline", p.getSelected());
    }

    @Test
    public void searchTextIsTrimmedAndCaseInsensitive() {
        CollectionSearchPanel p = panel();
        p.setQuery("  HOT ");
        assertEquals(List.of("hotel"), p.getResults());
    }

    @Test
    public void noMatchShowsMessageAndNothingIsSelected() {
        CollectionSearchPanel p = panel();
        p.setQuery("zzz");
        assertTrue(p.getResults().isEmpty());
        assertEquals("No collections match", p.getStatusText());
        assertNull(p.getSelected());
    }

    @Test
    public void clearingTheTextListsEverything() {
        CollectionSearchPanel p = panel();
        p.setQuery("hot");
        p.setQuery("");
        assertEquals(5, p.getResults().size());
    }

    @Test
    public void downAndUpArrowsMoveTheSelectionWithinBounds() {
        CollectionSearchPanel p = panel();
        p.setQuery("air");
        JBTextField field = find(p, JBTextField.class, "collectionSearchField");

        press(field, KeyEvent.VK_DOWN);
        assertEquals("airport", p.getSelected());
        press(field, KeyEvent.VK_DOWN);
        assertEquals("airport", p.getSelected());
        press(field, KeyEvent.VK_UP);
        press(field, KeyEvent.VK_UP);
        assertEquals("airline", p.getSelected());
    }

    @Test
    public void enterChoosesTheHighlightedCollection() {
        CollectionSearchPanel p = panel();
        p.setQuery("air");
        press(find(p, JBTextField.class, "collectionSearchField"), KeyEvent.VK_DOWN);
        press(find(p, JBTextField.class, "collectionSearchField"), KeyEvent.VK_ENTER);
        assertEquals(List.of("airport"), chosen);
    }

    @Test
    public void enterWithNoResultsChoosesNothing() {
        CollectionSearchPanel p = panel();
        p.setQuery("zzz");
        press(find(p, JBTextField.class, "collectionSearchField"), KeyEvent.VK_ENTER);
        assertTrue(chosen.isEmpty());
    }

    @Test
    public void doubleClickChoosesButSingleClickDoesNot() {
        CollectionSearchPanel p = panel();
        p.setQuery("ho");
        JBList<?> list = find(p, JBList.class, "collectionSearchResults");

        list.dispatchEvent(new MouseEvent(list, MouseEvent.MOUSE_CLICKED, 0, 0, 5, 5, 1, false));
        assertTrue(chosen.isEmpty());
        list.dispatchEvent(new MouseEvent(list, MouseEvent.MOUSE_CLICKED, 0, 0, 5, 5, 2, false));
        assertEquals(List.of("hotel"), chosen);
    }

    @Test
    public void staleResultsAreDroppedWhenTheUserKeptTyping() {
        List<Runnable> uiQueue = new ArrayList<>();
        CollectionSearchPanel p = new CollectionSearchPanel(t -> source.search(t, 20), chosen::add,
                Runnable::run, uiQueue::add, 0);

        p.setQuery("air");   // search #1 queued for the UI
        p.setQuery("hot");   // search #2 queued for the UI
        uiQueue.forEach(Runnable::run);

        assertEquals(List.of("hotel"), p.getResults());
    }

    @Test
    public void searchFailureIsReportedInsteadOfThrown() {
        CollectionSearchPanel p = panel(t -> {
            throw new IllegalStateException("cluster down");
        });
        p.setQuery("x");
        assertTrue(p.getResults().isEmpty());
        assertEquals("Search failed: cluster down", p.getStatusText());
    }

    @Test
    public void searchRunsOffTheCallingThread() {
        List<Runnable> backgroundQueue = new ArrayList<>();
        CollectionSearchPanel p = new CollectionSearchPanel(t -> source.search(t, 20), chosen::add,
                backgroundQueue::add, Runnable::run, 0);
        p.setQuery("air");

        assertEquals(0, source.searchCalls);
        assertEquals("Searching...", p.getStatusText());
        backgroundQueue.forEach(Runnable::run);
        assertEquals(1, source.searchCalls);
        assertEquals(2, p.getResults().size());
    }

    @Test
    public void focusStartsInTheTextField() {
        CollectionSearchPanel p = panel();
        assertSame(find(p, JBTextField.class, "collectionSearchField"), p.getFocusTarget());
    }

    @Test
    public void oldResultsAreRejectedWhileTheNextQueryIsStillDebouncing() {
        List<Runnable> uiQueue = new ArrayList<>();
        CollectionSearchPanel p = new CollectionSearchPanel(t -> source.search(t, 20), chosen::add,
                Runnable::run, uiQueue::add, 250);
        try {
            p.setQuery("air");
            p.runSearch();
            p.setQuery("hot");
            uiQueue.forEach(Runnable::run);
            assertTrue(p.getResults().isEmpty());
            assertNull(p.getSelected());
            assertEquals("Searching...", p.getStatusText());
        } finally {
            p.dispose();
        }
    }

    @Test
    public void editingTheFilterCannotAcceptThePreviousSelection() {
        CollectionSearchPanel p = new CollectionSearchPanel(t -> source.search(t, 20), chosen::add,
                Runnable::run, Runnable::run, 250);
        try {
            p.setQuery("air");
            p.runSearch();
            assertEquals("airline", p.getSelected());
            p.setQuery("hot");
            p.chooseSelected();
            assertTrue(chosen.isEmpty());
        } finally {
            p.dispose();
        }
    }

    @Test
    public void closingThePanelDiscardsPendingBackgroundResults() {
        List<Runnable> backgroundQueue = new ArrayList<>();
        CollectionSearchPanel p = new CollectionSearchPanel(t -> source.search(t, 20), chosen::add,
                backgroundQueue::add, Runnable::run, 0);
        p.setQuery("air");
        p.dispose();
        backgroundQueue.forEach(Runnable::run);
        assertTrue(p.getResults().isEmpty());
    }
}
