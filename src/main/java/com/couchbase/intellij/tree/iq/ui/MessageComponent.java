/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.ui;

import com.didalgo.gpt3.ModelType;
import com.couchbase.intellij.tree.iq.ChatGptBundle;
import com.couchbase.intellij.tree.iq.ChatGptIcons;
import com.couchbase.intellij.tree.iq.settings.OpenAISettingsState;
import com.couchbase.intellij.tree.iq.text.CodeSnippetManipulator;
import com.couchbase.intellij.tree.iq.text.TextFragment;
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.ExtendableHTMLViewFactory;
import com.intellij.util.ui.HTMLEditorKitBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

public class MessageComponent extends JBPanel<MessageComponent> {

    private static final Logger LOG = Logger.getInstance(MessageComponent.class);

    private final MessagePanel component = new MessagePanel();

    private volatile TextFragment text;

    public MessageComponent(TextFragment text, ModelType model) {
        this.text = text;
        var fromUser = (model == null);
        setDoubleBuffered(true);
        setOpaque(true);
        setBackground(fromUser ? new JBColor(0xF7F7F7, 0x3C3F41) : new JBColor(0xEBEBEB, 0x2d2f30));
        setBorder(JBUI.Borders.empty(JBUI.scale(4), JBUI.scale(1)));
        setLayout(new BorderLayout(JBUI.scale(2), 0));

        if (OpenAISettingsState.getInstance().isEnableAvatar()) {
            JPanel iconPanel = new JPanel(new BorderLayout());
            iconPanel.setBorder(JBUI.Borders.empty(JBUI.scale(7), JBUI.scale(7), JBUI.scale(7), 0));
            iconPanel.setOpaque(false);
            Icon imageIcon;
            if (fromUser) {
                imageIcon = ChatGptIcons.USER;
            } else if (model.ordinal() < ModelType.GPT_3_5_TURBO.ordinal()) {
                imageIcon = ChatGptIcons.ROBOT_PINK_EYE;
            } else if (model.ordinal() < ModelType.TEXT_DAVINCI_003.ordinal()) {
                imageIcon = ChatGptIcons.ROBOT_GREEN_EYE;
            } else {
                imageIcon = ChatGptIcons.ROBOT_GREEN_EYE;
            }
            iconPanel.add(new JBLabel(IconUtil.scale(imageIcon, this, 1.25f)), BorderLayout.NORTH);
            add(iconPanel, BorderLayout.WEST);
        }
        JPanel centerPanel = new JPanel(new VerticalLayout(JBUI.scale(0)));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(JBUI.Borders.emptyLeft(JBUI.scale(5)));
        centerPanel.add(createContentComponent(text, fromUser));
        add(centerPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(JBUI.Borders.empty(JBUI.scale(7), 0, 0, JBUI.scale(10)));
        JLabel copyAction = new JLabel(AllIcons.Actions.Copy);
        copyAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyAction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Transferable transferable = new StringSelection(getText().markdown());
                CopyPasteManager.getInstance().setContents(transferable);
                Notifications.Bus.notify(
                        new Notification(ChatGptBundle.message("group.id"),
                                "Copied successfully",
                                "ChatGPT " + (fromUser? "prompt":"reply") + " content has been successfully copied to the clipboard.",
                                NotificationType.INFORMATION));
            }
        });
        actionPanel.add(copyAction, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.EAST);
    }

    public TextFragment getText() {
        return text;
    }

    public String toDisplayText(TextFragment text, boolean fromUser) {
        if (!fromUser)
            return text.toHtml();

        String markdown = text.markdown();
        StringBuilder buf = new StringBuilder(markdown.length());
        char ch;
        boolean onLineStart = true;
        for (int i = 0; i < markdown.length(); i++) {
            switch (ch = markdown.charAt(i)) {
                case '\r' -> { }
                case '\n' -> {
                    onLineStart = true;
                    buf.append("<br>");
                }
                case '<' -> {
                    onLineStart = false;
                    buf.append("&lt;");
                }
                case '>' -> {
                    onLineStart = false;
                    buf.append("&gt;");
                }
                case '&' -> {
                    onLineStart = false;
                    buf.append("&amp;");
                }
                default -> {
                    if (onLineStart && Character.isWhitespace(ch)) {
                        buf.append("&nbsp;");
                        if (ch == '\t') buf.append("&nbsp;&nbsp;&nbsp;");
                    } else {
                        onLineStart = false;
                        buf.append(ch);
                    }
                }
            }
        }
        return buf.toString();
    }

    public Component createContentComponent(TextFragment content, boolean fromUser) {

        component.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        component.setContentType("text/html; charset=UTF-8");
        component.setOpaque(false);
        component.setBorder(null);

        var editorKit = configureHtmlEditorKit2(component, false);
        if (fromUser)
            editorKit.getStyleSheet().addRule("body {white-space:pre-wrap}");
        component.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, getText().markdown());
        component.updateMessage(fromUser? TextFragment.of(content.markdown(), CodeSnippetManipulator.makeCodeSnippetBlocksCollapsible(toDisplayText(content, true))) : content);
        component.setEditable(false);
        if (component.getCaret() != null) {
            component.setCaretPosition(0);
        }

        component.revalidate();
        component.repaint();

        return component;
    }

    public HTMLEditorKit configureHtmlEditorKit2(@NotNull JEditorPane editorPane, boolean notificationColor) {
        HTMLEditorKit kit = new HTMLEditorKitBuilder()
                .withViewFactoryExtensions((e, v) -> component.createView(e, v), ExtendableHTMLViewFactory.Extensions.WORD_WRAP)
                .withFontResolver((defaultFont, attributeSet) -> {
                    if ("a".equalsIgnoreCase(String.valueOf(attributeSet.getAttribute(AttributeSet.NameAttribute))))
                        return UIUtil.getLabelFont();
                    else
                        return defaultFont;
                }).build();
        String color = ColorUtil.toHtmlColor(notificationColor ? getLinkButtonForeground() : JBUI.CurrentTheme.Link.Foreground.ENABLED);
        kit.getStyleSheet().addRule("a {color: " + color + "}");
        kit.getStyleSheet().addRule("p {margin:4px 0}");
        editorPane.setEditorKit(kit);
        return kit;
    }

    public static @NotNull Color getLinkButtonForeground() {
        return JBColor.namedColor("Notification.linkForeground", JBUI.CurrentTheme.Link.Foreground.ENABLED);
    }

    private final AtomicReference<TextFragment> content = new AtomicReference<>();
    private final Timer updateContentTimer = new Timer(20, this::updateIncrementalContent);

    public void setContent(TextFragment content) {
        this.text = content;
        this.content.set(content);
        if (!updateContentTimer.isRunning()) {
            updateContentTimer.setRepeats(false);
            updateContentTimer.start();
        }
    }

    public void setErrorContent(String errorMessage) {
        setContent(TextFragment.of(errorMessage));
    }

    protected void updateIncrementalContent(ActionEvent event) {
        TextFragment message = null;
        try {
            message = content.get();
            if (message != null) {
                component.updateMessage(message);
                content.compareAndSet(message, null);
            }
        } catch (Exception e) {
            LOG.error("ChatGPT Exception in processing response: response: {}, error: {}", e, String.valueOf(message), e.getMessage());
            e.printStackTrace();
        }
    }
}
