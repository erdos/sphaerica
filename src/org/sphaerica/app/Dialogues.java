package org.sphaerica.app;

import javax.annotation.Resources;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

/**
 * A helper class for displaying gui dialoque windows.
 */
public class Dialogues {

    /**
     * HTML file displayed in the About dialog window.
     */
    private final static String ABOUT_RESOURCE = "/about.html";

    /**
     * Displays the default "About" window with information about the current
     * version, environment, etc.
     *
     * @param parent Component or null.
     */
    static void showAboutDialog(Component parent) {

        JEditorPane about = new JEditorPane();
        about.setBorder(new EmptyBorder(5, 5, 5, 5));
        // about.setOpaque(true);
        about.setEditable(false);

        try {
            about.setPage(Resources.class.getResource(ABOUT_RESOURCE));
        } catch (IOException e) {
            about.setText(e.getLocalizedMessage());
            e.printStackTrace();
        }
        JScrollPane scroller = new JScrollPane(about,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBorder(null);

        JPanel inner = new JPanel(new BorderLayout());
        inner.add(BorderLayout.CENTER, scroller);

        /*
         * Runtime r = Runtime.getRuntime(); JLabel info = new
         * JLabel(String.format("mem: %.3fMb/%.3fMb", 1d * r.freeMemory() / 1024
         * / 1024, 1d * r.maxMemory() / 1024 / 1024));
         * inner.add(BorderLayout.SOUTH, info);
         */

        JFrame window = new JFrame();
        window.setLocation(-1, -1);
        window.setResizable(false);
        window.setContentPane(inner);

        // please consider reusing windows.
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        final int width = 320, height = 480;
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 2;
        window.setLocation(x, y);
        window.setSize(width, height);

        window.setVisible(true);

        // JOptionPane.showMessageDialog(parent, ColumnLayout.createPanel(about,
        // info));

    }

    /**
     * Displays default error dialog.
     *
     * @param parent frame in which the dialog will be displayed. If null, no
     *               parent frame will be used.
     * @param msg    message to display to user.
     */
    public static void showErrorDialog(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, null,
                JOptionPane.ERROR_MESSAGE);
    }
}
