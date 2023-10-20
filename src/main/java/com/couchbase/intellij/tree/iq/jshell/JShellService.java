/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.jshell;

import jdk.jshell.SnippetEvent;

import java.util.List;

public interface JShellService {

    List<SnippetEvent> eval(String input);

}
