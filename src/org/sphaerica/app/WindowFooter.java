package org.sphaerica.app;

import org.sphaerica.swing.IconButton;
import org.sphaerica.swing.NextSlider;
import org.sphaerica.worksheet.SphericalObject;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * This class displays a footer component at the bottom of the window containing
 * several gui elements. The current implementation contains the zoom bar,
 * inspector button, inspector gui, and the projections switches button.
 */
@SuppressWarnings("serial")
public class WindowFooter extends JPanel {

    protected final ApplicationWindow parent;
    private final ObjectPropertiesPanel inspector;

    /**
     * Preferred height of the footer component.
     */
    private static final int PREFERRED_HEIGHT = 34;

    /**
     * Color of the thin line on the top of the footer.
     */
    private final static Color BORDER_TOP = new Color(118, 124, 128);

    WindowFooter(final ApplicationWindow parent) {
        this.parent = parent;
        this.inspector = new ObjectPropertiesPanel(parent);

        setLayout(new BorderLayout());

        setPreferredSize(new Dimension(0, PREFERRED_HEIGHT));
        setMaximumSize(new Dimension(100000, PREFERRED_HEIGHT));
        setBorder(new MatteBorder(1, 0, 0, 0, BORDER_TOP));
        // setOpaque(true); setBackground(Color.white);

        final JPanel right = new JPanel(new BorderLayout());
        right.add(createPropsButton(), BorderLayout.WEST);
        right.add(createZoomSlider(), BorderLayout.CENTER);

        this.add(inspector, BorderLayout.CENTER);
        this.add(right, BorderLayout.EAST);
    }

    /**
     * Builds projection properties button. This button displays a
     * ProjectionsMenu instance of mouse click event.
     */
    private JButton createPropsButton() {
        final Icon icon = parent.parent.resources
                .icon("actions/document-properties");
        final JPopupMenu menu = new ProjectionsMenu(parent);
        final JButton button = new IconButton(icon, menu);
        button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return button;
    }

    /**
     * Builds slider component for zooming in the editing area. This method sets
     * the default sizes and borders.
     *
     * @return zoom slider component
     */
    private NextSlider createZoomSlider() {
        final NextSlider zoom;
        zoom = NextSlider.installBorders(new NextSlider(100, 800, 100, 128));
        zoom.setPreferredSize(new Dimension(260, 12));
        zoom.setActionCommand("zoom");
        zoom.addActionListener(parent);
        zoom.assignValue(230);
        return zoom;
    }

    /**
     * Dispatches inspect method call to inspector panel instance.
     */
    public void inspect(SphericalObject obj) {
        inspector.inspect(obj);
    }

}
