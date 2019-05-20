package org.sphaerica.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This icon displays a square of the color given in parameter.
 */
public class ColorIcon implements Icon {
    final Color color;

    public ColorIcon(Color c) {
        color = c;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int w = getIconWidth(), h = getIconHeight();
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);

        g.setColor(Color.BLACK);
        g.fillRect(x, y, w / 2, h / 2);
        g.fillRect(x + w / 2, y + h / 2, w / 2, h / 2);

        g.setColor(color);
        g.fillRect(x, y, w, h);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
    }

    @Override
    public int getIconWidth() {
        return 12;
    }

    @Override
    public int getIconHeight() {
        return 12;
    }
}