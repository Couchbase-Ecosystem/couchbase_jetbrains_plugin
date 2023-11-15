package com.couchbase.intellij.tree;

//import com.couchbase.intellij.tree.iq.ChatGPTToolWindow;

import com.couchbase.intellij.tree.cblite.CBLWindowContent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;


public class CouchbaseWindowFactory implements ToolWindowFactory {

    public static final Key ACTIVE_CONTENT = Key.create("ActiveContent");

    public static final String CHATGPT_CONTENT_NAME = "ChatGPT";
    public static final String GPT35_TRUBO_CONTENT_NAME = "GPT-3.5-Turbo";
    public static final String ONLINE_CHATGPT_CONTENT_NAME = "Online ChatGPT";


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


        cbLite = createCBLiteContent(project);
        toolWindow.getContentManager().addContent(cbLite);

//        ChatGPTToolWindow chatGPTToolWindow = new ChatGPTToolWindow(project);
//        Content chatGpt = contentFactory.createContent(chatGPTToolWindow.getContent(), "iQ", false);
//        chatGpt.setIcon(AllIcons.General.Add);
//        toolWindow.getContentManager().addContent(chatGpt);

    }

    private Content createCBLiteContent(Project project) {
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
