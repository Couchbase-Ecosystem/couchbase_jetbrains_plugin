package com.couchbase.intellij.tree.node;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;

public class NodeDescriptorsTest {

    @Test
    public void toStringReturnsText() {
        NodeDescriptor node = new TooltipNodeDescriptor("Bucket A", "tip");

        Assert.assertEquals("Bucket A", node.toString());
        Assert.assertEquals("Bucket A", node.getText());
    }

    @Test
    public void setTextChangesToString() {
        NodeDescriptor node = new TooltipNodeDescriptor("old", "tip");
        node.setText("new");

        Assert.assertEquals("new", node.toString());
    }

    @Test
    public void tooltipNodeTwoArgConstructorHasNoIcon() {
        TooltipNodeDescriptor node = new TooltipNodeDescriptor("t", "hover text");

        Assert.assertEquals("hover text", node.getTooltip());
        Assert.assertNull(node.getIcon());
    }

    @Test
    public void tooltipNodeThreeArgConstructorKeepsIcon() {
        Icon icon = new ImageIcon(new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB));
        TooltipNodeDescriptor node = new TooltipNodeDescriptor("t", "tip", icon);

        Assert.assertSame(icon, node.getIcon());
        node.setTooltip(null);
        Assert.assertNull(node.getTooltip());
    }

    @Test
    public void counterNodeWrapsCountInParentheses() {
        CounterNodeDescriptor node = new CounterNodeDescriptor("Scopes", null) {
        };

        node.setCounter("3");

        Assert.assertEquals("Scopes", node.toString());
        Assert.assertEquals("(3)", labelText(node));
    }

    @Test
    public void counterNodeShowsZeroAndHidesMissingCounts() {
        CounterNodeDescriptor node = new CounterNodeDescriptor("Collections", null) {
        };

        node.setCounter("0");
        Assert.assertEquals("(0)", labelText(node));

        node.setCounter("");
        Assert.assertEquals("", labelText(node));

        node.setCounter(null);
        Assert.assertEquals("", labelText(node));
    }

    @Test
    public void loadMoreNodeStoresOffsetAndScope() {
        LoadMoreNodeDescriptor node = new LoadMoreNodeDescriptor("bkt", "_default", "_default", 200);

        Assert.assertEquals("Load More", node.getText());
        Assert.assertEquals("bkt", node.getBucket());
        Assert.assertEquals("_default", node.getScope());
        Assert.assertEquals("_default", node.getCollection());
        Assert.assertEquals(200, node.getNewOffset());

        node.setNewOffset(0);
        Assert.assertEquals(0, node.getNewOffset());
    }

    @Test
    public void missingIndexNodeKeepsLocationAndHtmlText() {
        MissingIndexNodeDescriptor node = new MissingIndexNodeDescriptor("travel-sample", "inventory", "airport");

        Assert.assertEquals("travel-sample", node.getBucket());
        Assert.assertEquals("inventory", node.getScope());
        Assert.assertEquals("airport", node.getCollection());
        Assert.assertTrue(node.getText(), node.getText().contains("Index not found."));

        node.setCollection("route");
        Assert.assertEquals("route", node.getCollection());
    }

    private static String labelText(CounterNodeDescriptor node) {
        for (java.awt.Component c : node.getCounterPanel().getComponents()) {
            if (c instanceof JLabel label) {
                return label.getText();
            }
        }
        throw new AssertionError("no label in counter panel");
    }
}
