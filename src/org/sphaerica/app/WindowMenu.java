package org.sphaerica.app;

import org.sphaerica.swing.ColorIcon;
import org.sphaerica.util.TangoColorScheme;
import org.sphaerica.worksheet.*;
import org.sphaerica.worksheet.Polygon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * The default menu bar for a sphaerica window. The menu contains various menu
 * items on basic document management actions and property settings.
 */
public class WindowMenu extends JMenuBar {

    private static final long serialVersionUID = 345611959535987339L;
    private final ApplicationWindow context;

    public WindowMenu(ApplicationWindow parent) {
        this.context = parent;

        add(buildFileMenu());
        add(buildViewMenu());
        add(buildHelpMenu());
    }

    /**
     * Creates the default top level File menu.
     *
     * @return a new JMenu instance
     */
    private JMenu buildFileMenu() {
        final JMenu file = menu("menu.file", null);
        file.add(item("menu.file.new", "actions/document-new", "new",
                KeyEvent.VK_N));
        file.add(item("menu.file.open", "actions/document-open", "open",
                KeyEvent.VK_O));
        file.addSeparator();
        file.add(item("menu.file.save", "actions/document-save", "save",
                KeyEvent.VK_S));
        JMenuItem saveAs = item("menu.file.saveas", "actions/document-save-as",
                "save-as", KeyEvent.VK_S | KeyEvent.SHIFT_MASK);
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.SHIFT_DOWN_MASK
                        | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(saveAs);
        file.addSeparator();
        file.add(item("menu.file.print", "actions/document-print", "print",
                KeyEvent.VK_P));
        file.addSeparator();
        file.add(item("menu.file.close", "actions/process-stop", "close",
                KeyEvent.VK_W));
        return file;
    }

    private final class StyleSetter implements ActionListener {
        Class<? extends SphericalObject> t;
        String k;
        Object v;

        StyleSetter(Class<? extends SphericalObject> type, String key,
                    Object val) {
            this.t = type;
            this.k = key;
            this.v = val;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            context.worksheet.getAppearanceFactory().setDefault(t, k, v);
        }
    }

    /**
     * Creates the default top level View menu.
     *
     * @return a new JMenu instance
     */
    private JMenu buildViewMenu() {
        final JMenu view = menu("menu.view");

        // angles
        final JMenu angles = menu("menu.view.angles");
        angles.add(item("degrees"));
        angles.add(item("radians"));
        angles.add(item("gradians"));

        // languages
        final JMenu lang = menu("language");
        lang.add(item("Hungarian", null, "lang:Magyar", -1));
        lang.add(item("English", null, "lang:English", -1));

        view.add(angles);
        view.add(lang);

        return view;
    }

    /**
     * Builds menu component displaying various appearance related settings.
     * This method is not used (yet) in the current release.
     *
     * @return menu containing default styling settings
     */
    protected JMenu buildStyleMenu() {
        JMenu style = menu("menu.view.style");

        // default curve color styles.
        final JMenu colorStyles = menu("menu.view.styles.curve-color");
        ButtonGroup grp0 = new ButtonGroup();
        for (Color c : TangoColorScheme.colors()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ColorIcon(
                    c));
            item.addActionListener(new StyleSetter(AbstractCurve.class,
                    ObjectAppearanceFactory.KEY_COLOR, c));
            colorStyles.add(item);
            grp0.add(item);
        }

        // default poly color styles.
        final JMenu polyColors = menu("menu.view.styles.poly-color");
        ButtonGroup grp1 = new ButtonGroup();
        for (Color c : TangoColorScheme.colors()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ColorIcon(
                    c));
            item.addActionListener(new StyleSetter(Polygon.class,
                    ObjectAppearanceFactory.KEY_COLOR, c));
            polyColors.add(item);
            grp1.add(item);
        }

        // default point color styles.
        final JMenu ptColors = menu("menu.view.styles.point-color");
        ButtonGroup grp2 = new ButtonGroup();
        for (Color c : TangoColorScheme.colors()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ColorIcon(
                    c));
            item.addActionListener(new StyleSetter(AbstractPoint.class,
                    ObjectAppearanceFactory.KEY_COLOR, c));
            ptColors.add(item);
            grp2.add(item);
        }

        // default free point color styles.
        final JMenu fptColor = menu("menu.view.styles.freepoint-color");
        ButtonGroup grp3 = new ButtonGroup();
        for (Color c : TangoColorScheme.colors()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ColorIcon(
                    c));
            item.addActionListener(new StyleSetter(FreePoint.class,
                    ObjectAppearanceFactory.KEY_COLOR, c));
            item.addActionListener(new StyleSetter(ParametricPoint.class,
                    ObjectAppearanceFactory.KEY_COLOR, c));
            fptColor.add(item);
            grp3.add(item);
        }

        style.add(colorStyles);
        style.add(polyColors);
        style.add(ptColors);
        style.add(fptColor);
        return style;
    }

    /**
     * Creates the default top level Help menu.
     *
     * @return a new JMenu instance
     */
    private JMenu buildHelpMenu() {
        final JMenu help = menu("menu.help");
        // help.add("Sphaerica online");
        // help.add("Documentation");
        help.add(item("about", null, "about", -1));
        add(help);
        return help;
    }

    /**
     * Creates a new JMenuItem instance and sets default action command,
     * listener, name.
     *
     * @param key the action command string and also the label text resource id.
     */
    protected JMenuItem item(String key) {
        return item(key, null, key, -1);
    }

    /**
     * Creates a menu with the given label text key and icon key.
     *
     * @param textKey key in the language resource file
     * @param iconKey key for the displayed icon resource
     * @return a new JMenu instance
     */
    private JMenu menu(String textKey, Icon iconKey) {
        final JMenu menu = new JMenu();
        context.parent.resources.register(menu, textKey);
        // menu.setIcon(context.parent.resources.icon(textKey));
        return menu;
    }

    /**
     * Creates a new menu with the given label text key.
     *
     * @param textKey key in the language resource file
     * @return a new JMenu instance
     */
    protected JMenu menu(String textKey) {
        return menu(textKey, null);
    }

    /**
     * Creates a new menu item and sets default text from dictionary, icon by
     * key, action command string and key stroke accelerator.
     *
     * @param textKey       key for translated text in dictionary
     * @param iconKey       key for icon
     * @param actionCommand action command. System default menu modifier is added to this
     *                      key code.
     * @param keyStroke     key stroke code. -1 means no accelerator key.
     * @return customized menu item instance
     */
    protected JMenuItem item(String textKey, String iconKey,
                             String actionCommand, int keyStroke) {
        final JMenuItem item = new JMenuItem();
        context.parent.resources.register(item, textKey);

        if (iconKey != null)
            item.setIcon(context.parent.resources.icon(iconKey));
        item.setActionCommand(actionCommand);
        item.addActionListener(context);

        if (keyStroke != -1)
            item.setAccelerator(KeyStroke.getKeyStroke(keyStroke, Toolkit
                    .getDefaultToolkit().getMenuShortcutKeyMask()));

        return item;
    }
}
