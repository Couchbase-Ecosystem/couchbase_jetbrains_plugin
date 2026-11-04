package com.couchbase.intellij.workbench;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExplainContentTest {

    @Test
    public void thePageBackgroundFollowsTheGivenThemeColourAndPlansAreInlinedAsAnArray() {
        String template = "<body style=\"background: CB_BACKGROUND\"><div style=\"background: CB_BACKGROUND\"></div>"
                + "<script>const mockedJson = CB_EXPLAIN_RESULT</script>";

        String page = ExplainContent.render(template, List.of("{\"a\":1}", "{\"b\":2}"), "#f2f2f2");

        assertFalse("no dark hardcoded background may remain", page.contains("3c3f41"));
        assertFalse(page.contains("CB_BACKGROUND"));
        assertEquals(2, page.split("#f2f2f2", -1).length - 1);
        assertTrue(page.contains("const mockedJson = [{\"a\":1}, {\"b\":2}]"));
    }
}
