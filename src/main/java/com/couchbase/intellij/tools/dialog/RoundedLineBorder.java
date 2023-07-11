package com.couchbase.intellij.tools.dialog;

import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

public class RoundedLineBorder extends LineBorder {

    private final int arcLength;

    public RoundedLineBorder(Color color, int thickness, int arcLength, boolean roundedCorners) {
        super(color, thickness, roundedCorners);
        this.arcLength = arcLength;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(getThickness()));

        int arcDiameter = arcLength;
        Shape outer = new RoundRectangle2D.Double(x, y, width - 1, height - 1, arcDiameter, arcDiameter);
        Area area = new Area(outer);
        //area.subtract(new Area(inner));

        g2.setColor(getLineColor());
        g2.draw(area);
        g2.dispose();
    }
}