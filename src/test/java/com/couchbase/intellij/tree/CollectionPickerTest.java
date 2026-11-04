package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.collections.FakeCollectionSource;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.ui.components.JBLabel;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class CollectionPickerTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    /** Collects background work so a test decides when (and whether) it completes. */
    private static final class ManualLoader implements CollectionPicker.Loader {
        private final List<Runnable> pending = new ArrayList<>();

        @Override
        public <T> void load(String title, Callable<T> work, Consumer<T> onSuccess, Consumer<Exception> onFailure) {
            pending.add(() -> {
                try {
                    onSuccess.accept(work.call());
                } catch (Exception e) {
                    onFailure.accept(e);
                }
            });
        }

        void finishAll() {
            List<Runnable> copy = new ArrayList<>(pending);
            pending.clear();
            copy.forEach(Runnable::run);
        }
    }

    private final ManualLoader loader = new ManualLoader();
    private final List<String> searchedWith = new ArrayList<>();
    private Consumer<String> searchCallback;
    private FakeCollectionSource source = FakeCollectionSource.numbered(25);
    private List<String> favorites = List.of();

    private CollectionPicker picker() {
        return new CollectionPicker((bucket, scope) -> {
            if (source == null) throw new IllegalStateException("scope " + scope + " is gone");
            return source;
        }, (bucket, scope) -> favorites, (s, onChosen) -> {
            searchedWith.add(s == source ? "scope" : "other");
            searchCallback = onChosen;
        }, loader);
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

    @SuppressWarnings("unchecked")
    private static ComboBox<String> combo(CollectionPicker p) {
        return find(p, ComboBox.class, "collectionPickerCombo");
    }

    private static JButton searchButton(CollectionPicker p) {
        return find(p, JButton.class, "collectionPickerSearch");
    }

    private static String status(CollectionPicker p) {
        return find(p, JBLabel.class, "collectionPickerStatus").getText();
    }

    @Test
    public void startsDisabledUntilAScopeIsLoaded() {
        CollectionPicker p = picker();
        assertFalse(combo(p).isEnabled());
        assertFalse(searchButton(p).isEnabled());
        assertNull(p.getSelectedCollection());
        assertEquals(-1, p.getTotal());

        p.setScope("travel", "inventory");
        assertTrue(p.isLoading());
        assertEquals("Loading collections...", status(p));
        assertFalse(combo(p).isEnabled());

        loader.finishAll();
        assertFalse(p.isLoading());
        assertTrue(combo(p).isEnabled());
        assertTrue(searchButton(p).isEnabled());
    }

    @Test
    public void offersOnlyTheFirstPageAndSaysHowManyAreHidden() {
        CollectionPicker p = picker();
        p.setScope("travel", "inventory");
        loader.finishAll();

        assertEquals(10, p.getItems().size());
        assertEquals("col00", p.getItems().get(0));
        assertEquals("col09", p.getItems().get(9));
        assertEquals(25, p.getTotal());
        assertEquals("col00", p.getSelectedCollection());
        assertEquals("Showing 10 of 25 collections. Use the search button to pick another one.", status(p));
    }

    @Test
    public void favoritesComeFirstAndSmallScopesShowNoHint() {
        source = new FakeCollectionSource("airline", "airport", "hotel");
        favorites = List.of("hotel");
        CollectionPicker p = picker();
        p.setScope("travel", "inventory");
        loader.finishAll();

        assertEquals(List.of("hotel", "airline", "airport"), p.getItems());
        assertEquals("", status(p));
    }

    @Test
    public void searchAddsTheChosenCollectionToTheComboAndSelectsIt() {
        CollectionPicker p = picker();
        p.setScope("travel", "inventory");
        loader.finishAll();

        searchButton(p).doClick();
        assertEquals(List.of("scope"), searchedWith);
        searchCallback.accept("col20");

        assertEquals("col20", p.getSelectedCollection());
        assertEquals(11, p.getItems().size());
        assertEquals("col20", p.getItems().get(10));
        assertEquals("Showing 11 of 25 collections. Use the search button to pick another one.", status(p));

        // Choosing a collection that is already listed does not duplicate it.
        searchCallback.accept("col03");
        assertEquals("col03", p.getSelectedCollection());
        assertEquals(11, p.getItems().size());
    }

    @Test
    public void selectionRequestedWhileLoadingIsAppliedAfterwards() {
        CollectionPicker p = picker();
        p.setScope("travel", "inventory");
        p.setSelectedCollection("col17");
        assertNull(p.getSelectedCollection());

        loader.finishAll();
        assertEquals("col17", p.getSelectedCollection());
        assertTrue(p.getItems().contains("col17"));
    }

    @Test
    public void aNewerScopeWinsOverAnOlderLoadThatFinishesLate() {
        CollectionPicker p = picker();
        List<Integer> loadedTotals = new ArrayList<>();
        p.setScope("travel", "inventory", loadedTotals::add);
        source = new FakeCollectionSource("only");
        p.setScope("travel", "other", loadedTotals::add);

        // Both loads complete; the first one must be ignored even though it runs first.
        loader.finishAll();
        assertEquals(List.of("only"), p.getItems());
        assertEquals(List.of(1), loadedTotals);
    }

    @Test
    public void emptyScopeReportsZeroAndKeepsTheComboEmpty() {
        source = new FakeCollectionSource();
        CollectionPicker p = picker();
        List<Integer> loadedTotals = new ArrayList<>();
        p.setScope("travel", "inventory", loadedTotals::add);
        loader.finishAll();

        assertEquals(List.of(0), loadedTotals);
        assertTrue(p.getItems().isEmpty());
        assertNull(p.getSelectedCollection());

        p.setSelectedCollection("_default");
        assertEquals("_default", p.getSelectedCollection());
    }

    @Test
    public void loadFailureIsShownAndHandedToTheErrorHandler() {
        source = null;
        CollectionPicker p = picker();
        List<Exception> errors = new ArrayList<>();
        p.setErrorHandler(errors::add);
        p.setScope("travel", "inventory", total -> fail("must not report a total"));
        loader.finishAll();

        assertEquals(1, errors.size());
        assertEquals("Could not load collections: scope inventory is gone", status(p));
        assertFalse(p.isLoading());
        assertFalse(combo(p).isEnabled());
    }

    @Test
    public void autoSelectCanBeTurnedOff() {
        CollectionPicker p = picker();
        p.setAutoSelectFirst(false);
        p.setScope("travel", "inventory");
        loader.finishAll();

        assertEquals(10, p.getItems().size());
        assertNull(p.getSelectedCollection());
    }

    @Test
    public void clearForgetsTheScopeAndDisablesSearch() {
        CollectionPicker p = picker();
        p.setScope("travel", "inventory");
        loader.finishAll();
        p.clear();

        assertTrue(p.getItems().isEmpty());
        assertFalse(searchButton(p).isEnabled());
        searchButton(p).doClick();
        assertTrue(searchedWith.isEmpty());
    }

    @Test
    public void sourceIsOnlyAskedForOnePage() {
        CollectionPicker p = picker();
        p.setScope("travel", "inventory");
        loader.finishAll();
        assertEquals(1, source.pageCalls);
        assertEquals(0, source.searchCalls);
    }
}
