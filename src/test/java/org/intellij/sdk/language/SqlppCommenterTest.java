package org.intellij.sdk.language;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SqlppCommenterTest {

    private final SqlppCommenter commenter = new SqlppCommenter();

    @Test
    public void testLineCommentPrefixIsHash() {
        assertEquals("#", commenter.getLineCommentPrefix());
    }

    @Test
    public void testBlockCommentPrefixIsEmptyString() {
        // NOTE: possible bug: block comment prefix is "" rather than null, so block-comment toggling would insert empty markers.
        assertEquals("", commenter.getBlockCommentPrefix());
    }

    @Test
    public void testBlockCommentSuffixAndCommentedMarkersAreNull() {
        assertNull(commenter.getBlockCommentSuffix());
        assertNull(commenter.getCommentedBlockCommentPrefix());
        assertNull(commenter.getCommentedBlockCommentSuffix());
    }
}
