package com.couchbase.intellij.tree.iq.ui.context.stack;

public interface SpeedSearchFilter<T> {
  default boolean canBeHidden(T value) {
    return true;
  }

  String getIndexedString(T value);
}
