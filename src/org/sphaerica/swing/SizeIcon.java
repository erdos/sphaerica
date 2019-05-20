package org.sphaerica.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This class displays an icon displaying an in value representing a line width.
 */
class SizeIcon implements Icon {
    final int width;

    SizeIcon(int w) {
        width = w;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int w = getIconWidth(), h = getIconHeight();
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);

        g.setColor(Color.BLACK);
        g.fillOval(x + getIconWidth() / 2 - width, y + getIconHeight() / 2
                - width, width * 2, width * 2);
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