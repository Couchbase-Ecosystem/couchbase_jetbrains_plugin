// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.language.psi;

import com.couchbase.intellij.database.entity.CouchbaseClusterEntity;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import generated.psi.Alias;
import org.intellij.sdk.language.SQLPPFileType;
import org.intellij.sdk.language.SQLPPLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlppFile extends PsiFileBase {
  public SqlppFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, SQLPPLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return SQLPPFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "Sqlpp File";
  }

  public Map<String, CouchbaseClusterEntity> getAliases() {
    Map<String, CouchbaseClusterEntity> result = new HashMap<>();
    PsiTreeUtil.collectElementsOfType(getNode().getPsi(), Alias.class).forEach(aliasPsi -> {
      PsiElement namePsi = aliasPsi.getNextSibling();
      PsiElement targetPsi = aliasPsi.getPrevSibling();
    });
    return result;
  }
}
