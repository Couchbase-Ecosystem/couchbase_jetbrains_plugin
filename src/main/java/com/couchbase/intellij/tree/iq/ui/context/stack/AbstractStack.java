package com.couchbase.intellij.tree.iq.ui.context.stack;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.CaptionPanel;
import com.intellij.ui.LightColors;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.TitlePanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.speedSearch.SpeedSearch;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AbstractStack {

    private static final Logger LOG = Logger.getInstance(AbstractStack.class);

    private CaptionPanel myCaption;
    private JComponent myComponent;
    private Project myProject;
    private boolean myHeaderAlwaysFocusable;

    protected final SpeedSearch mySpeedSearch = new SpeedSearch() {
        boolean searchFieldShown;

        @Override
        public void update() {
            updateSpeedSearchColors(false);
            onSpeedSearchPatternChanged();
            mySpeedSearchPatternField.setText(getFilter());
            if (isHoldingFilter() && !searchFieldShown) {
                searchFieldShown = true;
            }
            else if (!isHoldingFilter() && searchFieldShown) {
                searchFieldShown = false;
            }
        }

        @Override
        public void noHits() {
            updateSpeedSearchColors(true);
        }
    };

    protected void updateSpeedSearchColors(boolean error) {
        JBTextField textEditor = mySpeedSearchPatternField.getTextEditor();
        textEditor.setBackground(error ? LightColors.RED : UIUtil.getTextFieldBackground());
    }

    protected SearchTextField mySpeedSearchPatternField;

    private volatile State myState = State.NEW;

    private enum State {NEW, INIT, SHOWING, SHOWN, CANCEL, DISPOSE}

    private void debugState(@NonNls @NotNull String message, State @NotNull ... states) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(hashCode() + " - " + message);
            if (!ApplicationManager.getApplication().isDispatchThread()) {
                LOG.debug("unexpected thread");
            }
            for (State state : states) {
                if (state == myState) {
                    return;
                }
            }
            LOG.debug(new IllegalStateException("myState=" + myState));
        }
    }

    protected AbstractStack() { }

    protected @NotNull AbstractStack init(Project project,
                                          @NotNull JComponent component,
                                          boolean requestFocus,
                                          boolean focusable,
                                          @NlsContexts.PopupTitle @Nullable String caption,
                                          boolean headerAlwaysFocusable) {
        assert !requestFocus || focusable : "Incorrect argument combination: requestFocus=true focusable=false";

        myProject = project;
        myComponent = component;
        myHeaderAlwaysFocusable = headerAlwaysFocusable;

        JPanel myHeaderPanel = new JPanel(new BorderLayout()) {
            @Override
            public Color getBackground() {
                return JBUI.CurrentTheme.Popup.headerBackground(true);
            }
        };

        if (caption != null) {
            if (!caption.isEmpty()) {
                TitlePanel titlePanel = new TitlePanel();
                titlePanel.setText(caption);
                myCaption = titlePanel;
            }
            else {
                myCaption = new CaptionPanel();
            }
        }
        else {
            myCaption = new CaptionPanel();
            myCaption.setBorder(null);
            myCaption.setPreferredSize(JBUI.emptySize());
        }

        setWindowActive(myHeaderAlwaysFocusable);

        myHeaderPanel.add(myCaption, BorderLayout.NORTH);

        debugState("popup initialized", State.NEW);
        myState = State.INIT;
        return this;
    }

    private void setWindowActive(boolean active) {
        boolean value = myHeaderAlwaysFocusable || active;

        if (myCaption != null) {
            myCaption.setActive(value);
        }
    }

    public boolean isVisible() {
        return true;
    }

    protected boolean beforeShow() {
        return true;
    }

    protected void afterShow() {
    }

    public JComponent getComponent() {
        return myComponent;
    }

    public Project getProject() {
        return myProject;
    }

    public @NotNull CaptionPanel getTitle() {
        return myCaption;
    }

    protected void onSpeedSearchPatternChanged() {
    }
}
