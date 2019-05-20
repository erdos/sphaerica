package org.sphaerica.app;

import org.sphaerica.display.GnomonicPainter;
import org.sphaerica.display.OrthogonalPainter;
import org.sphaerica.display.StereographicPainter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This component is a popup menu that provides configuration options regarding
 * to the projection used by the sphere editor.
 */
@SuppressWarnings("serial")
public class ProjectionsMenu extends JPopupMenu implements ActionListener {

    ApplicationWindow context;

    public ProjectionsMenu(ApplicationWindow context) {
        this.context = context;

        // final JCheckBoxMenuItem backfaceVisible = new
        // JCheckBoxMenuItem("Show backface");
        // backfaceVisible.setActionCommand("backface");
        // backfaceVisible.addActionListener(this);
        // menu.add(backfaceVisible);

        // menu.addSeparator();
        // menu.add(item("background"));
        // menu.add(item("foreground"));
        // menu.addSeparator();
        add(item("stereographic"));
        add(item("orthogonal"));
        add(item("gnomonic"));
    }

    /**
     * Creates a menu item for the given key. The key string is used as an
     * action string and a dictionary key for displaying multilingual text.
     */
    public JMenuItem item(String key) {
        JMenuItem item = new JMenuItem(key);
        context.parent.resources.register(item, key);
        item.setActionCommand(key);
        item.addActionListener(this);
        return item;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if (action.equals("stereographic")) {
            context.editor.setProjection(new StereographicPainter(
                    context.editor.getArcBall()));
        } else if (action.equals("orthogonal")) {
            context.editor.setProjection(new OrthogonalPainter(context.editor
                    .getArcBall()));
        } else if (action.equals("gnomonic")) {
            context.editor.setProjection(new GnomonicPainter(context.editor
                    .getArcBall()));
        } else
            context.actionPerformed(e);
        context.worksheet.fireChangeListeners();
    }
}
