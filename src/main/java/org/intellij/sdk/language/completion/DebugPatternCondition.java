package org.intellij.sdk.language.completion;

import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DebugPatternCondition<T> extends PatternCondition<T> {
    public DebugPatternCondition() {
        super("debug condition");
    }

    @Override
    public boolean accepts(@NotNull T t, ProcessingContext context) {
        return true;
    }
}
