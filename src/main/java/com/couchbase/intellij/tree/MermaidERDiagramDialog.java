package com.couchbase.intellij.tree;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.entity.CouchbaseCollection;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.couchbase.intellij.tools.CBFolders;
import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.Nullable;
import utils.JsonObjectUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class MermaidERDiagramDialog extends DialogWrapper {

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
        loadMermaidDiagram();
    }

    private void loadMermaidDiagram() {

        String chart = generateChart();
        Log.debug(chart);

        String scriptSrc = CBFolders.getInstance().getJsDependenciesPath() + File.separator + "mermaid.min.js";

        String htmlContent = "<html><body>" +
                "<script src='" + scriptSrc + "'></script>" +
                "<div id='loadingMessage'>Loading chart...</div>" +
                "<div id='mermaidChart' class='mermaid' style='visibility: hidden; position: absolute; cursor: grab;'>" +
                "erDiagram\n" +
                chart +
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

    private String generateChart() {

        StringBuilder chartDef = new StringBuilder();

        Optional<ScopeSpec> scopeOpt = ActiveCluster.getInstance().getCluster()
                .bucket(bucket).collections().getAllScopes()
                .stream().filter(e -> e.name().equals(scope)).findFirst();

        if (scopeOpt.isPresent()) {

            for (CollectionSpec colSpec : scopeOpt.get().collections()) {

                Optional<JsonObject> obj = ActiveCluster.getInstance().getChild(bucket)
                        .flatMap(bucket -> bucket.getChild(scope))
                        .flatMap(scope -> scope.getChild(colSpec.name()))
                        .map(col -> ((CouchbaseCollection) col).generateDocument())
                        .filter(Objects::nonNull)
                        .findFirst();

                if (obj.isPresent()) {
                    Map<String, String> map = JsonObjectUtil.generatePaths(obj.get(), "");

                    StringBuilder sb = new StringBuilder(colSpec.name() + " { \n");
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String key = entry.getKey()
                                .replaceAll("\\*", "")
                                .replaceAll("\\s+", "_")
                                .replaceAll("[^a-zA-Z0-9_\\.\\[\\]]", "");
                        sb.append(entry.getValue() + " " + key + "\n");
                    }
                    sb.append("}\n");

                    Map<String, String> rels = RelationshipStorage.getInstance().getValue().getRelationships()
                            .get(ActiveCluster.getInstance().getId());

                    String scopePrefix = bucket + "." + scope + ".";
                    String prefix = scopePrefix + colSpec.name() + ".";
                    for (Map.Entry<String, String> entry : rels.entrySet()) {
                        if (entry.getKey().startsWith(prefix)) {
                            String fieldRef = entry.getKey().replace(prefix, "") + " = "
                                    + entry.getValue().replace(scopePrefix + entry.getValue().split("\\.")[2] + ".", "");
                            sb.append(colSpec.name() + " }o--|| " +
                                    entry.getValue().replace(scopePrefix, "")
                                            .split("\\.")[0] + " : " + "\"" + fieldRef + "\"\n");
                        }
                    }

                    chartDef.append(sb);
                }
            }
        }

        return chartDef.toString();
    }

}