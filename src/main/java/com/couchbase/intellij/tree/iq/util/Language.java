/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.util;

import java.util.List;

public interface Language {

    List<String> ids();

    String mimeType();

    List<String> fileExtensions();
}
