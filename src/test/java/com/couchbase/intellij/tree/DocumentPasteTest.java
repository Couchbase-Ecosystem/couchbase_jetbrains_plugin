package com.couchbase.intellij.tree;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DocumentPasteTest {

    /** An in-memory collection that records every write. */
    private static final class FakeTarget implements DocumentPaste.Target {
        final Map<String, String> documents = new LinkedHashMap<>();
        final List<String> log = new ArrayList<>();

        FakeTarget(String... existingIds) {
            for (String id : existingIds) documents.put(id, "{\"old\":true}");
        }

        @Override
        public boolean insert(String id, String content) {
            log.add("insert " + id);
            if (documents.containsKey(id)) return false;
            documents.put(id, content);
            return true;
        }

        @Override
        public void upsert(String id, String content) {
            log.add("upsert " + id);
            documents.put(id, content);
        }
    }

    private static DocumentPaste.Prompter answers(List<DocumentPaste.Conflict> conflicts, List<String> ids) {
        Iterator<DocumentPaste.Conflict> conflict = conflicts.iterator();
        Iterator<String> id = ids.iterator();
        return new DocumentPaste.Prompter() {
            @Override
            public DocumentPaste.Conflict onConflict(String current, String target) {
                assertTrue("asked more often than expected", conflict.hasNext());
                return conflict.next();
            }

            @Override
            public String askNewId(String current) {
                assertTrue("asked for an ID more often than expected", id.hasNext());
                return id.next();
            }
        };
    }

    private static final DocumentPaste.Prompter NEVER_ASKED = answers(List.of(), List.of());

    @Test
    public void aFreeIdIsInsertedWithoutAnyQuestion() {
        FakeTarget target = new FakeTarget();
        assertEquals("doc1", DocumentPaste.paste(target, "doc1", "{\"a\":1}", "t", NEVER_ASKED));
        assertEquals("{\"a\":1}", target.documents.get("doc1"));
        assertEquals(List.of("insert doc1"), target.log);
    }

    @Test
    public void replaceOverwritesTheExistingDocument() {
        FakeTarget target = new FakeTarget("doc1");
        assertEquals("doc1", DocumentPaste.paste(target, "doc1", "{\"new\":1}", "t",
                answers(List.of(DocumentPaste.Conflict.REPLACE), List.of())));
        assertEquals("{\"new\":1}", target.documents.get("doc1"));
        assertEquals(List.of("insert doc1", "upsert doc1"), target.log);
    }

    @Test
    public void changeIdPastesUnderTheNewIdAndKeepsTheOriginal() {
        FakeTarget target = new FakeTarget("doc1");
        assertEquals("doc1-copy", DocumentPaste.paste(target, "doc1", "{\"new\":1}", "t",
                answers(List.of(DocumentPaste.Conflict.RENAME), List.of("  doc1-copy "))));
        assertEquals("{\"old\":true}", target.documents.get("doc1"));
        assertEquals("{\"new\":1}", target.documents.get("doc1-copy"));
    }

    @Test
    public void aNewIdThatIsTakenTooIsAskedAboutAgain() {
        FakeTarget target = new FakeTarget("doc1", "doc2");
        assertEquals("doc3", DocumentPaste.paste(target, "doc1", "{}", "t",
                answers(List.of(DocumentPaste.Conflict.RENAME, DocumentPaste.Conflict.RENAME), List.of("doc2", "doc3"))));
        assertEquals(List.of("insert doc1", "insert doc2", "insert doc3"), target.log);
    }

    @Test
    public void cancellingOrLeavingTheIdBlankWritesNothing() {
        FakeTarget target = new FakeTarget("doc1");
        assertNull(DocumentPaste.paste(target, "doc1", "{}", "t", answers(List.of(DocumentPaste.Conflict.CANCEL), List.of())));
        assertNull(DocumentPaste.paste(target, "doc1", "{}", "t", answers(List.of(DocumentPaste.Conflict.RENAME), Collections.singletonList(null))));
        assertNull(DocumentPaste.paste(target, "doc1", "{}", "t", answers(List.of(DocumentPaste.Conflict.RENAME), List.of("   "))));
        assertEquals("{\"old\":true}", target.documents.get("doc1"));
        assertEquals(1, target.documents.size());
    }

    @Test
    public void qualifiedNamesAreBackticked() {
        assertEquals("`travel-sample`.`inventory`.`airline`", TreeCopy.qualifiedName("travel-sample", "inventory", "airline"));
    }
}
