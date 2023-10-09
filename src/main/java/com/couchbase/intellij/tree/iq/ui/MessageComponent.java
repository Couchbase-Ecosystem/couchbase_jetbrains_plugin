package com.couchbase.intellij.tree.iq.ui;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.util.StringUtil;
import com.couchbase.intellij.tree.node.ConnectionNodeDescriptor;
import com.intellij.icons.AllIcons;
import com.intellij.notification.impl.ui.NotificationsUtil;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MessageComponent extends JBPanel<MessageComponent> {

    private static final Logger LOG = LoggerFactory.getLogger(MessageComponent.class);

    private final MessagePanel component = new MessagePanel();

    private final String question;
    private final List<String> answers = new ArrayList<>();
    private String answer;

    public MessageComponent(String content, boolean me) {
        question = content;
        setDoubleBuffered(true);
        setOpaque(true);
        setBackground(me ? new JBColor(0xEAEEF7, 0x45494A) : new JBColor(0xE0EEF7, 0x2d2f30 /*2d2f30*/));
        setBorder(JBUI.Borders.empty(10, 10, 10, 0));
        setLayout(new BorderLayout(JBUI.scale(7), 0));

        if (OpenAISettingsState.getInstance().enableAvatar) {
            JPanel iconPanel = new JPanel(new BorderLayout());
            iconPanel.setOpaque(false);
            Icon icon;
            try {
                icon = me ? AllIcons.General.User : IconLoader.getIcon("/assets/icons/couchbase-active-big.svg", ConnectionNodeDescriptor.class);
            } catch (Exception e) {
                icon = me ? AllIcons.General.User : IconLoader.getIcon("/assets/icons/couchbase-active-big.svg", ConnectionNodeDescriptor.class);
            }
            //Image scale = ImgUtil.scale(imageIcon, 30, 30);
            iconPanel.add(new JBLabel(icon), BorderLayout.NORTH);
            add(iconPanel, BorderLayout.WEST);
        }
        JPanel centerPanel = new JPanel(new VerticalLayout(JBUI.scale(8)));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(JBUI.Borders.emptyRight(10));
        centerPanel.add(createContentComponent(content));
        add(centerPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(JBUI.Borders.emptyRight(10));
        JLabel copyAction = new JLabel(AllIcons.Actions.Copy);
        copyAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyAction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ClipboardUtil.setStr(me ? question : answer);
                //Notifications.Bus.notify(
                //        new Notification(ChatGPTBundle.message("group.id"),
                //                "Copy successfully",
                //                "ChatGPT reply content has been successfully copied to the clipboard.",
                //                NotificationType.INFORMATION));
            }
        });
        actionPanel.add(copyAction, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.EAST);
    }

    public Component createContentComponent(String content) {

        component.setEditable(false);
        component.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        component.setContentType("text/html; charset=UTF-8");
        component.setOpaque(false);
        component.setBorder(null);

        NotificationsUtil.configureHtmlEditorKit(component, false);
        component.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, StringUtil.unescapeXmlEntities(StringUtil.stripHtml(content, " ")));

        component.updateMessage(content);

        component.setEditable(false);
        if (component.getCaret() != null) {
            component.setCaretPosition(0);
        }

        component.revalidate();
        component.repaint();

        return component;
    }

    public void setContent(String content) {
        new MessageWorker(content).execute();
    }

    public void setSourceContent(String source) {
        answer = source;
    }

    public void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            Rectangle bounds = getBounds();
            scrollRectToVisible(bounds);
        });
    }

    public List<String> getAnswers() {
        return answers;
    }

    public String prevAnswers() {
        StringBuilder result = new StringBuilder();
        for (String s : answers){
            result.append(s);
        }
        return result.toString();
    }

    class MessageWorker extends SwingWorker<Void, String> {
        private final String message;

        public MessageWorker(String message) {
            this.message = message;
        }

        @Override
        protected Void doInBackground() throws Exception {
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                component.updateMessage(message);
                component.updateUI();
            } catch (Exception e) {
                LOG.error("ChatGPT Exception in processing response: response:{} error: {}", message, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
