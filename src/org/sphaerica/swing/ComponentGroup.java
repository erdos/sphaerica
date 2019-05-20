package org.sphaerica.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Thic component is a styled panel containing other components in a horizontal
 * equal area layout. The default style displays a white rounded rectangle in
 * the background.
 */
@SuppressWarnings("serial")
public class ComponentGroup extends JPanel {

    public ComponentGroup(JComponent... components) {
        setLayout(new GridLayout(1, 0));
        for (JComponent c : components)
            add(c);
        setBorder(new EmptyBorder(1, 1, 1, 1));
        setOpaque(false);
        setBackground(Color.white);

    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(lighten(getBackground()));
        Rectangle r = getBounds();
        int radius = 12;
        g2d.fillRoundRect(0, 0, r.width, r.height, radius, radius);
    }

    /**
     * Creates a brighter version of the color given
     *
     * @param c color
     * @return brighter color
     */
    public Color lighten(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
    }
}
