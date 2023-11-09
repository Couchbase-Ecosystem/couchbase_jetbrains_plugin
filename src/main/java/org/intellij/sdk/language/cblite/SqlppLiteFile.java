package org.intellij.sdk.language.cblite;

import com.couchbase.intellij.tree.cblite.sqlppl.SQLPPLiteFileType;
import com.couchbase.intellij.tree.cblite.sqlppl.SQLPPLiteLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class SqlppLiteFile extends PsiFileBase {
    protected SqlppLiteFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SQLPPLiteLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return SQLPPLiteFileType.INSTANCE;
    }
}
