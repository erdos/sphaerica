package org.sphaerica.swing;

import org.sphaerica.data.Resources;
import org.sphaerica.util.TangoColorScheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This factory class creates gui panels for various purposes.
 */
public class InfoPanelFactory {

    public final static JComponent createStatusPanel(String msg,
                                                     ActionListener lis) {
        final JComponent container = new JPanel();
        container.setLayout(new BorderLayout());

        container.setBackground(TangoColorScheme.Butter1.color.brighter());
        container.setOpaque(true);

        Border emptyBorder = new EmptyBorder(8, 8, 8, 6);
        Border lineBorder = BorderFactory.createMatteBorder(0, 0, 1, 0,
                TangoColorScheme.Butter1.color);
        container.setBorder(BorderFactory.createCompoundBorder(lineBorder,
                emptyBorder));

        final JLabel messagePanel = new JLabel(msg);
        final JButton closeButton = IconButton.createCloseButton(lis);

        container.add(closeButton, BorderLayout.EAST);
        container.add(messagePanel, BorderLayout.CENTER);

        return container;
    }

    /**
     * Builds a panel displaying information on a throwable instance. This panel
     * also offers an inspect button for details. This panel also offers a close
     * button that removes it from its container component.
     *
     * @param res       resources instance used for text translations
     * @param throwable object to display information about
     * @return panel displaying info on throwable
     */
    public final static JComponent createErrorPanel(final Resources res,
                                                    final Throwable throwable) {
        final JComponent container = new JPanel();
        container.setLayout(new BorderLayout());

        Border emptyBorder = new EmptyBorder(8, 8, 8, 6);
        Border lineBorder = BorderFactory.createMatteBorder(0, 0, 1, 0,
                TangoColorScheme.ScarletRed3.color);
        container.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
        container.setBackground(TangoColorScheme.ScarletRed1.color);


        JLabel text = new JLabel();
        container.add(text, BorderLayout.CENTER);

        String message = throwable.getLocalizedMessage();
        //message = message.replaceFirst(".*?\\:(.*?)\\(\\<Unknown.*", "$1");

        text.setText(message);

        JButton inspectButton = res.register(new JButton(), "inspect");
        //inspectButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 12));
        inspectButton.setPreferredSize(new Dimension((int) inspectButton.getPreferredSize().getWidth(), (int) text.getMinimumSize().getHeight()));
        inspectButton.setMargin(null);
        JButton closeButton = IconButton.createCloseButton(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new FadeComponent(container);
            }
        });

        inspectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel msg = new JLabel("<html><p>"
                        + throwable.getLocalizedMessage() + "</p></html>");
                msg.setPreferredSize(new Dimension(300, 60));
                JList trace = new JList(throwable.getStackTrace());
                JOptionPane.showMessageDialog(container,
                        ColumnLayout.createPanel(msg, trace));
            }
        });
        container.add(PanelFactory.createFlowPanel(FlowLayout.LEFT, 0, 0, inspectButton, closeButton), BorderLayout.EAST);
        //container.add(inspectButton, BorderLayout.WEST);
        container.add(text, BorderLayout.WEST);
        //container.add(closeButton, BorderLayout.EAST);

        return container;
    }

}
