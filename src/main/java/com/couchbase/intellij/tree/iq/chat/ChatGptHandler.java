package com.couchbase.intellij.tree.iq.chat;

import com.couchbase.intellij.tree.iq.OpenAIServiceHolder;
import com.theokanning.openai.completion.chat.*;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Subscription;

import javax.swing.SwingUtilities;
import java.util.*;

public class ChatGptHandler {

    public Flowable<?> handle(ConversationContext ctx, ChatMessageEvent.Initiating event, ChatMessageListener listener) {
        var openAiService = OpenAIServiceHolder.getOpenAiService(ctx.getModelPage());
        var flowHandler = new ChatCompletionHandler(listener);
        var request = event.getRequest().orElseThrow(() -> new IllegalArgumentException("ChatCompletionRequest is required"));

        if (Boolean.TRUE.equals(request.getStream())) {
            return openAiService.streamChatCompletion(request)
                    .doOnSubscribe(flowHandler.onSubscribe(event))
                    .doOnError(flowHandler.onError())
                    .doOnComplete(flowHandler.onComplete(ctx))
                    .doOnNext(flowHandler.onNextChunk());
        } else {
            return Flowable.fromCallable(() -> openAiService.createChatCompletion(request))
                    .doOnSubscribe(flowHandler.onSubscribe(event))
                    .doOnError(flowHandler.onError())
                    .doOnComplete(flowHandler.onComplete(ctx))
                    .doOnNext(flowHandler.onNext());
        }
    }

    static class ChatCompletionHandler {
        private final ChatMessageListener listener;
        private final SortedMap<Integer, StringBuffer> partialResponseChoices;
        private volatile ChatMessageEvent.Started event;

        public ChatCompletionHandler(ChatMessageListener listener) {
            this.listener = listener;
            this.partialResponseChoices = Collections.synchronizedSortedMap(new TreeMap<>());
        }

        public Consumer<Subscription> onSubscribe(ChatMessageEvent.Initiating event) {
            return subscription -> {
                SwingUtilities.invokeLater(() -> listener.exchangeStarted(this.event = event.started(subscription)));
            };
        }

        public Action onComplete(ConversationContext ctx) {
            return () -> {
                SwingUtilities.invokeLater(() -> {
                    try {
                        final List<ChatMessage> assistantMessages = toMessages(partialResponseChoices);
                        if (!assistantMessages.isEmpty()) {
                            ctx.addChatMessage(assistantMessages.get(0));
                        }
                        listener.responseCompleted(event.responseArrived(assistantMessages));
                    } catch (Exception e) {

                    }
                });
            };
        }

        public Consumer<ChatCompletionChunk> onNextChunk() {
            return chunk -> {
                if (!chunk.getChoices().isEmpty()) {
                    SwingUtilities.invokeLater(() -> listener.responseArriving(event.responseArriving(chunk, formResponse(chunk.getChoices()))));
                }
            };
        }

        public Consumer<ChatCompletionResult> onNext() {
            return result -> {
                if (result != null && result.getChoices() != null && !result.getChoices().isEmpty()) {
                    SwingUtilities.invokeLater(() -> listener.responseArrived(event.responseArrived(formResponse(result.getChoices()))));
                }
            };
        }

        public Consumer<Throwable> onError() {
            return cause -> {
                listener.exchangeFailed(event == null ? null : event.failed(cause));
            };
        }

        private List<ChatMessage> formResponse(List<ChatCompletionChoice> choices) {
            choices.forEach(choice -> {
                partialResponseChoices.computeIfAbsent(choice.getIndex(), __ -> new StringBuffer())
                        .append(StringUtils.defaultIfEmpty(choice.getMessage().getContent(), ""));
            });
            return toMessages(partialResponseChoices);
        }

        private List<ChatMessage> toMessages(SortedMap<Integer, StringBuffer> partialResponseChoices) {
            List<ChatMessage> responseChoices = new ArrayList<>(partialResponseChoices.size());
            partialResponseChoices.forEach((key, value) -> responseChoices.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(), value.toString())));
            return responseChoices;
        }
    }
}

