package com.couchbase.intellij.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CouchbaseConfigurable implements Configurable {

    private CoucbaseSettingsComponent settingsComponent;

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Couchbase Plugin";
    }

    @Override
    public @Nullable JComponent createComponent() {
        if (settingsComponent == null) {
            settingsComponent = new CoucbaseSettingsComponent();
        }
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        if (settingsComponent == null) {
            return false;
        }
        return settingsComponent.isModified();
    }

    @Override
    public void apply() {
        if (settingsComponent != null) {
            settingsComponent.apply();
        }
    }

    @Override
    public void reset() {
        if (settingsComponent != null) {
            settingsComponent.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }
}