/*
 * Copyright (c) 2023 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.couchbase.intellij.tree.iq.settings;

import com.couchbase.intellij.tree.iq.ChatGptBundle;
import com.intellij.openapi.options.Configurable;

public class GPT3_35_TurboPanel extends ModelPagePanel implements Configurable {

    public GPT3_35_TurboPanel() {
    }

    @Override
    protected OpenAISettingsState.OpenAIConfig getModelPageConfig(OpenAISettingsState state) {
        return state.getGpt35Config();
    }

    @Override
    public String getDisplayName() {
        return ChatGptBundle.message("ui.setting.menu.text");
    }
}
