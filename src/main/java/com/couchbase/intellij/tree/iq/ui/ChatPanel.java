package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.intellij.tree.iq.CapellaOrganization;
import com.couchbase.intellij.tree.iq.CapellaOrganizationList;
import com.couchbase.intellij.tree.iq.chat.*;
import com.couchbase.intellij.tree.iq.core.ChatCompletionParser;
import com.couchbase.intellij.tree.iq.intents.IntentProcessor;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.text.TextContent;
import com.couchbase.intellij.tree.iq.text.TextFragment;
import com.couchbase.intellij.tree.iq.ui.context.stack.ListStack;
import com.couchbase.intellij.tree.iq.ui.context.stack.ListStackFactory;
import com.couchbase.intellij.tree.iq.ui.context.stack.TextInputContextEntry;
import com.couchbase.intellij.tree.iq.ui.listener.SubmitListener;
import com.didalgo.gpt3.ModelType;
import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonUI;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.reactivex.disposables.Disposable;
import okhttp3.internal.http2.StreamResetException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;
import retrofit2.HttpException;

import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ChatPanel extends OnePixelSplitter implements ChatMessageListener {

    private final ExpandableTextFieldExt searchTextField;
    private final JButton button;
    private final JButton stopGenerating;
    private final MessageGroupComponent contentPanel;
    private final JProgressBar progressBar;
    private final Project myProject;
    private final LogoutListener logoutListener;
    private final SubmitListener submitAction;
    private JPanel actionPanel;
    private volatile Object requestHolder;
    private final MainConversationHandler conversationHandler;
    private ListStack contextStack;
    private final ChatLink chatLink;
    private int messageRetryCount = 0;

    public static final KeyStroke SUBMIT_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, CTRL_DOWN_MASK);

    public ChatPanel(@NotNull Project project, ConfigurationPage aiConfig, CapellaOrganizationList organizationList, CapellaOrganization organization, LogoutListener logoutListener, OrganizationListener orgChangeListener) {
        super(true, 0.99f);
        setLayout(new BorderLayout());
        setLackOfSpaceStrategy(LackOfSpaceStrategy.HONOR_THE_SECOND_MIN_SIZE);
        myProject = project;
        conversationHandler = new MainConversationHandler(this);
        this.logoutListener = logoutListener;

        chatLink = new ChatLinkService(project, conversationHandler, aiConfig);
        project.putUserData(ChatLink.KEY, chatLink);
        chatLink.addChatMessageListener(this);
        ContextAwareSnippetizer snippetizer = ApplicationManager.getApplication().getService(ContextAwareSnippetizer.class);
        submitAction = new SubmitListener(chatLink, this::getSearchText, snippetizer);

        this.setDividerWidth(1);
        this.putClientProperty(HyperlinkListener.class, submitAction);

        searchTextField = new ExpandableTextFieldExt(project);
        var searchTextDocument = (AbstractDocument) searchTextField.getDocument();
        searchTextDocument.setDocumentFilter(new NewlineFilter());
        searchTextDocument.putProperty("filterNewlines", Boolean.FALSE);
        searchTextDocument.addDocumentListener(new ExpandableTextFieldExt.ExpandOnMultiLinePaste(searchTextField));
        searchTextField.setMonospaced(false);
        searchTextField.addActionListener(submitAction);
        searchTextField.registerKeyboardAction(submitAction, SUBMIT_KEYSTROKE, JComponent.WHEN_FOCUSED);
        searchTextField.getEmptyText().setText("Type a prompt here");
        button = new JButton(submitAction);
        button.setUI(new DarculaButtonUI());

        stopGenerating = new JButton("Stop", AllIcons.Actions.Suspend);
        stopGenerating.addActionListener(e -> {
            aroundRequest(false);
            if (requestHolder instanceof Disposable disposable) {
                disposable.dispose();
            } else if (requestHolder instanceof Subscription subscription) {
                subscription.cancel();
            }
        });
        stopGenerating.setUI(new DarculaButtonUI());

        actionPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        actionPanel.add(createContextSnippetsComponent(), BorderLayout.NORTH);
        actionPanel.add(searchTextField, BorderLayout.CENTER);
        actionPanel.add(button, BorderLayout.EAST);
        contentPanel = new MessageGroupComponent(this, project, organizationList, organization, orgChangeListener, logoutListener);
        contentPanel.add(progressBar, BorderLayout.SOUTH);

        this.setFirstComponent(contentPanel);
        this.setSecondComponent(actionPanel);
    }

    private JComponent createContextSnippetsComponent() {
        // Creating an instance of ListPopupShower for testing
        ListStackFactory listStackFactory = new ListStackFactory();

        // Showing the list popup
        InputContext chatInputContext = getChatLink().getInputContext();
        contextStack = listStackFactory.showListPopup(actionPanel, getProject(), chatInputContext, this::computeTokenCount);
        JList list = contextStack.getList();
        list.setBackground(actionPanel.getBackground());
        list.setBorder(JBUI.Borders.emptyTop(3));
        list.setFocusable(false);
        list.getModel().addListDataListener(new ContextStackHandler());
        list.setVisible(false);
        contextStack.beforeShow();

        chatInputContext.addListener(event -> {
            contextStack.getListModel().syncModel();
            searchTextField.requestFocusInWindow();
            actionPanel.revalidate();
        });

        return list;
    }

    private int computeTokenCount(TextInputContextEntry info) {
        var tokenCount = 0;
        if (info.getTextContent().isPresent())
            tokenCount = getModelType().getTokenizer().encode(TextContent.toString(info.getTextContent().get())).size();
        info.setTokenCount(tokenCount);

        SwingUtilities.invokeLater(() -> {
            contextStack.getListModel().syncModel();

            actionPanel.revalidate();
        });
        return tokenCount;
    }

    public MessageComponent getQuestion() {
        return question;
    }

    public MessageComponent getAnswer() {
        return answer;
    }

    public interface LogoutListener {
        boolean onLogout(Throwable reason);
    }

    public interface OrganizationListener {
        void onOrgSelected(CapellaOrganization organization);
    }

    private class ContextStackHandler implements ListDataListener {

        protected void onContentsChange() {
            var hasContext = !getChatLink().getInputContext().isEmpty();
            if (hasContext != contextStack.getList().isVisible())
                contextStack.getList().setVisible(hasContext);
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            onContentsChange();
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            onContentsChange();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            onContentsChange();
        }
    }

    public final ChatLink getChatLink() {
        return chatLink;
    }

    public ModelType getModelType() {
        return getChatLink().getConversationContext().getModelType();
    }

    @Override
    public void exchangeStarting(ChatMessageEvent.Starting event) throws ChatExchangeAbortException {
        if (!presetCheck()) {
            throw new ChatExchangeAbortException("Preset check failed");
        }

        TextFragment userMessage = TextFragment.of(event.getUserMessage().getContent());
        question = new MessageComponent(this, userMessage, null);
        answer = new MessageComponent(this, TextFragment.of("Thinking..."), getModelType());
        SwingUtilities.invokeLater(() -> {
            setSearchText("");
            aroundRequest(true);

            MessageGroupComponent contentPanel = getContentPanel();
            contentPanel.add(question);
            contentPanel.add(answer);
            if (lastChatComponent instanceof ChatAwareMessageComponent) {
                ((ChatAwareMessageComponent) lastChatComponent).onNextMessage();
            }
            lastChatComponent = answer;
        });
    }

    private volatile MessageComponent question, answer;
    private volatile JBPanel<?> lastChatComponent;

    @Override
    public void exchangeStarted(ChatMessageEvent.Started event) {
        setRequestHolder(event.getSubscription());

        SwingUtilities.invokeLater(contentPanel::updateLayout);
    }

    protected boolean presetCheck() {
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        String page = getChatLink().getConversationContext().getModelPage();
        if (StringUtils.isEmpty(instance.getConfigurationPage(page).getApiKey())) {
            logoutListener.onLogout(null);
            return false;
        }
        return true;
    }

    @Override
    public void responseArriving(ChatMessageEvent.ResponseArriving event) {
        setContent(event.getPartialResponseChoices());
    }

    @Override
    public void responseArrived(ChatMessageEvent.ResponseArrived event) {
    }

    @Override
    public void responseCompleted(ChatMessageEvent.ResponseArrived event) {
        messageRetryCount = 0;
        List<ChatMessage> response = event.getResponseChoices();
        if (response.size() == 1 && response.get(0).getContent().startsWith("{")) {
            IntentProcessor intentProcessor = ApplicationManager.getApplication().getService(IntentProcessor.class);
            JsonObject intents = JsonObject.fromJson(response.get(0).getContent());
            getQuestion().addIntentResponse(intents);
            intentProcessor.process(this, event.getUserMessage(), intents);
        } else {
            setContent(event.getResponseChoices());
            SwingUtilities.invokeLater(() -> {
                aroundRequest(false);
            });
        }
    }

    public void setContent(List<ChatMessage> content) {
        TextFragment parseResult = ChatCompletionParser.parseGPT35TurboWithStream(content);
        answer.setContent(parseResult);
        answer.showFeedback();
    }

    @Override
    public void exchangeFailed(ChatMessageEvent.Failed event) {
        if (event.getCause() != null && event.getCause() instanceof HttpException) {
            HttpException err = (HttpException) event.getCause();
            if (err.code() == 401) {
                if (messageRetryCount == 0) {
                    if (!logoutListener.onLogout(err)) {
                        SwingUtilities.invokeLater(() -> {
                            messageRetryCount = 1;
                            contentPanel.removeLastMessage();
                            submitAction.submitPrompt(event.getUserMessage().getContent());
                        });
                        return;
                    }
                } else {
                    logoutListener.onLogout(null);
                    return;
                }
            }
        }
        if (answer != null) {
            answer.setErrorContent(getErrorMessage(event.getCause()));
        }
        aroundRequest(false);
    }

    private String getErrorMessage(Throwable cause) {
        if (cause == null)
            return "";
        return (isEmpty(cause.getMessage()) ? "" : cause.getMessage() + "; ")
                + getErrorMessage(cause.getCause());
    }

    @Override
    public void exchangeCancelled(ChatMessageEvent.Cancelled event) {
        messageRetryCount = 0;
    }

    public void responseArrivalFailed(ChatMessageEvent.Failed event) {
        if (event.getCause() instanceof StreamResetException) {
            answer.setErrorContent("*Request failure*, cause: " + event.getCause().getMessage());
            aroundRequest(false);
            event.getCause().printStackTrace();
            return;
        }
        answer.setErrorContent("*Response failure*, cause: " + event.getCause().getMessage() + ", please try again.\n\n Tips: if proxy is enabled, please check if the proxy server is working.");
        SwingUtilities.invokeLater(() -> {
            aroundRequest(false);
            contentPanel.scrollToBottom();
        });
    }

    public Project getProject() {
        return myProject;
    }

    public String getSearchText() {
        return searchTextField.getText();
    }

    public void setSearchText(String t) {
        searchTextField.setText(t);
    }

    public MessageGroupComponent getContentPanel() {
        return contentPanel;
    }

    public void aroundRequest(boolean status) {
        progressBar.setIndeterminate(status);
        progressBar.setVisible(status);
        button.setEnabled(!status);
        if (status) {
            actionPanel.remove(button);
            actionPanel.add(stopGenerating, BorderLayout.EAST);
        } else {
            actionPanel.remove(stopGenerating);
            actionPanel.add(button, BorderLayout.EAST);
        }
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    public void setRequestHolder(Object eventSource) {
        this.requestHolder = eventSource;
    }

    @Override
    public Dimension getPreferredSize() {
        return getParent().getSize();
    }

    public void addMessageComponent(JBPanel<?> component) {
        SwingUtilities.invokeLater(() -> {
            contentPanel.add(component);
            lastChatComponent = component;
            contentPanel.revalidate();
            contentPanel.repaint();
        });
    }

    public interface ChatAwareMessageComponent {
        void onNextMessage();
    }
}
