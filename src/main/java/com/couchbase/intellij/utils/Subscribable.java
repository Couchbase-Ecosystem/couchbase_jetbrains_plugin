package com.couchbase.intellij.utils;

import cn.hutool.core.stream.StreamUtil;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
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
        synchronized (this) {
            this.value = Optional.ofNullable(value);
            subscribers = subscribers.entrySet().stream().filter(subscriber -> subscriber.getKey() != null && subscriber.getKey().get() != null && subscriber.getValue() != null).filter(subscriber -> {
                try {
                    return subscriber.getValue().apply(this.value);
                } catch (Throwable e) {
                    return false;
                }
            }).collect(Collectors.toMap(s -> s.getKey(), s -> s.getValue()));
        }
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
        try {
            return Optional.of(get(0));
        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }

    public T get(long millis) throws InterruptedException {
        synchronized (this) {
            if (!value.isPresent()) {
                wait(millis);
            }
            return value.get();
        }
    }

    public void get(Consumer<Optional<T>> subscriber) {
        if (isPresent()) {
            subscriber.accept(value);
        } else {
            subscribe(subscriber, t -> {
                subscriber.accept(t);
                return false;
            });
        }
    }

    public boolean isPresent() {
        return value.isPresent();
    }

    public T getValue() {
        return value.orElse(null);
    }
}
