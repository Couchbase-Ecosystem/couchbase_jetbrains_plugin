package com.couchbase.intellij.tree.overview;

import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.couchbase.intellij.tree.overview.apis.IndexStats;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static utils.TemplateUtil.*;

public class IndexOverviewDialog extends DialogWrapper {

    private final String bucket;
    private final String scope;
    private final String collection;
    private final String indexName;

    public IndexOverviewDialog(Project project, String bucket, String scope, String collection, String indexName) {
        super(project);
        this.bucket = bucket;
        this.scope = scope;
        this.collection = collection;
        this.indexName = indexName;

        getPeer().getWindow().setMinimumSize(new Dimension(700, 620));
        getPeer().getWindow().setMaximumSize(new Dimension(700, 620));
        setResizable(true);
        init();
        setTitle("Stats for " + indexName);
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(JBUI.Borders.empty(10, 15));

        try {
            Map<String, IndexStats> indexStats = CouchbaseRestAPI.getIndexStats(bucket, scope, collection, indexName);
            if (indexStats.isEmpty()) {
                throw new IllegalStateException("Indexer Stats or the Index Stats is 0");
            }

            for (Map.Entry<String, IndexStats> idx : indexStats.entrySet()) {
                panel.add(getSeparator("Stats for " + idx.getKey()));
                String[] idxStats = {"# Doc Ids", "Avg Drain Rate", "Avg Item Size", "Avg Scan Latency", "% Cache Hit", "Cache Hits", "Cache Misses",
                        "Data Size", "Disk Size", "% Fragmentation", "Initial Build Progress", "Items Count", "Last Known Scan Time",
                        "# Docs Indexed", "# Docs Pending", "# Docs Queued", "# Items Flushed", "# Pending Requests", "# Requests",
                        "# Rows Returned", "# Scan Errors", "# Scan Timeouts", "# Records in Memory", "# Records on Disk", "% Resident",
                        "Scan Read", "Total Scan Duration", "Avg Array Length"

                };
                String[] idxValues = {idx.getValue().getDocid_count(), idx.getValue().getAvg_drain_rate(), idx.getValue().getAvg_item_size(), idx.getValue().getAvg_scan_latency(),
                        idx.getValue().getCache_hit_percent(), idx.getValue().getCache_hits(), idx.getValue().getCache_misses(),
                        fmtByte(idx.getValue().getData_size()), fmtByte(idx.getValue().getDisk_size()), fmtDouble(idx.getValue().getFrag_percent()),
                        idx.getValue().getInitial_build_progress(), idx.getValue().getItems_count(), idx.getValue().getLast_known_scan_time(),
                        idx.getValue().getNum_docs_indexed(), idx.getValue().getNum_docs_pending(), idx.getValue().getNum_docs_queued(),
                        idx.getValue().getNum_items_flushed(), idx.getValue().getNum_pending_requests(), idx.getValue().getNum_requests(),
                        idx.getValue().getNum_rows_returned(), idx.getValue().getNum_scan_errors(), idx.getValue().getNum_scan_timeouts(),
                        idx.getValue().getRecs_in_mem(), idx.getValue().getRecs_on_disk(), fmtDouble(idx.getValue().getResident_percent()),
                        fmtByte(idx.getValue().getScan_bytes_read()), idx.getValue().getTotal_scan_duration(), idx.getValue().getAvg_array_length()

                };
                JPanel idxPanel = createKeyValuePanel(idxStats, idxValues, 2);
                panel.add(idxPanel);
            }


        } catch (Exception e) {
            panel.add(new JLabel("An error occurred while trying to get the stats of the index" + indexName));
            e.printStackTrace();
            Log.error("An error occurred while trying to get the stats of the index" + indexName, e);
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JBScrollPane(panel), BorderLayout.CENTER);
        return mainPanel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }
}
