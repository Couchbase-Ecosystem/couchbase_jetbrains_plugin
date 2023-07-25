package org.intellij.sdk.language.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.diagnostic.Logger;

public class SqlppCompletionContributor extends CompletionContributor {

    private static final Logger log = Logger.getInstance(SqlppCompletionContributor.class);

    public SqlppCompletionContributor() {
//        new Keywords(this);
        new Identifiers(this);
    }

}
