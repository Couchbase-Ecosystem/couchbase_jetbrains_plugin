package com.couchbase.intellij.tree.iq.chat;

import com.couchbase.intellij.tree.iq.text.TextContent;
import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.List;

public interface ChatMessageComposer {

    ChatMessage compose(ConversationContext ctx, String prompt);

    ChatMessage compose(ConversationContext ctx, String prompt, List<? extends TextContent> textContents);

}
