package org.sphaerica.data;

import org.sphaerica.math.AngleUnit;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * This class handles resources such as language files, icon files and user
 * specific information.
 */
public final class Resources {

    private final Map<String, Icon> icons = new HashMap<String, Icon>();

    private ResourceBundle language;
    private AngleUnit angle = AngleUnit.DEGREE;

    private final Map<JComponent, String> maps = new HashMap<JComponent, String>();

    public void loadAngleUnit(AngleUnit angle) {
        this.angle = angle;
    }

    /**
     * Loads language from the languages project directory.
     *
     * @param lang language file prefix
     */
    public void loadLanguage(String lang) {
        try {
            language = new PropertyResourceBundle(getClass()
                    .getResourceAsStream("/languages/" + lang + ".properties"));
            for (Map.Entry<JComponent, String> elem : maps.entrySet())
                setTextFor(elem.getKey(), elem.getValue());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    {
        loadLanguage("English");
    }

    /**
     * Retrieves icon object from file system for key.
     *
     * @param key name of icon
     * @return Icon object or null if none found.
     */
    public Icon icon(String key) {
        if (key == null)
            throw new IllegalArgumentException(
                    "null value not permitted for icon key.");

        if (icons.containsKey(key))
            return icons.get(key);

        URL url = getClass().getResource("/icons/" + key + ".png");
        if (url == null)
            url = getClass().getResource("/icons/" + key + ".jpg");
        if (url == null)
            url = getClass().getResource("/icons/" + key);
        if (url == null) {
            System.err.println("[E] no icon for: " + key);
            return null;
        }

        final Icon retur = new ImageIcon(url);
        icons.put(key, retur);

        return retur;
    }

    /**
     * Looks up key in language dictionary
     *
     * @param key in dictionary
     * @return translated word found in dictionary for key
     */
    public String translate(String key) {
        if (language.containsKey(key))
            return language.getString(key);
        else {
            System.err.println("[E] no translation for: " + key);
            return key;
        }
    }

    public String angle(double radians) {
        return angle.getReadable(radians);
    }

    /**
     * Registers swing component for multilingual use. The text content of the
     * component will be set to the word in the dictionary and will be updated
     * whenever the dictionary changes.
     *
     * @param comp swing component
     * @param key  text string key for lookup in dictionary
     * @return component in parameter
     */
    public final <T extends JComponent> T register(final T comp,
                                                   final String key) {
        setTextFor(comp, key);
        comp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                unregister(comp);
            }
        });
        maps.put(comp, key);
        setTextFor(comp, key);
        return comp;
    }

    /**
     * Deregisters swing component. This component will not be updated any more
     * when dictionary changes.
     */
    private <T extends JComponent> T unregister(final T comp) {
        maps.remove(comp);

        return comp;
    }

    /**
     * Calls the .setText method on component if it has any. Uses reflection.
     *
     * @param s label text
     */
    private void setTextFor(JComponent comp, String s) {
        try {
            comp.getClass().getMethod("setText", String.class)
                    .invoke(comp, translate(s));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
