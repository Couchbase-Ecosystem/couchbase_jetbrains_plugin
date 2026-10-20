package com.couchbase.intellij.workbench;

import com.couchbase.client.java.Cluster;
import com.couchbase.intellij.database.ActiveCluster;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import static org.junit.Assert.*;

public class ReadOnlyScriptTest {
    @Test public void everyStatementIsCheckedBeforeAnyTransactionOrSdkRequest() {
        ActiveCluster previous = ActiveCluster.getInstance();
        ActiveCluster active = Mockito.mock(ActiveCluster.class);
        Cluster sdk = Mockito.mock(Cluster.class);
        var output = Mockito.mock(QueryResultToolWindowFactory.class);
        Mockito.when(active.get()).thenReturn(sdk); Mockito.when(active.getCluster()).thenReturn(sdk);
        Mockito.when(active.isReadOnlyMode()).thenReturn(true);
        ActiveCluster.setInstance(active);
        try (var windows = Mockito.mockStatic(QueryResultUtil.class)) {
            windows.when(() -> QueryResultUtil.getOutputWindow(null)).thenReturn(output);
            for (String mutation : List.of("UPDATE`b` SET x=1", "DELETE FROM b", "INSERT INTO b VALUES ('k',{})",
                    "UPSERT INTO b VALUES ('k',{})", "CREATE INDEX i ON b(x)", "DROP INDEX b.i",
                    "ALTER INDEX b.i WITH {}", "GRANT query_select ON b TO u", "REVOKE query_select ON b FROM u", "BUILD INDEX ON b(i)")) {
                assertFalse(mutation, QueryExecutor.executeScript(new LinkedBlockingQueue<>(), QueryExecutor.QueryType.NORMAL,
                        null, List.of("SELECT 1 AS result", mutation), -1, null));
            }
            Mockito.verifyNoInteractions(sdk);
            Mockito.verify(output, Mockito.times(10)).updateQueryStats(Mockito.anyList(), Mockito.isNull(), Mockito.argThat(
                    error -> error.getErrors().getFirst().getMessage().contains("read-only")), Mockito.isNull(), Mockito.eq(false));
        } finally { ActiveCluster.setInstance(previous); }
    }
}
