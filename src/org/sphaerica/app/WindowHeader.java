package org.sphaerica.app;

import org.sphaerica.swing.ComponentGroup;
import org.sphaerica.swing.IconButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Header component for the main window of the program. Normally, you can find
 * this component at the north side of the window. This component displays the
 * construction tools and several other buttons, such as a scripter panel
 * button, undo/redo buttons and the animator button.
 */
@SuppressWarnings("serial")
public class WindowHeader extends JPanel implements ActionListener {

    private final static Color BORDER_BOTTOM = new Color(118, 124, 128);

    private final JComponent cc;
    private final CardLayout topLayout;
    private final JComponent advBg, top, sc;

    private final ApplicationWindow parent;

    WindowHeader(ApplicationWindow w) {
        this.parent = w;

        this.setPreferredSize(new Dimension(-1, 48));

        sc = createScripterPanel();

        topLayout = new CardLayout();
        top = new JPanel(topLayout);
        cc = new ConstructionPanelBuilder(parent).buildPanel();

        top.add(cc, "constructor");
        top.add(sc, "scripter");
        top.add(new JLabel("Please, select a point on the sphere!"), "dialog");

        final JButton advancedMode = new IconButton(
                parent.parent.resources.icon("apps/utilities-terminal"),
                "advanced");
        advBg = new ComponentGroup(advancedMode);
        advancedMode.addActionListener(this);

        setLayout(new BorderLayout());
        setBorder(new MatteBorder(0, 0, 1, 0, BORDER_BOTTOM));
        add(top, BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }

    /**
     * Creates scripter panel and sets default borders.
     *
     * @return scripter panel component
     */
    private JComponent createScripterPanel() {
        JComponent c = new ScripterPanel(parent);
        c.setBorder(new EmptyBorder(8, 8, 8, 8));
        return c;
    }

    /**
     * Creates the panel displayed at the right side of the component. This
     * panel contains the action buttons.
     *
     * @return panel for the right side of the header.
     */
    private JComponent createRightPanel() {
        final JPanel topRight;

        final Action undoAction = BaseActionFactory.createUndo(parent);
        final IconButton undoButton = new IconButton(undoAction);

        final Action redoAction = BaseActionFactory.createRedo(parent);
        final IconButton redoButton = new IconButton(redoAction);

        final Action playAction = BaseActionFactory.createPlay(parent);
        final IconButton playButton = new IconButton(playAction);

        topRight = new JPanel();
        topRight.setLayout(new BoxLayout(topRight, BoxLayout.X_AXIS));
        topRight.setBorder(new EmptyBorder(7, 6, 7, 6));
        topRight.setPreferredSize(new Dimension(parent.PANEL_WIDTH, 48));

        topRight.add(advBg);
        topRight.add(Box.createHorizontalGlue());

        topRight.add(new ComponentGroup(undoButton, redoButton));
        topRight.add(Box.createHorizontalGlue());

        topRight.add(new ComponentGroup(playButton));
        return topRight;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (cc.isVisible()) {
            topLayout.show(top, "scripter");
        } else if (sc.isVisible()) {
            topLayout.show(top, "constructor");
        }
        advBg.setBackground(sc.isVisible() ? Color.blue : Color.white);
    }
}
