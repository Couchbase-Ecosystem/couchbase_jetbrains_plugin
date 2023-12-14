package com.couchbase.intellij.tree.iq.chat;

public interface ChatMessageListener {

    void exchangeStarting(ChatMessageEvent.Starting event) throws ChatExchangeAbortException;

    void exchangeStarted(ChatMessageEvent.Started event);

    void responseArriving(ChatMessageEvent.ResponseArriving event);

    void responseArrived(ChatMessageEvent.ResponseArrived event);

    void responseCompleted(ChatMessageEvent.ResponseArrived event);

    void exchangeFailed(ChatMessageEvent.Failed event);

    void exchangeCancelled(ChatMessageEvent.Cancelled event);

}
