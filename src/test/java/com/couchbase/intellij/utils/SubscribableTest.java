package com.couchbase.intellij.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class SubscribableTest {

    @Test
    public void emptyByDefault() {
        Subscribable<String> s = new Subscribable<>();
        assertFalse(s.isPresent());
        assertNull(s.getValue());
    }

    @Test
    public void constructorWithNullValueIsEmpty() {
        Subscribable<String> s = new Subscribable<>(null);
        assertFalse(s.isPresent());
        assertNull(s.getValue());
    }

    @Test
    public void setStoresValueAndNullClearsIt() {
        Subscribable<String> s = new Subscribable<>("a");
        assertEquals("a", s.getValue());
        s.set(null);
        assertFalse(s.isPresent());
        assertNull(s.getValue());
    }

    @Test
    public void getReturnsPresentValue() throws Exception {
        assertEquals(Optional.of("a"), new Subscribable<>("a").get());
        assertEquals("a", new Subscribable<>("a").get(10));
    }

    @Test(expected = NoSuchElementException.class)
    public void timedGetOnEmptyThrowsAfterTimeout() throws Exception {
        new Subscribable<String>().get(1);
    }

    @Test
    public void subscriberIsInvokedImmediatelyAndOnEverySet() {
        Subscribable<String> s = new Subscribable<>("v");
        AtomicInteger calls = new AtomicInteger();
        Object key = new Object();
        s.subscribe(key, opt -> {
            calls.incrementAndGet();
            return true;
        });
        assertEquals(1, calls.get());
        s.set("w");
        assertEquals(2, calls.get());
    }

    @Test
    public void subscriberReturningFalseIsDroppedOnNextSet() {
        Subscribable<String> s = new Subscribable<>("v");
        AtomicInteger calls = new AtomicInteger();
        Object key = new Object();
        s.subscribe(key, opt -> calls.incrementAndGet() < 2);
        assertEquals(1, calls.get());
        s.set("w");
        assertEquals(2, calls.get());
        s.set("x");
        assertEquals(2, calls.get());
    }

    @Test
    public void subscriberThatThrowsIsDroppedWithoutPropagating() {
        Subscribable<String> s = new Subscribable<>("v");
        AtomicInteger calls = new AtomicInteger();
        Object key = new Object();
        s.subscribe(key, opt -> {
            if (calls.incrementAndGet() > 1) {
                throw new IllegalStateException("boom");
            }
            return true;
        });
        s.set("w");
        assertEquals(2, calls.get());
        s.set("x");
        assertEquals(2, calls.get());
    }

    @Test
    public void unsubscribeStopsNotifications() {
        Subscribable<String> s = new Subscribable<>("v");
        AtomicInteger calls = new AtomicInteger();
        Object key = new Object();
        s.subscribe(key, opt -> {
            calls.incrementAndGet();
            return true;
        });
        s.unsubscribe(key);
        s.set("w");
        assertEquals(1, calls.get());
    }

    @Test
    public void subscribingSameKeyReplacesPreviousSubscriber() {
        Subscribable<String> s = new Subscribable<>("v");
        AtomicInteger first = new AtomicInteger();
        AtomicInteger second = new AtomicInteger();
        Object key = new Object();
        s.subscribe(key, opt -> {
            first.incrementAndGet();
            return true;
        });
        s.subscribe(key, opt -> {
            second.incrementAndGet();
            return true;
        });
        s.set("w");
        assertEquals(1, first.get());
        assertEquals(2, second.get());
    }

    @Test
    public void consumerGetOnPresentValueInvokesImmediately() {
        Subscribable<String> s = new Subscribable<>("v");
        List<Optional<String>> seen = new ArrayList<>();
        s.get(seen::add);
        assertEquals(List.of(Optional.of("v")), seen);
    }

    @Test
    public void consumerGetOnEmptyIsNotifiedOnlyOnce() {
        // NOTE: possible bug: get(Consumer) on an empty Subscribable calls the consumer immediately with
        // Optional.empty(), but the wrapper returns false so the subscription is never stored and the
        // consumer never receives the value later.
        Subscribable<String> s = new Subscribable<>();
        List<Optional<String>> seen = new ArrayList<>();
        s.get(seen::add);
        s.set("later");
        assertEquals(List.of(Optional.empty()), seen);
    }
}
