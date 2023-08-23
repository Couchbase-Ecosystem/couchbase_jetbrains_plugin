package com.couchbase.intellij.tree;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public class Mouse implements MouseListener {

    public static MouseListener click(Consumer<MouseEvent> action) {
        return new Mouse() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                action.accept(mouseEvent);
            }
        };
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
