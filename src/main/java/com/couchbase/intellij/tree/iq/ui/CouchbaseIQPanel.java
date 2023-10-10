package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.intellij.tree.iq.api.AuthResponse;
import com.couchbase.intellij.tree.iq.api.ChatCompletionResponse;
import com.couchbase.intellij.tree.iq.api.IQRestService;
import com.couchbase.intellij.tree.iq.api.OrganizationResponse;
import com.couchbase.intellij.tree.iq.message.ChatGPTBundle;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.ui.listener.SendListener;
import com.couchbase.intellij.tree.iq.util.HtmlUtil;
import com.couchbase.intellij.tree.node.MissingIndexFootNoteNodeDescriptor;
import com.couchbase.intellij.workbench.Log;
import com.intellij.find.SearchTextArea;
import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonUI;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.*;
import com.intellij.util.ui.JBUI;
import okhttp3.Call;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CouchbaseIQPanel {

    private final SearchTextArea searchTextArea;
    private final JButton button;
    private final JButton stopGenerating;
    private final MessageGroupComponent contentPanel;
    private final JProgressBar progressBar;
    private final OnePixelSplitter splitter;

    private final OnePixelSplitter mainSplitter;
    private final Project myProject;
    private JPanel actionPanel;
    private ExecutorService executorService;
    private Object requestHolder;

    private JPanel mainPanel;

    private boolean myIsChatGPTModel;

    private String jwt;
    private Map<String, String> orgs;
    private String username;
    private String password;

    private String orgId;


    public CouchbaseIQPanel(@NotNull Project project, boolean isChatGPTModel) {

        myIsChatGPTModel = isChatGPTModel;
        myProject = project;
        mainPanel = new JPanel(new BorderLayout());

        try {
            if (this.jwt == null) {
                AuthResponse auth = IQRestService.login("denis.rosa@couchbase.com", "umasenhaMUIT0complicada!");
                this.jwt = auth.getJwt();
                System.out.println(jwt);
            }
            //OrganizationResponse response = IQRestService.loadOrganizations(this.jwt);
            //this.orgId = response.getData().get(0).getData().getId();
            this.orgId = "68bc50f0-d13b-4838-859a-a4a7d43a59c9";

        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Could not authenticate with Dev");
        }
        SendListener listener = new SendListener(this);

        splitter = new OnePixelSplitter(true, .98f);
        splitter.setDividerWidth(2);
        String[] data = {"Chat 1", "Chat 2", "Chat 3", "Chat 4"};
        JBList<String> jbList = new JBList<>(data);
        jbList.setCellRenderer(new IconListRenderer());
        JScrollPane scrollPane = new JBScrollPane(jbList);
        JBPanel historyChat = new JBPanel(new BorderLayout());
        historyChat.add(scrollPane, BorderLayout.CENTER);

        searchTextArea = new SearchTextArea(new JBTextArea(), true);
        searchTextArea.getTextArea().addKeyListener(listener);
        searchTextArea.setMinimumSize(new Dimension(searchTextArea.getWidth(), 500));
        searchTextArea.setMultilineEnabled(OpenAISettingsState.getInstance().enableLineWarp);
        button = new JButton(ChatGPTBundle.message("ui.toolwindow.send"), IconLoader.getIcon("/icons/send.svg", CouchbaseIQPanel.class));
        button.addActionListener(e -> {

            SwingUtilities.invokeLater(() -> {
                try {

                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    String selectedText = "";
                    if (editor != null) {
                        SelectionModel selectionModel = editor.getSelectionModel();
                        selectedText = selectionModel.getSelectedText() != null? selectionModel.getSelectedText(): "" ;
                    }

                    MessageGroupComponent contentPanel = getContentPanel();

                    // Add the message component to container
                    MessageComponent question = new MessageComponent(searchTextArea.getTextArea().getText(), true);
                    MessageComponent answer = new MessageComponent("Waiting for response...", false);
                    contentPanel.add(question);
                    contentPanel.add(answer);
                    contentPanel.updateLayout();
                    contentPanel.scrollToBottom();
                    final String message = getSearchTextArea().getTextArea().getText();

                    getSearchTextArea().getTextArea().setText("");


                    // Request the server.
                    if (selectedText!= null && !"".equals(selectedText)) {
                        selectedText = "The user might ask about the following code: " + selectedText.replace("\"", "\\\"");
                    }

                    final String systemMessage = selectedText;

                    CompletableFuture.runAsync(() -> {
                        try {
                            ChatCompletionResponse response = IQRestService.sendIQMessage(orgId, jwt,
                                    systemMessage, message);


                            String html = HtmlUtil.md2html(response.getChoices().get(0).getMessage().getContent());
                            System.out.println(html);

                            SwingUtilities.invokeLater(() -> {
                                answer.setContent(html);
                                contentPanel.updateLayout();
                                contentPanel.scrollToBottom();
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
        button.setUI(new DarculaButtonUI());

        stopGenerating = new JButton("Stop", AllIcons.Actions.Suspend);
        stopGenerating.addActionListener(e -> {
            executorService.shutdownNow();
            aroundRequest(false);
            if (requestHolder instanceof EventSource) {
                ((EventSource) requestHolder).cancel();
            } else if (requestHolder instanceof Call) {
                ((Call) requestHolder).cancel();
            }
        });
        stopGenerating.setUI(new DarculaButtonUI());

        actionPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        progressBar.setVisible(false);

        ToggleHistoryChatAction toggleAction = new ToggleHistoryChatAction("Toggle HistoryChat", AllIcons.Actions.ListFiles);

        // Create ActionToolbar using the toggle action
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(toggleAction);
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("MyToolbar", actionGroup, false);
        actionToolbar.setTargetComponent(mainPanel);


        AnAction newCouchbaseIQChat = new AnAction("Clean Chat") {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Add connection action code here
                System.out.println("Create new Chat");
            }
        };
        newCouchbaseIQChat.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/clear.svg", CouchbaseIQPanel.class));
        DefaultActionGroup rightActionGroup = new DefaultActionGroup();
        rightActionGroup.add(newCouchbaseIQChat);
        ActionToolbar rightToolBar = ActionManager.getInstance().createActionToolbar("NewChatToolbar", rightActionGroup, false);
        rightToolBar.setTargetComponent(mainPanel);

        actionPanel.add(progressBar, BorderLayout.NORTH);
        actionPanel.add(searchTextArea, BorderLayout.CENTER);
        actionPanel.add(button, BorderLayout.EAST);
        contentPanel = new MessageGroupComponent(project, isChatGPTModel());

        JLabel centerLabel = new JLabel("Couchbase iQ Chat");
        centerLabel.setHorizontalAlignment(JLabel.CENTER);
        centerLabel.setBorder(new EmptyBorder(-5, 0, 0, 0)); // pushes the label up by 5 pixels


        JPanel chatTopBar = new JPanel(new BorderLayout());
        chatTopBar.add(actionToolbar.getComponent(), BorderLayout.WEST);
        chatTopBar.add(centerLabel, BorderLayout.CENTER);
        chatTopBar.add(rightToolBar.getComponent(), BorderLayout.EAST);


        JBPanel leftPanel = new JBPanel(new BorderLayout());
        leftPanel.add(chatTopBar, BorderLayout.NORTH);
        leftPanel.add(contentPanel, BorderLayout.CENTER);


        splitter.setFirstComponent(leftPanel);
        splitter.setSecondComponent(actionPanel);


        AnAction newChatLeftMenu = new AnAction("New Couchbase IQ Chat") {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Add connection action code here
                System.out.println("Create new Chat");
            }
        };
        newChatLeftMenu.getTemplatePresentation().setIcon(AllIcons.General.Add);

        AnAction deleteChatLeftMenu = new AnAction("Delete Couchbase IQ Conversation") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Add connection action code here
                System.out.println("Create new Chat");
            }
        };
        deleteChatLeftMenu.getTemplatePresentation().setIcon(AllIcons.General.Remove);

        AnAction editChatTitleLeftMenu = new AnAction("Edit Chat Title") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Add connection action code here
                System.out.println("Create new Chat");
            }
        };
        editChatTitleLeftMenu.getTemplatePresentation().setIcon(AllIcons.Actions.Edit);

        AnAction exportChatTitleLeftMenu = new AnAction("Export Chat") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // Add connection action code here
                System.out.println("Create new Chat");
            }
        };
        exportChatTitleLeftMenu.getTemplatePresentation().setIcon(AllIcons.ToolbarDecorator.Export);


        DefaultActionGroup leftPanelActionGroup = new DefaultActionGroup();
        leftPanelActionGroup.add(newCouchbaseIQChat);
        leftPanelActionGroup.add(deleteChatLeftMenu);
        leftPanelActionGroup.add(editChatTitleLeftMenu);
        leftPanelActionGroup.addSeparator();
        leftPanelActionGroup.add(exportChatTitleLeftMenu);

        ActionToolbar leftPanelToolbar = ActionManager.getInstance().createActionToolbar("LeftPanelToolbar", leftPanelActionGroup, true);
        leftPanelToolbar.setTargetComponent(mainPanel);

        JPanel chatHistoryPanel = new JPanel(new BorderLayout());
        chatHistoryPanel.add(leftPanelToolbar.getComponent(), BorderLayout.NORTH);
        chatHistoryPanel.add(historyChat, BorderLayout.CENTER);

        mainSplitter = new OnePixelSplitter(false, .2f);
        mainSplitter.setFirstComponent(chatHistoryPanel);
        mainSplitter.setSecondComponent(splitter);


        JBLabel notificationLabel = new JBLabel("Couchbase iQ is a beta feature. Don't forget to give us your feedback!", AllIcons.General.Beta, SwingConstants.LEFT);
        notificationLabel.setBorder(JBUI.Borders.empty(5));
        JPanel notificationPanel = new JPanel();
        notificationPanel.setBackground(JBColor.LIGHT_GRAY);
        notificationPanel.add(notificationLabel);
        notificationPanel.setBorder(JBUI.Borders.customLine(JBUI.CurrentTheme.ToolWindow.borderColor(), 0, 0, 1, 0));


        mainPanel.add(notificationPanel, BorderLayout.NORTH);
        mainPanel.add(mainSplitter, BorderLayout.CENTER);


    }

    public Project getProject() {
        return myProject;
    }

    public SearchTextArea getSearchTextArea() {
        return searchTextArea;
    }

    public MessageGroupComponent getContentPanel() {
        return contentPanel;
    }

    public JPanel init() {
        return mainPanel;
    }

    public JButton getButton() {
        return button;
    }

    public ExecutorService getExecutorService() {
        executorService = Executors.newFixedThreadPool(1);
        return executorService;
    }

    public void aroundRequest(boolean status) {
        progressBar.setIndeterminate(status);
        progressBar.setVisible(status);
        button.setEnabled(!status);
        if (status) {
            contentPanel.addScrollListener();
            actionPanel.remove(button);
            actionPanel.add(stopGenerating, BorderLayout.EAST);
        } else {
            contentPanel.removeScrollListener();
            actionPanel.remove(stopGenerating);
            actionPanel.add(button, BorderLayout.EAST);
        }
        actionPanel.updateUI();
    }

    public void setRequestHolder(Object eventSource) {
        this.requestHolder = eventSource;
    }

    public boolean isChatGPTModel() {
        return myIsChatGPTModel;
    }

    private class ToggleHistoryChatAction extends AnAction {
        ToggleHistoryChatAction(String text, Icon icon) {
            super(text, null, icon);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            boolean isHistoryChatVisible = mainSplitter.getFirstComponent().isVisible();
            mainSplitter.getFirstComponent().setVisible(!isHistoryChatVisible);

            // Adjust the splitter proportion based on visibility
            if (!isHistoryChatVisible) {
                mainSplitter.setProportion(.2f);
            } else {
                mainSplitter.setProportion(0f);
            }
        }
    }

    class IconListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            setIcon(AllIcons.General.Balloon);
            setText((String) value);

            setIconTextGap(JBUI.scale(5));

            return this;
        }
    }
}
