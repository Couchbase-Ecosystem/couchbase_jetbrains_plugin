package com.couchbase.intellij.tree.iq.ui.listener;

import com.couchbase.intellij.tree.iq.core.SendAction;
import com.couchbase.intellij.tree.iq.ui.CouchbaseIQPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * @author Wuzi
 */
public class SendListener implements ActionListener,KeyListener {

    private final CouchbaseIQPanel couchbaseIQPanel;

    public SendListener(CouchbaseIQPanel couchbaseIQPanel) {
        this.couchbaseIQPanel = couchbaseIQPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            doActionPerformed();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void doActionPerformed() throws IOException {
        String text = couchbaseIQPanel.getSearchTextArea().
                getTextArea().getText();
        SendAction sendAction = couchbaseIQPanel.getProject().getService(SendAction.class);
        sendAction.doActionPerformed(couchbaseIQPanel,text);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER && !e.isControlDown() && !e.isShiftDown()){
            e.consume();
            couchbaseIQPanel.getButton().doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
