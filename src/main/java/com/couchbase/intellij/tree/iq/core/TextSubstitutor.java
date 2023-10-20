/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.core;

@FunctionalInterface
public interface TextSubstitutor {
    TextSubstitutor NONE = (x -> x);

    String resolvePlaceholders(String text);

}
