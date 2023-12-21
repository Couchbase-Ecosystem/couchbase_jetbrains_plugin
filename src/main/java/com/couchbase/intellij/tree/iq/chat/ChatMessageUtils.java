package com.couchbase.intellij.tree.iq.chat;

import com.didalgo.gpt3.ChatFormatDescriptor;
import com.didalgo.gpt3.GPT3Tokenizer;
import com.didalgo.gpt3.TokenCount;
import com.couchbase.intellij.tree.iq.core.TextSubstitutor;
import com.couchbase.intellij.tree.iq.text.CodeFragment;
import com.couchbase.intellij.tree.iq.text.TextContent;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class ChatMessageUtils {

    public static List<? extends TextContent> composeExcept(List<? extends TextContent> textContents, List<? extends TextContent> exceptions, String exceptionPrompt) {
        for (var codeFragment : textContents)
            if (!exceptions.contains(codeFragment) && !exceptionPrompt.contains(TextContent.toString(codeFragment).strip()))
                return textContents;

        return List.of();
    }

    public static String composeAll(String prompt, List<? extends TextContent> textContents) {
        var buf = new StringBuilder();
        for (var textContent : textContents) {
            if (prompt.contains(textContent.toString()))
                continue;
            if (textContent instanceof CodeFragment codeFragment && StringUtils.isEmpty(codeFragment.description()))
                buf.append("[Selected code]\n");
            textContent.appendTo(buf);
            buf.append("\n\n");
        }
        if (!prompt.isEmpty()) {
            if (!buf.isEmpty())
                buf.append("---\n\n");
            buf.append(prompt);
        }

        return buf.toString();
    }

    public static boolean isRoleUser(ChatMessage chatMessage) {
        return isRole(ChatMessageRole.USER, chatMessage);
    }

    public static boolean isRoleSystem(ChatMessage chatMessage) {
        return isRole(ChatMessageRole.SYSTEM, chatMessage);
    }

    private static boolean isRole(ChatMessageRole role, ChatMessage chatMessage) {
        return Objects.equals(role.value(), chatMessage.getRole());
    }

    @SuppressWarnings("StringEquality")
    public static void substitutePlaceholders(List<ChatMessage> chatMessages, TextSubstitutor substitutor) {
        chatMessages.replaceAll(chatMessage -> {
            String template = chatMessage.getContent();
            String resolved = substitutor.resolvePlaceholders(template);
            if (resolved != template) {
                chatMessage = new ChatMessage(chatMessage.getRole(), resolved);
            }
            return chatMessage;
        });
    }

    public static int countTokens(List<ChatMessage> messages, GPT3Tokenizer tokenizer, ChatFormatDescriptor formatDescriptor) {
        return TokenCount.fromMessages(messages, tokenizer, formatDescriptor);
    }
}
