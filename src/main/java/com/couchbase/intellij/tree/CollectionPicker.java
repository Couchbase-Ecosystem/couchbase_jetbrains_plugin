package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.ClusterUiTask;
import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.database.collections.CollectionSource;
import com.couchbase.intellij.database.collections.CollectionSources;
import com.couchbase.intellij.persistence.storage.FavoriteCollectionsStorage;
import com.couchbase.intellij.workbench.Log;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Single-collection chooser for dialogs. A scope may hold thousands of collections, so the combo only offers the
 * favorites and the first alphabetical page; anything else is reached through the search button, which adds the
 * chosen name to the combo. Collection names are loaded in the background, never on the EDT.
 */
public class CollectionPicker extends JPanel {

    /** Runs {@code work} off the EDT and hands the result (or the failure) back on the EDT. */
    public interface Loader {
        <T> void load(String title, Callable<T> work, Consumer<T> onSuccess, Consumer<Exception> onFailure);
    }

    public interface SourceFactory {
        CollectionSource forScope(String bucket, String scope);
    }

    public interface FavoritesLookup {
        List<String> favorites(String bucket, String scope);
    }

    public interface SearchOpener {
        void open(CollectionSource source, Consumer<String> onChosen);
    }

    private final ComboBox<String> combo = new ComboBox<>();
    private final JButton searchButton = new JButton(AllIcons.Actions.Find);
    private final JBLabel status = new JBLabel("");
    private final SourceFactory sources;
    private final FavoritesLookup favorites;
    private final SearchOpener search;
    private final Loader loader;

    private CollectionSource source;
    private int generation;
    private boolean loading;
    private int total = -1;
    private String pendingSelection;
    private boolean autoSelectFirst = true;
    private Consumer<Exception> errorHandler;

