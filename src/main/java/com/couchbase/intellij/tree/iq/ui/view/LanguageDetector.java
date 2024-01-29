package com.couchbase.intellij.tree.iq.ui.view;

import com.couchbase.intellij.tree.iq.util.Language;
import com.couchbase.intellij.tree.iq.util.SqlppLanguage;
import com.couchbase.intellij.tree.iq.util.StandardLanguage;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import java.util.Optional;

public class LanguageDetector {

    public static Optional<Language> getLanguage(Element elem) {
        String lang = getLanguageClassIfAvailable(elem);
        if (lang == null && elem.getElementCount() > 0) {
            lang = getLanguageClassIfAvailable(elem.getElement(0));
            if (lang == null) {
                lang = detectLanguageByContent(elem);
            }
        }

        if ("sqlpp".equals(lang)) {
            return Optional.of(SqlppLanguage.INSTANCE);
        }


        return StandardLanguage.findByIdentifier(lang);
    }

    public static String getLanguageClassIfAvailable(Element elem) {
        Element codeElement = null;
        AttributeSet codeAttrs;
        if (elem.getElementCount() > 0
                && (codeElement = elem.getElement(0)) != null
                && (codeAttrs = codeElement.getAttributes()) != null
                && codeAttrs.getAttribute(HTML.Tag.CODE) instanceof AttributeSet codeAttr
                && codeAttr.getAttribute(HTML.Attribute.CLASS) instanceof String codeLang
                && codeLang.startsWith("language-")) {
            return codeLang.substring("language-".length());
        }
        return null;
    }
    public static String detectLanguageByContent(Element elem) {
        Element codeElement = null;
        if (elem.getElementCount() > 0
                && (codeElement = elem.getElement(0)) != null) {
            try {
                int so = codeElement.getStartOffset();
                int length = codeElement.getEndOffset() - so;
                String elementCode = codeElement.getDocument().getText(so, length).trim();
                if (elementCode.startsWith("SELECT") || elementCode.startsWith("UPDATE") || elementCode.contains("INSERT") || elementCode.startsWith("EXPLAIN")) {
                    return "sql";
                }
            } catch (BadLocationException e) {
                return null;
            }
        }
        return null;
    }
}
