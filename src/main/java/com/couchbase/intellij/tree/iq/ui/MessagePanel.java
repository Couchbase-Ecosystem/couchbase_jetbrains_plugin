package com.couchbase.intellij.tree.iq.ui;

import com.couchbase.intellij.tree.iq.text.TextFragment;
import com.couchbase.intellij.tree.iq.ui.view.CollapsiblePanelFactory;
import com.couchbase.intellij.tree.iq.ui.view.HyperlinkHandler;
import com.couchbase.intellij.tree.iq.ui.view.LanguageDetector;
import com.couchbase.intellij.tree.iq.ui.view.RSyntaxTextAreaView;
import com.couchbase.intellij.tree.iq.util.StandardLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.ExtendableHTMLViewFactory;
import com.intellij.util.ui.HTMLEditorKitBuilder;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTML;
import java.awt.*;

public class MessagePanel extends HtmlPanel implements MessageRenderer {

    private volatile TextFragment text;

    public MessagePanel(Project project) {
        setEditorKit(new HTMLEditorKitBuilder()
                .withViewFactoryExtensions((e, v) -> createView(project, e, v), ExtendableHTMLViewFactory.Extensions.WORD_WRAP)
                .build());
        setOpaque(true);
    }

    protected View createView(Project project, Element elem, View view) {
        AttributeSet attrs = elem.getAttributes();
        if (attrs.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.DIV && supportsCollapsibility(attrs))
            return CollapsiblePanelFactory.createPanel(project, this, elem, attrs);
        if (attrs.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.PRE)
            return new RSyntaxTextAreaView(project, elem, LanguageDetector.getLanguage(elem).orElse(StandardLanguage.NONE));

        return view;
    }

    protected boolean supportsCollapsibility(AttributeSet attrs) {
        return CollapsiblePanelFactory.supportsCollapsibility(this, attrs);
    }

    @Override
    protected @NotNull @Nls String getBody() {
        return (text == null)? "" : text.toHtml();
    }

    @Override
    protected @NotNull Font getBodyFont() {
        return UIUtil.getLabelFont();
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            HyperlinkHandler.handleOrElse(e, super::hyperlinkUpdate);
    }

    public void updateMessage(TextFragment updateMessage) {
        this.text = updateMessage;
        update();
    }
}
