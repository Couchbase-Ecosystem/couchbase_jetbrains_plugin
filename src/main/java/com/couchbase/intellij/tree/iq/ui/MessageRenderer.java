/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.ui;

import javax.swing.text.EditorKit;

public interface MessageRenderer {

    EditorKit getEditorKit();

    int getWidth();

    Object getClientProperty(Object key);
}
