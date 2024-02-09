package com.couchbase.intellij.utils;

import cn.hutool.core.stream.StreamUtil;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Subscribable<T> {
    private Optional<T> value;
    private Map<WeakReference<Object>, Function<Optional<T>, Boolean>> subscribers = new HashMap<>();

    public Subscribable() {
        value = Optional.empty();
    }

    public Subscribable(T value) {
        this.value = Optional.ofNullable(value);
    }

    public void set(T value) {
        this.value = Optional.ofNullable(value);
        subscribers = subscribers.entrySet().stream()
                .filter(subscriber -> subscriber.getKey() != null
                        && subscriber.getKey().get() != null
                        && subscriber.getValue() != null)
                .filter(subscriber -> {
                    try {
                        return subscriber.getValue().apply(this.value);
                    } catch (Throwable e) {
                        return false;
                    }
                })
                .collect(Collectors.toMap(s -> s.getKey(), s -> s.getValue()));
    }

    /**
     * Adds a subscriber for this value.
     * When invoked, subscriber must return true to re-validate the subscription
     *  otherwise it will be unsubscribed.
     * @param subscriber the subscriber
     */
    public void subscribe(Object key, Function<Optional<T>, Boolean> subscriber) {
        if (subscriber.apply(value)) {
            this.subscribers = this.subscribers.entrySet().stream()
                            .filter(s -> s.getKey() != null && s.getKey().get() != null && !Objects.equals(s.getKey().get(), key))
                    .collect(Collectors.toMap(s -> s.getKey(), s -> s.getValue()));
            this.subscribers.put(new WeakReference<>(key), subscriber);
        }
    }

    public void unsubscribe(Object key) {
        this.subscribers = this.subscribers.entrySet().stream()
                .filter(s -> s.getKey() != null && s.getKey().get() != null && !Objects.equals(s.getKey().get(), key))
                .collect(Collectors.toMap(s -> s.getKey(), s -> s.getValue()));
    }

    public Optional<T> get() {
        return value;
    }

    public boolean isPresent() {
        return value.isPresent();
    }

    public T getValue() {
        return value.orElse(null);
    }
}
