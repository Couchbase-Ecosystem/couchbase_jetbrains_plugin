package com.couchbase.intellij.workbench;

import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class QueryHistoryNavigatorTest {
    @Test public void emptyHistoryHasNoNavigationAndKeepsTheDraft() {
        var history = new QueryHistoryNavigator(List.of(), "draft");
        assertEquals("No history", history.label());
        assertFalse(history.canPrevious()); assertFalse(history.canNext());
        assertEquals("draft", history.previous("draft"));
        assertEquals("draft", history.next("draft"));
    }

    @Test public void browsingBothBoundariesRestoresTheOriginalDraft() {
        var history = new QueryHistoryNavigator(List.of("oldest", "newest"), "draft");
        assertEquals("History (2)", history.label());
        assertEquals("newest", history.previous("draft"));
        assertEquals("History (2/2)", history.label());
        assertEquals("oldest", history.previous("newest"));
        assertFalse(history.canPrevious());
        assertEquals("oldest", history.previous("oldest"));
        assertEquals("newest", history.next("oldest"));
        assertEquals("draft", history.next("newest"));
        assertFalse(history.canNext());
        assertEquals("History (2)", history.label());
    }

    @Test public void editsToRecalledQueriesSurviveNavigationWithoutChangingSavedHistory() {
        List<String> saved = List.of("first", "second");
        var history = new QueryHistoryNavigator(saved, "draft");
        history.previous("draft"); history.previous("edited second");
        assertEquals("edited second", history.next("edited first"));
        assertEquals("draft", history.next("edited second"));
        assertEquals("edited second", history.previous("new draft"));
        assertEquals("edited first", history.previous("edited second"));
        history.next("edited first");
        assertEquals("new draft", history.next("edited second"));
        assertEquals(List.of("first", "second"), saved);
    }

    @Test public void sharedHistoryUpdatesPreserveRecalledEditsAndTheDraft() {
        var history = new QueryHistoryNavigator(List.of("first", "second"), "draft");
        history.previous("draft");
        history.refresh(List.of("second", "third"), "edited second");
        assertEquals("History (1/2)", history.label());
        assertEquals("third", history.next("edited second"));
        assertEquals("draft", history.next("third"));
        assertEquals("third", history.previous("draft"));
        assertEquals("edited second", history.previous("third"));
    }

    @Test public void anEvictedQueryRemainsBrowsableUntilReturningToTheDraft() {
        var history = new QueryHistoryNavigator(List.of("evicted"), "draft");
        history.previous("draft");
        history.refresh(List.of("latest"), "edited old query");
        assertEquals("draft", history.next("edited old query"));
        history.refresh(List.of("latest"), "draft");
        assertEquals("latest", history.previous("draft"));
    }

    @Test public void refreshingAtTheDraftKeepsRecalledEditsAndAddsOtherEditorsQueries() {
        var history = new QueryHistoryNavigator(List.of("first"), "draft");
        history.previous("draft"); history.next("edited first");
        history.refresh(List.of("first"), "draft");
        assertEquals("edited first", history.previous("draft"));
        history.next("edited first");
        history.refresh(List.of("first", "newest"), "updated draft");
        assertEquals("newest", history.previous("updated draft"));
        assertEquals("edited first", history.previous("newest"));
        history.next("edited first");
        assertEquals("updated draft", history.next("newest"));
    }

    @Test public void executionFindsTheRecalledEntryAfterAnotherEditorChangesHistory() {
        var history = new QueryHistoryNavigator(List.of("old", "query"), "draft");
        history.previous("draft");
        assertEquals(0, history.executionIndex(List.of("query", "new")));
        assertEquals(1, history.executionIndex(List.of("new")));
        history.next("query");
        assertEquals(2, history.executionIndex(List.of("query", "new")));
    }
}
