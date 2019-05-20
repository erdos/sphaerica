package org.sphaerica.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This class provides helper methods for building JPanel instances with layout
 * managers preset.
 */
public class PanelFactory {


    /**
     * Creates a new panel with a flow layout of the given parameters and components added.
     *
     * @param alignment
     * @param hgap
     * @param vgap
     * @param children
     * @return panel object with flow layout
     */
    public final static JPanel createFlowPanel(int alignment, int hgap, int vgap,
                                               JComponent... children) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(alignment, hgap, vgap));
        panel.setOpaque(false);
        for (JComponent c : children)
            panel.add(c);
        return panel;
    }

    /**
     * Creates a new panel with a flow layout of the given alignment and the
     * components added to it.
     *
     * @param alignment alignment for the FlowLayout layout manager.
     * @param compo     array of components to add.
     */
    public final static JPanel createFlowPanel(int alignment,
                                               JComponent... children) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(alignment));
        panel.setOpaque(false);
        for (JComponent c : children)
            panel.add(c);
        return panel;
    }

    /**
     * Creates a panel with flow layout centered and adds given components to
     * it.
     *
     * @param children components to add.
     * @return panel instance.
     */
    public final static JPanel createFlowPanel(JComponent... children) {
        return createFlowPanel(FlowLayout.CENTER, children);
    }

    /**
     * Creates a panel with grid layout of the given parameters and adds
     * components to it.
     *
     * @param x        number of rows
     * @param y        number of cols
     * @param xgap     vertical gap
     * @param ygap     horizontal gap
     * @param children array of children to add
     * @return panel with grid layout
     */
    public final static JPanel createGridPanel(int x, int y, int xgap,
                                               int ygap, JComponent... children) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(x, y, xgap, ygap));
        panel.setOpaque(false);
        for (JComponent c : children)
            panel.add(c);
        return panel;
    }

}
