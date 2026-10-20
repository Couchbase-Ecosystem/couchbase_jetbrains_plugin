package com.couchbase.intellij.tree;

import com.couchbase.intellij.database.collections.FakeCollectionSource;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionSearchDialogTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    private static class Probe extends CollectionSearchDialog {
        Probe(FakeCollectionSource source, java.util.function.Consumer<String> onChosen) {
            super(null, source, onChosen);
        }

        String okText() {
            return (String) getOKAction().getValue(Action.NAME);
        }

        void clickOk() {
            doOKAction();
        }
    }

    @Test
    public void dialogHostsTheSearchPanelAndHasTheRightTitle() {
        Probe dialog = new Probe(new FakeCollectionSource("a"), n -> {
        });
        try {
            assertEquals("Search Collection", dialog.getTitle());
            JComponent center = dialog.getPreferredFocusedComponent();
            assertNotNull(center);
            assertEquals("collectionSearchField", center.getName());
            assertEquals("Add to List", dialog.okText());
        } finally {
            dialog.close(com.intellij.openapi.ui.DialogWrapper.CANCEL_EXIT_CODE);
        }
    }

    @Test
    public void okWithNothingHighlightedDoesNotChooseAnything() {
        List<String> chosen = new ArrayList<>();
        Probe dialog = new Probe(new FakeCollectionSource(), chosen::add);
        try {
            dialog.clickOk();
            assertTrue(chosen.isEmpty());
        } finally {
            dialog.close(com.intellij.openapi.ui.DialogWrapper.CANCEL_EXIT_CODE);
        }
    }
}
