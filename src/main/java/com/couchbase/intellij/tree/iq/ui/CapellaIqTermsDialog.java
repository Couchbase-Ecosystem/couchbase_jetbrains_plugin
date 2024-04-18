package com.couchbase.intellij.tree.iq.ui;

import com.github.weisj.jsvg.J;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class CapellaIqTermsDialog extends DialogWrapper {
    private static final String IQ_TERMS_LABEL = "<html>Capella IQ uses a third-party large language model (LLM).<br/>" +
            "Please do not enter sensitive data into iQ and review its output before using.</html>";
    private static final String IHAVEREADANDACCEPTED = "I have read and agree to the ";
    private static final String TERMS_URL = "https://www.couchbase.com/iq-terms/";
    private static final String SUPPLEMENTALTERMS = "<html><a href='" + TERMS_URL + "'>Capella iQ supplemental terms.</html>";

    private Project project;
    private JCheckBox acceptCheckBox;

    @Override
    protected void doOKAction() {
        super.doOKAction();
        isOk = true;
    }

    private boolean isOk;

    public CapellaIqTermsDialog(Project project) {
        super(project, true);
        this.project = project;
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel(IQ_TERMS_LABEL), BorderLayout.NORTH);

        // todo: check if the user is an admin org
        acceptCheckBox = new JCheckBox(IHAVEREADANDACCEPTED);
        mainPanel.add(acceptCheckBox, BorderLayout.WEST);
        JEditorPane termsLink = new JEditorPane("text/html", SUPPLEMENTALTERMS);
        termsLink.setEditable(false);
        termsLink.setBackground(null);
        termsLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        termsLink.setMargin(JBUI.insets(1, 0, 0, 0));
        termsLink.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(URI.create(TERMS_URL));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        mainPanel.add(termsLink, BorderLayout.CENTER);
        return mainPanel;
    }

    public boolean isAccepted() {
        return isOk && acceptCheckBox.isSelected();
    }
}
