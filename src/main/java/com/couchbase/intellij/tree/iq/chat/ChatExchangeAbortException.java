/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.chat;

public class ChatExchangeAbortException extends RuntimeException {
    public ChatExchangeAbortException(String message) {
        super(message);
    }
}
