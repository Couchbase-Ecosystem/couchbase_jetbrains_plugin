// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.language.cblite.psi;

import com.couchbase.intellij.tree.cblite.sqlppl.SQLPPLiteLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SqlppLiteTokenType extends IElementType {

  public SqlppLiteTokenType(@NotNull @NonNls String debugName) {
    super(debugName, SQLPPLiteLanguage.INSTANCE);
  }

  @Override
  public String toString() {
    return "SqlppLiteTokenType." + super.toString();
  }

}
