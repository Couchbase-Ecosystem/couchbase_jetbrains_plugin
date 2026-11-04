package com.couchbase.intellij.workbench;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import org.junit.Test;
import utils.ColorHelper;

public class ThemeColorsTest extends LightPlatformCodeInsightFixture4TestCase {

    static {
        com.couchbase.intellij.testutil.AllowedThreads.allowJdkAwtThreads();
    }

    @Test
    public void themeColoursAreCssHexValues() {
        assertTrue(ColorHelper.panelBackgroundHex(), ColorHelper.panelBackgroundHex().matches("#[0-9a-fA-F]{6}"));
        assertTrue(ColorHelper.labelForegroundHex(), ColorHelper.labelForegroundHex().matches("#[0-9a-fA-F]{6}"));
        assertFalse("text must be readable on the panel", ColorHelper.panelBackgroundHex().equalsIgnoreCase(ColorHelper.labelForegroundHex()));
    }
}
