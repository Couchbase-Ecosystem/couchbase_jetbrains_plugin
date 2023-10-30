/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.ui;


import com.couchbase.intellij.tree.iq.chat.ChatMessageEvent;
import com.couchbase.intellij.tree.iq.chat.ChatMessageListener;
import com.couchbase.intellij.tree.iq.chat.ConversationContext;
import com.couchbase.intellij.tree.iq.chat.ConversationHandler;
import com.couchbase.intellij.tree.iq.core.ChatCompletionRequestProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainConversationHandler implements ConversationHandler {

    private static final Logger LOG = Logger.getInstance(MainConversationHandler.class);

    private final MainPanel mainPanel;

    public MainConversationHandler(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Override
    public Disposable push(ConversationContext ctx, ChatMessageEvent.Starting event, ChatMessageListener listener) {
        var application = ApplicationManager.getApplication();
        var userMessage = event.getUserMessage();
        var chatCompletionRequestProvider = application.getService(ChatCompletionRequestProvider.class);
        var chatCompletionRequest = chatCompletionRequestProvider.chatCompletionRequest(ctx, userMessage)
                .build();

//        return application.getService(ChatGPTHandler.class)
//                .handle(ctx, event.initiating(chatCompletionRequest), listener)
//                .subscribeOn(Schedulers.io())
//                .subscribe();

        return null;
    }
}
