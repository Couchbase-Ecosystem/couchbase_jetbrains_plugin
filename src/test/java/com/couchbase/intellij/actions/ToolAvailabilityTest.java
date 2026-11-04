package com.couchbase.intellij.actions;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.ToolStatus;
import com.intellij.openapi.actionSystem.Presentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ToolAvailabilityTest {

    private ActiveCluster previous;
    private ActiveCluster active;
    private ToolStatus previousStatus;

    @Before
    public void installMockCluster() {
        previous = ActiveCluster.getInstance();
        active = mock(ActiveCluster.class);
        ActiveCluster.setInstance(active);
        previousStatus = CBTools.getTool(CBTools.Type.CB_IMPORT).getStatus();
    }

    @After
    public void restore() {
        ActiveCluster.setInstance(previous);
        CBTools.getTool(CBTools.Type.CB_IMPORT).setStatus(previousStatus);
    }

    private Presentation update(boolean requiresWrite) {
        Presentation presentation = new Presentation("Data Import");
        ToolAvailability.update(presentation, CBTools.Type.CB_IMPORT, requiresWrite, "Import data");
        return presentation;
    }

    @Test
    public void toolStillDownloadingIsListedButDisabledWithAReason() {
        CBTools.getTool(CBTools.Type.CB_IMPORT).setStatus(ToolStatus.DOWNLOADING);
        Presentation p = update(true);
        assertFalse(p.isEnabled());
        assertTrue(p.getDescription(), p.getDescription().contains("cbimport is still being downloaded"));
    }

    @Test
    public void missingToolExplainsWhereItComesFrom() {
        CBTools.getTool(CBTools.Type.CB_IMPORT).setStatus(ToolStatus.NOT_AVAILABLE);
        Presentation p = update(false);
        assertFalse(p.isEnabled());
        assertTrue(p.getDescription(), p.getDescription().contains("cbimport is not installed"));
    }

    @Test
    public void availableToolIsEnabledWithItsOwnDescription() {
        CBTools.getTool(CBTools.Type.CB_IMPORT).setStatus(ToolStatus.AVAILABLE);
        Presentation p = update(true);
        assertTrue(p.isEnabled());
        assertEquals("Import data", p.getDescription());
    }

    @Test
    public void readOnlyConnectionsDisableOnlyWritingTools() {
        CBTools.getTool(CBTools.Type.CB_IMPORT).setStatus(ToolStatus.AVAILABLE);
        when(active.isReadOnlyMode()).thenReturn(true);
        assertFalse(update(true).isEnabled());
        assertEquals(ToolAvailability.READ_ONLY, update(true).getDescription());
        assertTrue(update(false).isEnabled());
    }

    @Test
    public void certificateConnectionsWinOverEveryOtherReason() {
        CBTools.getTool(CBTools.Type.CB_IMPORT).setStatus(ToolStatus.DOWNLOADING);
        when(active.usesCertificateAuthentication()).thenReturn(true);
        Presentation p = update(true);
        assertFalse(p.isEnabled());
        assertEquals(PasswordConnectionAction.REQUIRES_PASSWORD, p.getDescription());
    }
}
