package com.couchbase.intellij.tree;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.ClusterUiTask;
import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.database.collections.CollectionSources;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.persistence.storage.FavoriteCollectionsStorage;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.couchbase.intellij.tools.CBFolders;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.Nullable;
import utils.JsonObjectUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class MermaidERDiagramDialog extends DialogWrapper {

    /** Entities drawn at most: favorites first, then the first alphabetical collections of the scope. */
    public static final int MAX_COLLECTIONS = 50;

    private final JBCefBrowser browser;
    private final String bucket;
    private final String scope;

    public MermaidERDiagramDialog(String bucket, String scope) {
        super(true); // use current window as parent
        this.bucket = bucket;
        this.scope = scope;
        setTitle("ER Diagram");
        browser = new JBCefBrowser();
        init();
        browser.loadHTML("<html><body>Loading chart...</body></html>");

        ActiveCluster active = ActiveCluster.getInstance();
        List<String> favorites = FavoriteCollectionsStorage.getInstance().getValue()
                .getFavorites(active.getId(), bucket, scope);
        // Schema inference runs per collection, so the chart is built in the background and capped.
        ClusterUiTask.submit(null, active, "Building ER diagram of " + bucket + "." + scope,
                connection -> generateChart(favorites), this::loadMermaidDiagram, error -> {
                    Log.error("Could not build the ER diagram of " + bucket + "." + scope, error);
                    browser.loadHTML("<html><body>Could not build the diagram: "
                            + StringUtil.escapeXmlEntities(String.valueOf(error.getMessage())) + "</body></html>");
                });
    }

    private void loadMermaidDiagram(Chart chart) {

        Log.debug(chart.definition);

        String scriptSrc = CBFolders.getInstance().getJsDependenciesPath() + File.separator + "mermaid.min.js";

        String notice = chart.shown < chart.total
                ? "<div style='font-family: sans-serif; padding: 6px;'>Showing " + chart.shown + " of " + chart.total
                + " collections (favorites first). Mark collections as favorites to include them.</div>"
                : "";

        String htmlContent = "<html><body>" +
                "<script src='" + scriptSrc + "'></script>" +
                notice +
                "<div id='loadingMessage'>Loading chart...</div>" +
                "<div id='mermaidChart' class='mermaid' style='visibility: hidden; position: absolute; cursor: grab;'>" +
                "erDiagram\n" +
                chart.definition +
                "</div>" +
                "<script>" +
                "mermaid.initialize({" +
                " startOnLoad:true" +
                ",theme: 'dark'" +
                "});" +
                " mermaid.init(undefined, '#mermaidChart', function() {" +
                "    var chartElement = document.getElementById('mermaidChart');" +
                "    chartElement.style.visibility = 'visible';" + // Make chart visible
                "    chartElement.style.position = 'static';" + // Reset position
                "    document.getElementById('loadingMessage').style.display = 'none';" + // Hide loading message
                "});" +
                "var element = document.getElementById('mermaidChart');" +
                "var scale = 1;" +
                "var translate = {x: 0, y: 0};" +
                "element.addEventListener('wheel', function(e) {" +
                "    e.preventDefault();" +
                "    var xs = (e.clientX - translate.x) / scale," +
                "        ys = (e.clientY - translate.y) / scale," +
                "        delta = (e.deltaY > 0 ? 0.9 : 1.1);" +
                "    scale *= delta;" +
                "    translate.x -= xs * (delta - 1) * scale;" +
                "    translate.y -= ys * (delta - 1) * scale;" +
                "    element.style.transform = 'translate(' + translate.x + 'px, ' + translate.y + 'px) scale(' + scale + ')';" +
                "});" +
                "element.addEventListener('mousedown', function(e) {" +
                "    e.preventDefault();" +
                "    element.style.cursor = 'grabbing';" +
                "    var posX = e.clientX, posY = e.clientY;" +
                "    function mouseMoveHandler(e) {" +
                "        translate.x += e.clientX - posX;" +
                "        translate.y += e.clientY - posY;" +
                "        element.style.transform = 'translate(' + translate.x + 'px, ' + translate.y + 'px) scale(' + scale + ')';" +
                "        posX = e.clientX; posY = e.clientY;" +
                "    }" +
                "    function mouseUpHandler() {" +
                "        element.style.cursor = 'grab';" +
                "        document.removeEventListener('mousemove', mouseMoveHandler);" +
                "        document.removeEventListener('mouseup', mouseUpHandler);" +
                "    }" +
                "    document.addEventListener('mousemove', mouseMoveHandler);" +
                "    document.addEventListener('mouseup', mouseUpHandler);" +
                "});" +
                "</script>" +
                "</body></html>";
        browser.loadHTML(htmlContent);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(browser.getComponent(), BorderLayout.CENTER);
        return panel;
    }

    /** The mermaid definition plus how many of the scope's collections it covers. */
    record Chart(String definition, int shown, int total) {
    }

    /** Runs off the EDT: infers the schema of every drawn collection that is not cached yet. */
    private Chart generateChart(List<String> favorites) {

        StringBuilder chartDef = new StringBuilder();

        CollectionListing listing = CollectionListing.build(CollectionSources.forScope(bucket, scope), favorites,
                List.of(), MAX_COLLECTIONS);

        Map<String, String> rels = RelationshipStorage.getInstance().getValue().getRelationships()
                .getOrDefault(ActiveCluster.getInstance().getId(), Map.of());
        String scopePrefix = bucket + "." + scope + ".";

        for (String name : listing.getNames()) {

            Optional<JsonObject> obj = ActiveCluster.getInstance().getChild(bucket)
                    .flatMap(bucket -> bucket.getChild(scope))
                    .flatMap(scope -> scope.getChild(name))
                    .map(col -> ((CouchbaseCollection) col).generateDocument())
                    .filter(Objects::nonNull)
                    .findFirst();

            if (obj.isPresent()) {
                Map<String, String> map = JsonObjectUtil.generatePaths(obj.get(), "");

                StringBuilder sb = new StringBuilder(name + " { \n");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey()
                            .replaceAll("\\*", "")
                            .replaceAll("\\s+", "_")
                            .replaceAll("[^a-zA-Z0-9_\\.\\[\\]]", "");
                    sb.append(entry.getValue() + " " + key + "\n");
                }
                sb.append("}\n");

                String prefix = scopePrefix + name + ".";
                for (Map.Entry<String, String> entry : rels.entrySet()) {
                    if (entry.getKey().startsWith(prefix)) {
                        String fieldRef = entry.getKey().replace(prefix, "") + " = "
                                + entry.getValue().replace(scopePrefix + entry.getValue().split("\\.")[2] + ".", "");
                        sb.append(name + " }o--|| " +
                                entry.getValue().replace(scopePrefix, "")
                                        .split("\\.")[0] + " : " + "\"" + fieldRef + "\"\n");
                    }
                }

                chartDef.append(sb);
            }
        }

        return new Chart(chartDef.toString(), listing.getNames().size(), listing.getTotal());
    }

}
