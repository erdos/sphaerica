package org.sphaerica.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * This class creates a nice fading animation to hide a swing component.
 */
@SuppressWarnings("serial")
public class FadeComponent extends JComponent implements ActionListener {

    final BufferedImage image;

    /**
     * Current status of the animation. Between 0 and 1.
     */
    double status = 1;

    /**
     * Change of transparency in one step of animation.
     */
    public final static double delta = 1.0 / 8; // speed of the animation


    /**
     * Number of animation steps in a second.
     */
    public final static int fps = 50;

    final Timer t = new Timer(fps, this);

    /**
     * Starts fading a component.
     *
     * @param component component to face. null param is not permitted.
     */
    public FadeComponent(JComponent component) {
        if (component == null)
            throw new IllegalArgumentException("Can not fade null component.");
        final JComponent parent = (JComponent) component.getParent();

        image = new BufferedImage(component.getWidth(), component.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);
        setPreferredSize(component.getSize());

        component.paint(image.getGraphics());

        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) != component)
                continue;
            parent.add(this, i);
            parent.remove(component);
            revalidate();
            break;
        }
        t.setRepeats(true);
        t.start();
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setComposite(AlphaComposite.SrcOver.derive((float) status));
        g.drawImage(image, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (status <= 0)
            destroy();
        else
            step();
    }

    /**
     * Removes the component from its parent and revalidates the parent.
     */
    private void destroy() {
        t.stop();
        JComponent comp = (JComponent) getParent();
        comp.remove(this);
        comp.validate();
    }

    /**
     * Linear fading step.
     */
    private void step() {
        status = Math.max(0, status - delta);
        setPreferredSize(new Dimension(image.getWidth(),
                (int) (image.getHeight() * status)));
        revalidate();
    }
}
