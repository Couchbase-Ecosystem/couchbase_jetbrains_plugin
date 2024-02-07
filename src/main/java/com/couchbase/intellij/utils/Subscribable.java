package com.couchbase.intellij.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Subscribable<T> {
    private Optional<T> value;
    private List<WeakReference<Function<Optional<T>, Boolean>>> subscribers = new ArrayList<>();

    public Subscribable() {
        value = Optional.empty();
    }

    public Subscribable(T value) {
        this.value = Optional.ofNullable(value);
    }

    public void set(T value) {
        this.value = Optional.ofNullable(value);
        subscribers = subscribers.stream()
                .filter(subscriber -> subscriber.get() != null)
                .filter(subscriber -> subscriber.get().apply(this.value))
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
            this.subscribers.add(new WeakReference<>(subscriber));
        }
    }

    public void unsubscribe(Function<Optional<T>, Boolean> subscriber) {
        this.subscribers.retainAll(this.subscribers.stream()
                .filter(ref -> ref.get() != null)
                .filter(ref -> !Objects.equals(subscriber, ref.get()))
                        .collect(Collectors.toList()));
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
