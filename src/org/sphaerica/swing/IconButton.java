package org.sphaerica.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is a general class for graphical icon only push buttons.
 */
@SuppressWarnings("serial")
public class IconButton extends JButton {

    /**
     * Creates button with icon and action command.
     *
     * @param icon    icon to display on button
     * @param command action command string to send to action listeners
     */
    public IconButton(Icon icon, String command) {
        super(icon);
        setActionCommand(command);
        setUI(new SimpleButtonUI());
    }

    /**
     * Creates button with default action.
     *
     * @param action action object of icon
     */
    public IconButton(Action action) {
        super(action);
        setText(null);
        setUI(new SimpleButtonUI());
    }

    /**
     * Creates icon button with popup menu to display on click.
     *
     * @param icon icon of button
     * @param menu popup menu displayed on button click
     */
    public IconButton(Icon icon, final JPopupMenu menu) {
        this(icon, "menu");
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menu.show((Component) e.getSource(), 0, 0);
            }
        });
    }

    /**
     * Creates icon button with predefined icon, command string and action
     * listener.
     *
     * @param icon     icon to display on buttonm
     * @param command  action command string to send to action listeners
     * @param listener default action listener to add to button
     */
    public IconButton(Icon icon, String command, ActionListener listener) {
        this(icon, command);
        addActionListener(listener);
    }

    /**
     * Creates a default close button with action string "close" and nice icons.
     *
     * @param listener default action listener to add to button
     * @return custom close icon button
     */
    public static final JButton createCloseButton(ActionListener listener) {
        JButton button = new JButton();
        button.setActionCommand("close");
        button.addActionListener(listener);

        button.setRolloverIcon(CloseIcon.DARK);
        button.setIcon(CloseIcon.NORMAL);
        button.setUI(new SimpleButtonUI());

        return button;
    }

    /**
     * A simple button ui with no borders and margins.
     */
    public final static class SimpleButtonUI extends BasicButtonUI {
        protected void installDefaults(AbstractButton b) {
            b.setMargin(new Insets(0, 0, 0, 0));
            b.setBorderPainted(false);
            b.setBorder(new EmptyBorder(0, 0, 0, 0));
            b.setContentAreaFilled(false);
            b.setFocusable(false);

            super.installDefaults(b);
        }
    }
}