    public CollectionPicker(SourceFactory sources, FavoritesLookup favorites, SearchOpener search, Loader loader) {
        super(new BorderLayout(0, 2));
        this.sources = sources;
        this.favorites = favorites;
        this.search = search;
        this.loader = loader;

        combo.setName("collectionPickerCombo");
        searchButton.setName("collectionPickerSearch");
        searchButton.setToolTipText("Search for a collection by name");
        searchButton.setMargin(JBUI.insets(2));
        searchButton.addActionListener(e -> openSearch());
        status.setName("collectionPickerStatus");
        status.setForeground(UIUtil.getContextHelpForeground());
        status.setFont(JBUI.Fonts.smallFont());

        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.add(combo, BorderLayout.CENTER);
        row.add(searchButton, BorderLayout.EAST);
        add(row, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        clear();
    }

    /** A picker bound to the active connection, its favorites and the standard search dialog. */
    public static CollectionPicker forActiveCluster(@Nullable Project project) {
        // Dialogs set the scope while they are still being built, so results are delivered under the modality
        // of whichever window ends up holding the picker, not the one current at submit time.
        CollectionPicker[] picker = new CollectionPicker[1];
        Loader loader = new Loader() {
            @Override
            public <T> void load(String title, Callable<T> work, Consumer<T> onSuccess, Consumer<Exception> onFailure) {
                ClusterUiTask.submit(project, ActiveCluster.getInstance(), title, connection -> work.call(),
                        onSuccess, onFailure, picker[0]);
            }
        };
        picker[0] = new CollectionPicker(CollectionSources::forScope,
                (bucket, scope) -> FavoriteCollectionsStorage.getInstance().getValue()
                        .getFavorites(ActiveCluster.getInstance().getId(), bucket, scope),
                (source, onChosen) -> new CollectionSearchDialog(project, source, onChosen).show(),
                loader);
        return picker[0];
    }

    /** Forgets the scope and empties the combo. */
    public void clear() {
        generation++;
        loading = false;
        source = null;
        total = -1;
        pendingSelection = null;
        combo.removeAllItems();
        combo.setEnabled(false);
        searchButton.setEnabled(false);
        status.setText("");
    }

    public void setScope(String bucket, String scope) {
        setScope(bucket, scope, null);
    }

    /**
     * Loads the favorites and the first page of {@code scope} in the background. {@code onLoaded} runs on the EDT
     * once the combo is filled, with the number of collections in the scope.
     */
    public void setScope(String bucket, String scope, @Nullable Consumer<Integer> onLoaded) {
        clear();
        if (bucket == null || scope == null) {
            return;
        }
        final int mine = generation;
        loading = true;
        status.setText("Loading collections...");
        loader.load("Listing collections of " + bucket + "." + scope, () -> {
            CollectionSource s = sources.forScope(bucket, scope);
            return new Loaded(s, CollectionListing.build(s, favorites.favorites(bucket, scope), List.of(),
                    CollectionListing.PAGE_SIZE));
        }, loaded -> {
            if (mine != generation) {
                return;
            }
            loading = false;
            source = loaded.source;
            total = loaded.listing.getTotal();
            combo.removeAllItems();
            for (String name : loaded.listing.getNames()) {
                combo.addItem(name);
            }
            combo.setEnabled(isEnabled());
            searchButton.setEnabled(isEnabled());
            if (pendingSelection != null) {
                String wanted = pendingSelection;
                pendingSelection = null;
                setSelectedCollection(wanted);
            } else if (combo.getItemCount() > 0 && autoSelectFirst) {
                combo.setSelectedIndex(0);
            } else {
                combo.setSelectedItem(null);
            }
            updateStatus();
            if (onLoaded != null) {
                onLoaded.accept(total);
            }
        }, error -> {
            if (mine != generation) {
                return;
            }
            loading = false;
            status.setText("Could not load collections: " + message(error));
            if (errorHandler != null) {
                errorHandler.accept(error);
            } else {
                Log.error("Could not list the collections of " + bucket + "." + scope, error);
            }
        });
    }

    private static String message(Exception error) {
        return error.getMessage() == null ? error.toString() : error.getMessage();
    }

    private void updateStatus() {
        if (total > combo.getItemCount()) {
            status.setText(combo.getItemCount() + " of " + total + " collections listed. Search for the others.");
        } else {
            status.setText("");
        }
    }

    private void openSearch() {
        if (source == null) {
            return;
        }
        search.open(source, this::setSelectedCollection);
    }

    /** Selects {@code name}, adding it to the combo if it is not one of the listed collections. */
    public void setSelectedCollection(@Nullable String name) {
        if (name == null) {
            combo.setSelectedItem(null);
            return;
        }
        if (loading) {
            pendingSelection = name;
            return;
        }
        if (indexOf(name) < 0) {
            combo.addItem(name);
            updateStatus();
        }
        combo.setSelectedItem(name);
    }

    private int indexOf(String name) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (name.equals(combo.getItemAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public @Nullable String getSelectedCollection() {
        Object selected = combo.getSelectedItem();
        return selected == null ? null : selected.toString();
    }

    /** Names currently offered by the combo, in display order. */
    public List<String> getItems() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < combo.getItemCount(); i++) {
            items.add(combo.getItemAt(i));
        }
        return items;
    }

    /** Number of collections in the scope, or -1 before the first load completes. */
    public int getTotal() {
        return total;
    }

    public boolean isLoading() {
        return loading;
    }

    /** Fired whenever the selected collection changes, including while the combo is being refilled. */
    public void addActionListener(ActionListener listener) {
        combo.addActionListener(listener);
    }

    public void setErrorHandler(@Nullable Consumer<Exception> errorHandler) {
        this.errorHandler = errorHandler;
    }

    /** Whether the first listed collection is selected once a scope loads (default) or the combo starts empty. */
    public void setAutoSelectFirst(boolean autoSelectFirst) {
        this.autoSelectFirst = autoSelectFirst;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        combo.setEnabled(enabled && !loading && source != null);
        searchButton.setEnabled(enabled && source != null);
    }

    public JComponent getFocusTarget() {
        return combo;
    }

    private record Loaded(CollectionSource source, CollectionListing listing) {
    }
}
