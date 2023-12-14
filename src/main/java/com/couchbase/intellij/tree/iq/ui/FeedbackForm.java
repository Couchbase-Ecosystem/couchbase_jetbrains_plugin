package com.couchbase.intellij.tree.iq.ui;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;

public class FeedbackForm extends JBPanel<FeedbackForm> {

    private static final String feedbackEndpoint = "https://nms548yy5b.execute-api.us-west-1.amazonaws.com/Prod/";

    private final JBTextField additionalInfo;
    private final JButton submit;

    private final boolean liked;
    private boolean submitted;

    public FeedbackForm(boolean liked) {
        this.liked = liked;
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

        MessagePanel message = new MessagePanel();
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
            sendFeedback();
        }
    }

    private void sendFeedback() {
        submitted = true;
        submit.setEnabled(false);
        additionalInfo.setEditable(false);

        HttpRequest request = HttpRequest.newBuilder(new URI(feedbackEndpoint))
                .header("X-Secret", "c0uchbase_is_aw3some")
                .method(Http.P)

    }

    public void submitIfHavent() {
        if (!submitted) {
            sendFeedback();
        }
    }
}
