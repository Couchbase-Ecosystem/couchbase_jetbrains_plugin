package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.client.java.json.JsonObject;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.function.Consumer;

public class FeedbackForm extends JBPanel<FeedbackForm> {


    private final JBTextField additionalInfo;
    private final JButton submit;

    private final boolean liked;
    private boolean submitted;

    private final Consumer<String> sender;

    public static FeedbackForm liked(Project project) {
        return new FeedbackForm(project, true, null);
    }

    public static FeedbackForm disliked(Project project, Consumer<String> sender) {
        return new FeedbackForm(project, false, sender);
    }

    private FeedbackForm(Project project, boolean liked, Consumer<String> sender) {
        this.liked = liked;
        this.sender = sender;
        setDoubleBuffered(true);
        setOpaque(true);
        setBackground(new JBColor(0xEBEBEB, 0x2d2f30));
        setBorder(JBUI.Borders.empty(JBUI.scale(10), JBUI.scale(10)));
        setLayout(new BorderLayout(JBUI.scale(2), 0));

        additionalInfo = new JBTextField();
        submit = new JButton("Submit");

        submit.addActionListener(e -> {
            sendFeedback();
        });

        MessagePanel message = new MessagePanel(project);
        message.setEditable(false);
        message.setOpaque(true);
        message.setBackground(null);
        message.setBorder(JBUI.Borders.emptyLeft(JBUI.scale(5)));
        add(message, BorderLayout.NORTH);
        if (!liked) {
            message.setText("Thank you for the feedback. We are sorry you are not satisfied with the response. You can use this text box to provide us with tips on how to improve our responses in the future:");
            add(additionalInfo, BorderLayout.CENTER);
            add(submit, BorderLayout.SOUTH);
        } else {
            message.setText("Thank you for the feedback!");
            submitted = true;
        }
    }

    private void sendFeedback() {
        submitted = true;
        submit.setEnabled(false);
        additionalInfo.setEditable(false);

        if (sender != null) {
            sender.accept(additionalInfo.getText());
        }
    }

    public void submitIfHavent() {
        if (!submitted) {
            sendFeedback();
        }
    }
}
