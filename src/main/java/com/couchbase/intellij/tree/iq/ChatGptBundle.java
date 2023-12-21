package com.couchbase.intellij.tree.iq;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class ChatGptBundle extends DynamicBundle {

    @NonNls
    private static final String BUNDLE = "messages.ChatGptBundle";
    private static final ChatGptBundle INSTANCE = new ChatGptBundle();

    private ChatGptBundle() {
        super(BUNDLE);
    }

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }
}
