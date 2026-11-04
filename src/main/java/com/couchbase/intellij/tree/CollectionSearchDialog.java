package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.collections.CollectionSource;
import com.couchbase.intellij.database.collections.CachedCollectionSearch;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.function.Consumer;

/** Search-as-you-type picker for the collections of one scope. */
public class CollectionSearchDialog extends DialogWrapper {

    public static final int MAX_RESULTS = 20;

    private final CollectionSearchPanel panel;

    public CollectionSearchDialog(@Nullable Project project, CollectionSource source, Consumer<String> onChosen) {
        super(project);
        setTitle("Search Collection");
        setOKButtonText("Add to List");
        CachedCollectionSearch search = new CachedCollectionSearch(source);
        panel = new CollectionSearchPanel(text -> search.search(text, MAX_RESULTS + 1), name -> {
            onChosen.accept(name);
            close(OK_EXIT_CODE);
        }, ApplicationManager.getApplication()::executeOnPooledThread, SwingUtilities::invokeLater, 250);
        init();
        Disposer.register(getDisposable(), panel::dispose);
    }

    @Override
    protected void doOKAction() {
        if (panel.getSelected() != null) {
            panel.chooseSelected();
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return panel.getFocusTarget();
    }
}
