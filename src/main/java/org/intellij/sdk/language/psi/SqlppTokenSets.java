// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.intellij.sdk.language.psi;

import com.intellij.psi.tree.TokenSet;
import generated.GeneratedTypes;

public interface SqlppTokenSets {

  TokenSet IDENTIFIERS = TokenSet.create(GeneratedTypes.IDENTIFIER);

  TokenSet COMMENTS = TokenSet.create(GeneratedTypes.COMMENT);

}
