package org.sphaerica.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 * This enum is a collection of icons for a close button. This is simple a
 * monochrome round icon.
 */
public enum CloseIcon implements Icon {

    DARK(new Color(12, 12, 12, 62)), NORMAL(new Color(12, 12, 12, 42)), DARKEST(
            new Color(12, 12, 12, 255));

    private final Color color;

    CloseIcon(Color c) {
        this.color = c;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);

        int rr = 8;

        Area a1 = new Area(new Ellipse2D.Float(2, 2, 18, 18));
        a1.subtract(new Area(new BasicStroke(3)
                .createStrokedShape(new Line2D.Float(+rr, +rr, +22 - rr, +22
                        - rr))));
        a1.subtract(new Area(new BasicStroke(3)
                .createStrokedShape(new Line2D.Float(+rr, +22 - rr, +22 - rr,
                        +rr))));

        g2d.fill(a1);
    }

    @Override
    public int getIconWidth() {
        return 22;
    }

    @Override
    public int getIconHeight() {
        return 22;
    }
}
