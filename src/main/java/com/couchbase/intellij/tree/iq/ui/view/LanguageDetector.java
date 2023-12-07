/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.ui.view;

import com.couchbase.intellij.tree.iq.util.Language;
import com.couchbase.intellij.tree.iq.util.StandardLanguage;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import java.util.Optional;

public class LanguageDetector {

    public static Optional<Language> getLanguage(Element elem) {
        String lang = getLanguageClassIfAvailable(elem);
        if (lang == null && elem.getElementCount() > 0)
            lang = getLanguageClassIfAvailable(elem.getElement(0));

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
}
