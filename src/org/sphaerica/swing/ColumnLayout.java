package org.sphaerica.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This layout displays its components width the same width in one column.
 */
public class ColumnLayout implements LayoutManager {

    /**
     * Vertical gap is the space between the components.
     */
    int vgap = 0;

    /**
     * Creates the layout manager with vertical gap of 0.
     */
    public ColumnLayout() {
    }

    /**
     * Creates the layout manager with the given vertical gap. That is the space
     * between the components in this layout.
     *
     * @param vgap vertical gap.
     */
    public ColumnLayout(int vgap) {
        this.vgap = vgap;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets i = parent.getInsets();
        int y = i.top;

        for (Component comp : parent.getComponents()) {
            if (!comp.isVisible())
                continue;

            comp.setBounds(i.left, y,
                    parent.getSize().width - i.left - i.right,
                    comp.getPreferredSize().height);
            y += comp.getHeight() + vgap;
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        int w = 0, h = 0;
        Insets i = parent.getInsets();
        for (Component comp : parent.getComponents()) {
            if (!comp.isVisible())
                continue;

            w = Math.max(comp.getPreferredSize().width, w);
            h += comp.getPreferredSize().height + vgap;
        }
        return new Dimension(w + i.left + i.right, h + i.top + i.bottom - vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return minimumLayoutSize(parent);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    public static JPanel createPanel(JComponent... components) {
        JPanel panel = new JPanel(new ColumnLayout());
        for (JComponent c : components)
            panel.add(c);
        return panel;
    }

}
