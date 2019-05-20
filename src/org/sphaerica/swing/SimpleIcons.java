package org.sphaerica.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This enum provides simple geometric line drawing icons.
 */
public enum SimpleIcons implements Icon {


    RECT_DOTTED {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(1, 0, 0, 1, new float[]{2, 2}, 0));
            g.drawRect(x + r, y + r, w - r - r, h - r - r);
        }
    }, RECT_DOTTED_X {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(1, 0, 0, 1, new float[]{2, 2}, 0));
            g.drawRect(x + r, y + r, w - r - r, h - r - r);
            g.drawLine(x + r, y + r, x + w - r, y + h - r);
            g.drawLine(x + r, y + h - r, x + w - r, y + r);
        }
    }, CIRCLE_DOTTED {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(1, 0, 0, 1, new float[]{2, 2}, 0));
            g.drawOval(x + r, y + r, w - r - r, h - r - r);
        }
    }, CIRCLE_SOLID {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(1));
            g.drawOval(x + r, y + r, w - r - r, h - r - r);
        }
    }, TRIANGLE {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(1));
            g.drawLine(x + 6, y + h - 6, x + w / 2, 6 + r);
            g.drawLine(x + w - 6, y + h - 6, x + w / 2, 6 + r);
            g.drawLine(x + 6, y + h - 6, x + w - 6, y + h - 6);
        }
    }, X {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            final int r = 6;
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(1));
            g.drawLine(x + r, y + r, x + w - r, y + h - r);
            g.drawLine(x + r, y + h - r, x + w - r, y + r);
        }
    }, PLUS {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            final int r = 6;
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.drawLine(x + w / 2, y + r, x + w / 2, y + h - r);
            g.drawLine(x + r, y + h / 2, x + w - r, y + h / 2);
        }
    };

    private static final int w = 22, h = 22, r = 5;

    @Override
    public int getIconHeight() {
        return h;
    }

    @Override
    public int getIconWidth() {
        return w;
    }

}
