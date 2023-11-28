package com.couchbase.intellij.tree.iq.chat;

import io.reactivex.disposables.Disposable;

@FunctionalInterface
public interface ConversationHandler {

    Disposable push(ConversationContext ctx, ChatMessageEvent.Starting event, ChatMessageListener listener);

}
