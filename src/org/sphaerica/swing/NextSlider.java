package org.sphaerica.swing;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * An enchanced scroller/slider java swing gui component.
 */
@SuppressWarnings("serial")
public class NextSlider extends JPanel implements ActionListener {
    private float min = 100, max = 800, value = min, tip = 120;
    private final List<ActionListener> listeners = new ArrayList<ActionListener>();

    private boolean hover = false, clicked = false;
    private String actionCommand = "adjust";

    public final static <T extends JComponent> T installBorders(T compo) {
        final int top = 11, left = 4, right = 24, bottom = 11;
        compo.setBorder(new CompoundBorder(new EmptyBorder(top, left, bottom, right), new LineBorder(Color.gray)));

        return compo;
    }

    public NextSlider(float min, float max, float value, float tip) {
        this();
        this.min = min;
        this.max = max;
        this.value = value;
        this.tip = tip;
    }

    public NextSlider() {
        super();
        listeners.add(this);

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (clicked)
                    calculateValue(e.getPoint().x);
            }
        });
        addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                clicked = false;
            }

            public void mousePressed(MouseEvent e) {
                if (box().contains(e.getPoint())) {
                    clicked = true;
                    calculateValue(e.getPoint().x);
                }
            }

            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });

        setOpaque(true);
    }

    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }


    private void calculateValue(float x) {
        Insets in = getInsets();
        int tipW = tip().width;
        assignValue(((x - in.left) - (tipW) / 2) / (getWidth() - in.left - in.right - tipW) * (max - min) + min);
        repaint();
    }

    public final boolean isHovered() {
        return hover;
    }

    protected Rectangle tip() {
        Insets in = getInsets();

        int w = (int) (tip / (max - min) * (getWidth() - in.left - in.right));
        int x = (int) ((getWidth() - in.left - in.right - w) * (getValue() - min) / (max - min)) + in.left;

        return new Rectangle(x, in.top, w, getHeight() - in.top - in.bottom);
    }

    protected Rectangle box() {
        Insets in = getInsets();
        return new Rectangle(in.left, in.top, getWidth() - in.left - in.right, getHeight() - in.top - in.bottom);
    }

    public void paintComponent(Graphics g) {
        paintBackground(g);
        paintTip(g);
    }

    public void paintBackground(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        final Rectangle box = box();
        g.setColor(new Color(230, 230, 230));
        g2d.setPaint(new LinearGradientPaint(0, box.y, 0, box.height + box.y, new float[]{0.5f, 1}, new Color[]{new Color(0xfafafa), new Color(0xdddddd)}));
        g.fillRect(box.x, box.y, box.width, box.height);
    }

    public void paintTip(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        final Rectangle tip = tip();
        //g2d.setPaint(new LinearGradientPaint(0, box.y, 0, box.height + box.y, new float[] { 0.5f, 1 }, new Color[] { new Color(0x222a1d).brighter(), new Color(0x212111).brighter() }));
        g2d.setPaint(new Color(12, 12, 11, 192));

        g2d.fillRect(tip.x, tip.y, tip.width, tip.height);
    }

    /**
     * Sets current value of slider to argument and fires action listeners.
     *
     * @param f float val to assign.
     */
    public void assignValue(float f) {
        setValue(f);

        final ActionEvent event = new ActionEvent(NextSlider.this, 0, actionCommand);
        for (ActionListener listener : listeners)
            listener.actionPerformed(event);

        repaint();
    }

    /**
     * Sets current value of slider to argument without firing listeners.
     *
     * @param f float val to assign.
     */
    public void setValue(float f) {
        value = Math.min(Math.max(f, min), max);
    }

    /**
     * Increases value of slider by argument.
     *
     * @param f
     */
    public final void increaseValue(float f) {
        setValue(getValue() + f);
    }

    /**
     * Decreases value of slider by argument.
     *
     * @param f
     */
    public final void decreaseValue(float f) {
        setValue(getValue() - f);
    }

    /**
     * Returns value of slider.
     *
     * @return value of slider
     */
    public float getValue() {
        return value;
    }

    /**
     * Default event handler. The default implementation is empty.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * Sets action command to send to action listeners.
     *
     * @param str action command string
     */
    public void setActionCommand(String str) {
        actionCommand = str;
    }

    public String getActionCommand() {
        return actionCommand;
    }
}
