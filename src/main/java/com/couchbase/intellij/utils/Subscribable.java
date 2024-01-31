package com.couchbase.intellij.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Subscribable<T> {
    private Optional<T> value;
    private List<Function<Optional<T>, Boolean>> subscribers = new ArrayList<>();

    public Subscribable() {
        value = Optional.empty();
    }

    public Subscribable(T value) {
        this.value = Optional.ofNullable(value);
    }

    public void set(T value) {
        this.value = Optional.ofNullable(value);
        subscribers = subscribers.stream()
                .filter(subscriber -> subscriber.apply(this.value))
                .collect(Collectors.toList());
    }

    /**
     * Adds a subscriber for this value.
     * When invoked, subscriber must return true to re-validate the subscription
     *  otherwise it will be unsubscribed.
     * @param subscriber the subscriber
     */
    public void subscribe(Function<Optional<T>, Boolean> subscriber) {
        if (subscriber.apply(value)) {
            this.subscribers.add(subscriber);
        }
    }

    public void unsubscribe(Function<Optional<T>, Boolean> subscriber) {
        this.subscribers.remove(subscriber);
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
