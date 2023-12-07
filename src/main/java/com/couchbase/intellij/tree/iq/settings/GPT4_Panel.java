/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.settings;

import com.couchbase.intellij.tree.iq.ChatGptBundle;
import com.intellij.openapi.options.Configurable;

public class GPT4_Panel extends ModelPagePanel implements Configurable {

    public GPT4_Panel() {
    }

    @Override
    protected OpenAISettingsState.OpenAIConfig getModelPageConfig(OpenAISettingsState state) {
        return state.getGpt4Config();
    }

    @Override
    public String getDisplayName() {
        return ChatGptBundle.message("ui.setting.menu.text");
    }
}
