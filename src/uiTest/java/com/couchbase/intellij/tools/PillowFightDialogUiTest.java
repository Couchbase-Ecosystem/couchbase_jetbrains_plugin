package com.couchbase.intellij.tools;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.dialog.CollapsiblePanel;
import com.couchbase.intellij.tree.DialogUiTestCase;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/** The real Pillow Fight dialog: collection loading while modal, and the Advanced Options layout. */
public class PillowFightDialogUiTest extends DialogUiTestCase {
    private ActiveCluster previous;
    /** Released by the test once the dialog is on screen; the scope manifest waits for it off the EDT. */
    private final CountDownLatch release = new CountDownLatch(1);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        previous = ActiveCluster.getInstance();
        Set<CollectionSpec> collections = new HashSet<>();
        IntStream.range(0, 25).forEach(i -> collections.add(CollectionSpec.create(String.format("col%02d", i), "inventory")));
        List<ScopeSpec> scopes = List.of(ScopeSpec.create("inventory", collections));
        Cluster sdk = Mockito.mock(Cluster.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sdk.buckets().getAllBuckets()).thenReturn(Map.of("travel-sample", Mockito.mock(BucketSettings.class)));
        Mockito.when(sdk.bucket("travel-sample").collections().getAllScopes()).thenAnswer(call -> {
            if (!SwingUtilities.isEventDispatchThread()) {
                assertTrue("manifest released", release.await(15, TimeUnit.SECONDS));
            }
            return scopes;
        });
        ActiveCluster active = Mockito.mock(ActiveCluster.class);
        Mockito.when(active.get()).thenReturn(sdk);
        Mockito.when(active.getId()).thenReturn("pillow-ui");
        ActiveCluster.setInstance(active);
    }

    @Override
    protected void tearDown() throws Exception {
        release.countDown();
        try {
            super.tearDown();
        } finally {
            ActiveCluster.setInstance(previous);
        }
    }

    private DialogFixture pillowFight() {
        return showDialog(() -> new PillowFightDialog(getProject()));
    }

    private void awaitCollections(DialogFixture dialog) {
        await("first page listed", () -> GuiActionRunner.execute(() -> dialog.comboBox("collectionPickerCombo").contents().length == 10));
    }

    @Test
    public void collectionsLoadedDuringConstructionArriveWhileTheDialogIsModal() {
        DialogFixture dialog = pillowFight();
        dialog.label("collectionPickerStatus").requireText("Loading collections...");
        dialog.comboBox("collectionPickerCombo").requireDisabled();

        release.countDown();

        awaitCollections(dialog);
        dialog.comboBox("collectionPickerCombo").requireEnabled().requireSelection("col00");
        dialog.button("collectionPickerSearch").requireEnabled();
        dialog.label("collectionPickerStatus").requireText("10 of 25 collections listed. Search for the others.");
    }

    @Test
    public void advancedOptionsExpandWithoutClippingAndCollapseBackToTheCompactSize() {
        release.countDown();
        DialogFixture dialog = pillowFight();
        awaitCollections(dialog);
        JDialog window = (JDialog) dialog.target();
        Dimension compact = GuiActionRunner.execute(() -> window.getSize());
        CollapsiblePanel advanced = robot.finder().findByType(window, CollapsiblePanel.class);

        dialog.label("collapsibleHeader").click();
        await("advanced options expanded", () -> GuiActionRunner.execute(advanced::contentIsEnabled));
        await("dialog grows", () -> GuiActionRunner.execute(() -> window.getHeight() > compact.height));
        java.util.concurrent.Callable<Integer> visibleLabelsInsideTheViewport = () -> {
            JViewport viewport = ((JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, advanced)).getViewport();
            int checked = 0;
            for (Component component : robot.finder().findAll(advanced, c -> c instanceof JLabel l && l.isShowing()
                    && l.getText() != null && l.getText().endsWith(": "))) {
                JLabel label = (JLabel) component;
                Rectangle bounds = SwingUtilities.convertRectangle(label.getParent(), label.getBounds(), viewport);
                assertTrue(label.getText() + " slid off the left edge", bounds.x >= 0);
                assertTrue(label.getText() + " is clipped on the right", bounds.x + bounds.width <= viewport.getWidth());
                checked++;
            }
            return checked;
        };
        int checked = GuiActionRunner.execute(visibleLabelsInsideTheViewport);
        assertTrue("advanced labels were visible", checked > 5);

        dialog.label("collapsibleHeader").click();
        await("dialog shrinks back to its compact size", () -> GuiActionRunner.execute(() ->
                Math.abs(window.getWidth() - compact.width) <= 4 && window.getHeight() <= compact.height + 4));
    }
}
