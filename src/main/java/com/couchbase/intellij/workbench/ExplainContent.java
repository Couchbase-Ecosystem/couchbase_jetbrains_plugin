package com.couchbase.intellij.workbench;

import com.couchbase.intellij.tools.CBFolders;

import java.io.File;

public class ExplainContent {

    private static String content;

    public static String getContent(String explainResult) {

        if (content == null) {
            String explainPath = CBFolders.getInstance().getExplainPath() + File.separator;
            String styles = CBFolders.getInstance().getExplainPath() + File.separator + "styles" + File.separator;
            String scripts = explainPath + "scripts" + File.separator;
            String utils = scripts + "utils" + File.separator;
            String constants = scripts + "utils" + File.separator + "constants" + File.separator;
            content = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "  <head>\n" +
                    "    <script>\n" +
                    "      const mockedJson = CB_EXPLAIN_RESULT \n" +
                    "    </script>\n" +
                    "    <script src=\"" + explainPath + "main.js\"></script>\n" +
                    "    <script src=\"" + explainPath + "d3.v7.min.js\"></script>\n" +
                    "    <link rel=\"stylesheet\" type=\"text/css\" href=\"" + styles + "nodes.css\" />\n" +
                    "    <script src=\"" + constants + "orientation.js\"></script>\n" +
                    "    <script src=\"" + utils + "n1ql.js\"></script>\n" +
                    "    <script src=\"" + scripts + "analyzePlan.js\"></script>\n" +
                    "    <script src=\"" + scripts + "planNode.js\"></script>\n" +
                    "    <script src=\"" + scripts + "convertN1QLPlanToPlanNodes.js\"></script>\n" +
                    "    <script src=\"" + utils + "getNodeDetails.js\"></script>\n" +
                    "    <script src=\"" + utils + "getNodeName.js\"></script>\n" +
                    "    <script src=\"" + utils + "getNonChildFieldList.js\"></script>\n" +
                    "    <script src=\"" + utils + "getToolTip.js\"></script>\n" +
                    "    <script src=\"" + scripts + "makeSimpleTreeFromPlanNodes.js\"></script>\n" +
                    "    <script src=\"" + constants + "config.js\"></script>\n" +
                    "    <script src=\"" + utils + "getHeight.js\"></script>\n" +
                    "    <script src=\"" + utils + "getNodeTranslation.js\"></script>\n" +
                    "    <script src=\"" + utils + "getRootTranslation.js\"></script>\n" +
                    "    <script src=\"" + utils + "getWidth.js\"></script>\n" +
                    "    <script src=\"" + utils + "getLink.js\"></script>\n" +
                    "    <script src=\"" + utils + "removeAllToolTips.js\"></script>\n" +
                    "    <script src=\"" + utils + "makeToolTip.js\"></script>\n" +
                    "    <script src=\"" + utils + "removeAllToolTips.js\"></script>\n" +
                    "    <script src=\"" + scripts + "makeD3TreeFromSimpleTree.js\"></script>\n" +
                    "  </head>\n" +
                    "\n" +
                    "   <body style=\" background: #3c3f41\">\n" +
                    "    <script type=\"module\">\n" +
                    "      window.main(mockedJson, document.getElementById(\"explain-plan\"));\n" +
                    "    </script>\n" +
                    "\n" +
                    "    <div\n" +
                    "      id=\"explain-plan\"\n" +
                    "      style=\"\n" +
                    "        width: 95%;\n" +
                    "        height: 300px;\n" +
                    "        background: #3c3f41;\n" +
                    "        margin: 10px auto;\n" +
                    "      \"\n" +
                    "    ></div>\n" +
                    "  </body>";
        }
        return content.replace("CB_EXPLAIN_RESULT", explainResult);
    }
}
