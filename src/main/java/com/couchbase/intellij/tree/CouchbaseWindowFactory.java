package com.couchbase.intellij.tree;

//import com.couchbase.intellij.tree.iq.ChatGPTToolWindow;

import com.couchbase.intellij.tree.cblite.CBLWindowContent;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.intellij.tree.iq.IQWindowContent;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


public class CouchbaseWindowFactory implements ToolWindowFactory {

    public static final String CLUSTER_CONTENT = "cluster";
    public static final String IQ_CONTENT = "iq";
    private static final Map<Project, Map<String, Content>> CONTENTS_BY_PROJECT = new WeakHashMap<>();

    private Content cbLite;
    private ToolWindow toolWindow;
    private Project project;


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.project = project;
        CouchbaseWindowContent couchbaseWindowContent = new CouchbaseWindowContent(project);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content explorer = contentFactory.createContent(couchbaseWindowContent, "Explorer", false);
        toolWindow.getContentManager().addContent(explorer);
        setContent(project, CLUSTER_CONTENT, explorer);

        try {
            cbLite = createCBLiteContent(project);
            toolWindow.getContentManager().addContent(cbLite);
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Failed to start the CBLite Plugin", e);
        }

//        ChatGPTToolWindow chatGPTToolWindow = new ChatGPTToolWindow(project);
//        Content chatGpt = contentFactory.createContent(chatGPTToolWindow.getContent(), "iQ", false);
//        chatGpt.setIcon(AllIcons.General.Add);
//        toolWindow.getContentManager().addContent(chatGpt);

        IQWindowContent iqWindowContent = new IQWindowContent(project);
        Content iq = contentFactory.createContent(iqWindowContent, "iQ ᴮᵉᵗᵃ", false);
        iq.setIcon(AllIcons.General.Tip);
        toolWindow.getContentManager().addContent(iq);
        setContent(project, IQ_CONTENT, iq);
    }

    protected void setContent(Project project, String key, Content content) {
        if (!CONTENTS_BY_PROJECT.containsKey(project)) {
            CONTENTS_BY_PROJECT.put(project, new HashMap<>());
        }
        CONTENTS_BY_PROJECT.get(project).put(key, content);
    }

    public static Content getContent(Project project, String key) {
        if (CONTENTS_BY_PROJECT.containsKey(project)) {
            return CONTENTS_BY_PROJECT.get(project).get(key);
        }
        return null;
    }

    private Content createCBLiteContent(Project project) throws Exception {
        CBLWindowContent CBLWindowContent = new CBLWindowContent(project);
        return ContentFactory.getInstance().createContent(CBLWindowContent, "CBLite", false);
    }


    //TODO: For now, CBLITE will always be on
//    public void toggleCouchbaseLite() {
//        if (cbLite == null) {
//            cbLite = createCBLiteContent(project);
//            toolWindow.getContentManager().addContent(cbLite);
//        } else {
//            toolWindow.getContentManager().removeContent(cbLite, true);
//            cbLite = null;
//        }
//    }
}
