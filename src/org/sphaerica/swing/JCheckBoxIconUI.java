package org.sphaerica.swing;

import javax.swing.*;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import java.awt.*;

/**
 * This class is an alternative user interface for check boxes. It displays an
 * icon for each state of the check box.
 */
public class JCheckBoxIconUI extends BasicCheckBoxUI {
    private final Icon on, off;
    private JCheckBox checkbox;

    /**
     * Creates an ui displaying and icon on each state of the check box it is
     * installed to.
     *
     * @param on  icon to show when box is checked
     * @param off icon to show when box is not checked
     */
    public JCheckBoxIconUI(Icon on, Icon off) {
        this.on = on;
        this.off = off;
    }

    public void installUI(final JComponent c) {
        this.checkbox = (JCheckBox) c;
        c.setBorder(null);
        c.setOpaque(false);

        c.setPreferredSize(this.getPreferredSize(c));

        super.installUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = c.getWidth(), h = c.getHeight();
        int iw = on.getIconWidth(), ih = on.getIconHeight();

        Icon ico = this.checkbox.isSelected() ? on : off;
        ico.paintIcon(this.checkbox, g, (w - iw) / 2, (h - ih) / 2);
    }

    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(32, 32);
    }
}