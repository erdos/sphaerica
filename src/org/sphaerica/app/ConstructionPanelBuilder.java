package org.sphaerica.app;

import org.sphaerica.swing.PanelFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Construction Panel GUI component builder class. This class loads the
 * construction.js.xml file to create a menu of scripted geometry constructons.
 */
final class ConstructionPanelBuilder {

    /**
     * Name of the xml resource in which the construction tool descriptions can
     * be found.
     */
    private static final String XML_RESOURCE = "construction.lisp.xml";

    /**
     * Parent window for the component.
     */
    private final ApplicationWindow context;

    ConstructionPanelBuilder(final ApplicationWindow parental) {
        context = parental;
    }

    /**
     * Builds the gui component containing buttons for the construction tools.
     */
    JComponent buildPanel() {

        JComponent constructionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        try {
            final DocumentBuilder builder = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();
            final Document doc = builder.parse(ClassLoader
                    .getSystemResourceAsStream(XML_RESOURCE));
            populate(constructionBar, doc.getChildNodes().item(0).getChildNodes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Component c : constructionBar.getComponents()) {
            if (c instanceof AbstractButton)
                ((AbstractButton) c).setUI(new AbstractB());
        }

        constructionBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        constructionBar.setOpaque(false);

        // // this trick centers the content.
        //JPanel panel = new JPanel(new GridBagLayout());
        //panel.add(constructionBar);
        return PanelFactory.createFlowPanel(FlowLayout.LEFT, constructionBar);
    }

    /**
     * Populates given menu item with actons and submenus from node object.
     *
     * @param menu menu component to insert items.
     * @param node node object containing menu item informations.
     */
    private void populate(JComponent menu, NodeList node) {
        for (int i = 0; i < node.getLength(); i++) {
            final Node n = node.item(i);

            final AbstractButton c;

            if (n.getNodeName().equals("menu")) {
                c = new JMenu();
                populate(c, n.getChildNodes());
            } else if (n.getNodeName().equals("item")) {
                final String action = n.getTextContent();
                c = new JMenuItem();
                c.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        context.scripter.evalInNewThread(action);
                    }
                });
            } else if (n.getNodeName().equals("separator")) {
                menu.add(new JSeparator());
                continue;
            } else
                continue;

            context.parent.resources.register(c, n.getAttributes()
                    .getNamedItem("name").getTextContent());

            if (n.getAttributes().getNamedItem("icon") != null)
                c.setIcon(context.parent.resources.icon(n.getAttributes()
                        .getNamedItem("icon").getTextContent()));

            menu.add(c);
        }
    }

    /**
     * A button LAF for the top level menu items.
     */
    class AbstractB extends BasicButtonUI {
        public void installUI(final JComponent c) {
            super.installUI(c);

            c.setFont(c.getFont().deriveFont(11f));
            c.setBorder(new EmptyBorder(2, 12, 2, 22)); // top left bottom right

            final Color normal = Color.white;
            final Color hover = new Color(245, 245, 255, 255);

            c.setBackground(normal);
            c.setOpaque(false);
            c.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    c.setBackground(hover);
                }

                public void mouseExited(MouseEvent e) {
                    c.setBackground(normal);
                }

            });

            if (c instanceof JMenu) {
                final JMenu cMenu = (JMenu) c;
                cMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cMenu.getPopupMenu().show(cMenu.getParent(),
                                cMenu.getLocation().x,
                                cMenu.getLocation().y + cMenu.getHeight());
                    }
                });
            }
        }

        @Override
        public void paint(Graphics g, JComponent c) {

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(c.getBackground());
            Rectangle r = c.getBounds();
            int radius = 6;
            g2d.fillRoundRect(0, 0, r.width, r.height, radius, radius);

            int w = c.getWidth(), h = c.getHeight(), b = 6, x = 4;
            g2d.setColor(Color.lightGray);
            g2d.fillPolygon(new int[]{w - b, w - b - x - x, w - b - x}, new int[]{h / 2 - 2, h / 2 - 2, h / 2 + 4}, 3);
            super.paint(g, c);
        }
    }
}