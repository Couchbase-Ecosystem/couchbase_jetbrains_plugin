package com.couchbase.intellij.tools.dialog;

import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;

public class CustomArrowButton extends BasicArrowButton {
    private static final int ICON_SIZE = 16;
    private static final int ICON_OFFSET_X = 4;

    public CustomArrowButton(int direction, Color background, Color shadow, Color darkShadow, Color highlight) {
        super(direction, background, shadow, darkShadow, highlight);
        setPreferredSize(new Dimension(ICON_SIZE + ICON_OFFSET_X, ICON_SIZE));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(ICON_OFFSET_X, 0);
        super.paint(g2);
        g2.dispose();
    }
}