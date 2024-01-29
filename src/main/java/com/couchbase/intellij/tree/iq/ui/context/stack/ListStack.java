package com.couchbase.intellij.tree.iq.ui.context.stack;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.InputEvent;

/**
 * A stack displaying a list of items (or other actions).
 *
 */
public interface ListStack {

    /**
     * Returns the popup step currently displayed in the popup.
     *
     * @return the popup step.
     */
    ListStackStep getListStep();

    ListStackModel getListModel();

    JList getList();

    /**
     * Handles the selection of the currently focused item in the popup step.
     *
     * @param handleFinalChoices If true, the action of the focused item is always executed
     * (as if Enter was pressed). If false, and the focused item has a submenu, the submenu
     * is opened (as if the right arrow key was pressed).
     */
    void handleSelect(boolean handleFinalChoices);

    void handleSelect(boolean handleFinalChoices, InputEvent e);

    void addListSelectionListener(ListSelectionListener listSelectionListener);

    default boolean isShowSubmenuOnHover() {
        return false;
    }

    boolean beforeShow();
}
