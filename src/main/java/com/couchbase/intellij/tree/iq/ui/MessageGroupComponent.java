package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.QueryContext;
import com.couchbase.intellij.persistence.storage.IQStorage;
import com.couchbase.intellij.tree.iq.CapellaOrganization;
import com.couchbase.intellij.tree.iq.CapellaOrganizationList;
import com.couchbase.intellij.tree.iq.IQWindowContent;
import com.couchbase.intellij.tree.iq.SystemMessageHolder;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.text.TextFragment;
import com.couchbase.intellij.tree.iq.util.ScrollingTools;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.NullableComponent;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.Gray;
import com.intellij.ui.HideableTitledPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.couchbase.intellij.workbench.CustomSqlFileEditor.NO_QUERY_CONTEXT_SELECTED;

public class MessageGroupComponent extends JBPanel<MessageGroupComponent> implements NullableComponent, SystemMessageHolder {
    private final JPanel myList = new JPanel(new VerticalLayout(0));
    private final JBScrollPane myScrollPane = new JBScrollPane(myList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    private final CapellaOrganization organization;
    private final ChatPanel.OrganizationListener orgChangeListener;
    private int myScrollValue = 0;
    private JComboBox<String> orgSelector;
    private final Project project;
    private final ChatPanel chat;

    public MessageGroupComponent(ChatPanel chat, @NotNull Project project, CapellaOrganizationList organizationList, CapellaOrganization organization, ChatPanel.OrganizationListener orgChangeListener, ChatPanel.LogoutListener logoutListener) {
        this.chat = chat;
        this.project = project;
        this.organization = organization;
        this.orgChangeListener = orgChangeListener;
        setBorder(JBUI.Borders.empty());
        setLayout(new BorderLayout());
        setBackground(UIUtil.getListBackground());

        myScrollPane.getVerticalScrollBar().putClientProperty(JBScrollPane.IGNORE_SCROLLBAR_IN_INSETS, Boolean.TRUE);
        ScrollingTools.installAutoScrollToBottom(myScrollPane);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(JBUI.Borders.emptyLeft(0));


        HideableTitledPanel cPanel = new HideableTitledPanel("Settings", false);
        cPanel.setContentComponent(createSettingsPanel(organizationList, logoutListener));
        cPanel.setOn(false);
        cPanel.setBorder(JBUI.Borders.empty(0, 8, 10, 0));
        add(cPanel, BorderLayout.NORTH);

        add(mainPanel, BorderLayout.CENTER);

        JBLabel myTitle = new JBLabel("Conversation");
        myTitle.setForeground(JBColor.namedColor("Label.infoForeground", new JBColor(Gray.x80, Gray.x8C)));
        myTitle.setFont(JBFont.label());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(JBUI.Borders.empty(0, 10, 10, 0));

        panel.add(myTitle, BorderLayout.WEST);

        DefaultActionGroup chatActions = new DefaultActionGroup();
        createContextSelectorAction(chatActions);
        chatActions.add(new AnAction(() -> "New chat", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                myList.removeAll();
                addAssistantTipsIfEnabled(false);
                myList.updateUI();
                chat.getChatLink().getConversationContext().clear();
            }
        });

        ActionToolbarImpl chatPanel = new ActionToolbarImpl("Chat Actions Toolbar", chatActions, true);
        chatPanel.setTargetComponent(this);
        panel.add(chatPanel, BorderLayout.EAST);
        mainPanel.add(panel, BorderLayout.NORTH);

        myList.setOpaque(true);
        myList.setBackground(UIUtil.getListBackground());
        myList.setBorder(JBUI.Borders.emptyRight(0));

        myScrollPane.setBorder(JBUI.Borders.empty());
        mainPanel.add(myScrollPane);
        myScrollPane.getVerticalScrollBar().setAutoscrolls(true);
        myScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            myScrollValue = e.getValue();
        });

        addAssistantTipsIfEnabled(true);
    }

    private void createContextSelectorAction(DefaultActionGroup group) {
        DefaultActionGroup placeholder = new DefaultActionGroup();
        group.add(placeholder);
        AtomicReference<DefaultActionGroup> oldContextAction = new AtomicReference<>(placeholder);
        ActiveCluster.INSTANCE.subscribe(this, optionalActiveCluster -> {
            if (optionalActiveCluster.isEmpty()) {
                if (oldContextAction.get() != null) {
                    group.remove(oldContextAction.get());
                    oldContextAction.set(null);
                }
                return true;
            }

            ActiveCluster activeCluster = optionalActiveCluster.get();
            AtomicReference<String> contextLabel = new AtomicReference<>(NO_QUERY_CONTEXT_SELECTED);
            DefaultActionGroup contextAction = new DefaultActionGroup(contextLabel::get, true) {
                @Override
                public boolean displayTextInToolbar() {
                    return true;
                }
            };

            AnAction clearContextAction = new AnAction("Clear context") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    IQWindowContent.clearClusterContext();
                }
            };

            activeCluster.getQueryContext().subscribe(this, queryContext -> {
                if (queryContext.isPresent()) {
                    QueryContext context = queryContext.get();
                    contextLabel.set(String.format("%s.%s", context.getBucket(), context.getScope()));
                    if (!contextAction.containsAction(clearContextAction)) {
                        contextAction.add(clearContextAction);
                    }
                } else {
                    contextLabel.set(NO_QUERY_CONTEXT_SELECTED);
                    contextAction.remove(clearContextAction);
                    contextAction.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/question_circle.svg", MessageGroupComponent.class));
                }
                return true;
            });

            List<AnAction> bucketActions = new ArrayList<>();
            if (oldContextAction.get() != null) {
                group.replaceAction(oldContextAction.get(), contextAction);
            } else {
                group.add(contextAction);
            }
            oldContextAction.set(contextAction);

            bucketActions.clear();
            contextAction.removeAll();
            contextAction.getTemplatePresentation().setIcon(IconLoader.getIcon("/assets/icons/query_context.svg", MessageGroupComponent.class));
            contextAction.addSeparator("Buckets");
            activeCluster.getChildren().stream()
                    .sorted(Comparator.comparing(b -> b.getName().toLowerCase()))
                    .forEach(bucket -> {
                        DefaultActionGroup bucketsGroup = new DefaultActionGroup(bucket.getName(), true);
                        bucketsGroup.addSeparator("Scopes");

                        bucket.getChildren().stream()
                                .sorted(Comparator.comparing(s -> s.getName().toLowerCase()))
                                .forEach(scope -> {

                                    AnAction scopeAction = new AnAction(scope.getName()) {
                                        @Override
                                        public void actionPerformed(@NotNull AnActionEvent e) {
                                            IQWindowContent.setClusterContext(bucket.getName(), scope.getName());
                                        }
                                    };

                                    bucketsGroup.add(scopeAction);
                                });
                        bucketActions.add(bucketsGroup);
                        contextAction.add(bucketsGroup);
                    });
            contextAction.addSeparator();
            return true;
        });
    }

    private JPanel createSettingsPanel(CapellaOrganizationList organizationList, ChatPanel.LogoutListener logoutListener) {
        JPanel panel = new NonOpaquePanel(new GridLayout(0, 1));
        JPanel orgPanel = new NonOpaquePanel(new BorderLayout());


        orgSelector = new ComboBox<>(organizationList.getData().stream()
                .map(CapellaOrganizationList.Entry::getData)
                .map(CapellaOrganization::getName)
                .toArray(String[]::new));

        orgSelector.setSelectedIndex(organizationList.indexOf(organization));
        orgSelector.addActionListener(e -> ApplicationManager.getApplication().invokeLater(() -> {
            CapellaOrganization selectedOrg = organizationList.getData().get(orgSelector.getSelectedIndex()).getData();
            if (selectedOrg != organization) {
                orgChangeListener.onOrgSelected(selectedOrg);
            }
        }));

        orgPanel.add(new JLabel("Organization:"), BorderLayout.NORTH);
        orgPanel.add(orgSelector, BorderLayout.CENTER);
        DefaultActionGroup toolbarActions = new DefaultActionGroup();
        toolbarActions.add(new AnAction(() -> "Logout", AllIcons.Actions.Exit) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    logoutListener.onLogout(null);
                });
            }

            @Override
            public boolean displayTextInToolbar() {
                return true;
            }
        });

        ActionToolbarImpl actonPanel = new ActionToolbarImpl("System Role Toolbar", toolbarActions, true);
        actonPanel.setTargetComponent(this);
        panel.add(orgPanel);
        panel.setBorder(JBUI.Borders.empty(0, 8, 10, 8));

        JCheckBox enableTelemetry = new JCheckBox("Allow prompt and response collection to help make Capella iQ better.");
        enableTelemetry.setSelected(IQStorage.getInstance().getState().isAllowTelemetry());
        enableTelemetry.addActionListener(action -> {
            IQStorage.getInstance().getState().setAllowTelemetry(enableTelemetry.isSelected());
        });
        panel.add(enableTelemetry);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(actonPanel, BorderLayout.EAST);
        panel.add(bottomPanel);

        return panel;
    }

    public void addSeparator(JComponent comp) {
        ApplicationManager.getApplication().invokeLater(() -> {
            JSeparator separator = new JSeparator();
            separator.setForeground(JBColor.border());
            comp.add(separator);
            updateLayout();
            invalidate();
            validate();
            repaint();
        });
    }

    protected void addAssistantTipsIfEnabled(boolean firstUse) {
        addSeparator(myList);

        var introEnabled = OpenAISettingsState.getInstance().getEnableInitialMessage();
        if (!firstUse && introEnabled == null)
            OpenAISettingsState.getInstance().setEnableInitialMessage(introEnabled = false);
        if (!Boolean.FALSE.equals(introEnabled))
            myList.add(createAssistantTips());
    }

    protected MessageComponent createAssistantTips() {
        var modelType = chat.getChatLink().getConversationContext().getModelType();
        return new MessageComponent(chat, TextFragment.of("""
                Hi, I'm your iQ-powered couchbase assistant. How can I assist you today?
                                
                Here are some suggestions to get you started:
                [✦ What is Couchbase](assistant://?prompt=What+is+Couchbase)
                [✦ How do I connect to Couchbase using Java SDK](assistant://?prompt=How+do+I+connect+to+Couchbase+using+Java+SDK)
                [✦ How to use joins with Couchbase](assistant://?prompt=How+to+use+joins+with+Couchbase)
                """), modelType);
    }

    public void add(JBPanel<?> messageComponent) {
        ApplicationManager.getApplication().invokeLater(() -> {
            myList.add(messageComponent);
            updateLayout();
            scrollToBottom();
            invalidate();
            validate();
            repaint();
        });
    }

    public void scrollToBottom() {
        ScrollingTools.scrollToBottom(myScrollPane);
    }

    public void updateLayout() {
        LayoutManager layout = myList.getLayout();
        int componentCount = myList.getComponentCount();
        for (int i = 0; i < componentCount; i++) {
            layout.removeLayoutComponent(myList.getComponent(i));
            layout.addLayoutComponent(null, myList.getComponent(i));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (myScrollValue > 0) {
            g.setColor(JBColor.border());
            int y = myScrollPane.getY() - 1;
            g.drawLine(0, y, getWidth(), y);
        }
    }

    @Override
    public boolean isVisible() {
        if (super.isVisible()) {
            int count = myList.getComponentCount();
            for (int i = 0; i < count; i++) {
                if (myList.getComponent(i).isVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isNull() {
        return !isVisible();
    }

    @Override
    public String getSystemMessage() {
        return null;
    }

    public void removeLastMessage() {
        ApplicationManager.getApplication().invokeLater(() -> {
            myList.remove(myList.getComponentCount() - 1);
            myList.remove(myList.getComponentCount() - 1);
            updateLayout();
            scrollToBottom();
            invalidate();
            validate();
            repaint();
        });
    }
}
