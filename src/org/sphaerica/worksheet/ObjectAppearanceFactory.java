package org.sphaerica.worksheet;

import org.sphaerica.util.TangoColorScheme;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uploads the SphericalObject's inner appearance map with default values. Use
 * the init method to initialize the appearance representation. The conventional
 * appearance properties, types and values can be found here: Please note, that
 * this list may change on later versions.
 *
 * <pre>
 * <u>key</u>              <u>type</u>     <u>default value</u>
 * KEY_VISIBILITY   Boolean  false
 * KEY_COLOR        Color    /some value/
 * KEY_SIZE         Integer  /some value/
 * </pre>
 */
public class ObjectAppearanceFactory implements SphericalObjectVisitor {

    /**
     * The visibility key tells if an object is visible in the editor.
     */
    public final static String KEY_VISIBILITY = "visible";

    /**
     * The color can indicate the color of a point marker, a line or the fill
     * color of a polygon.
     */
    public final static String KEY_COLOR = "color";

    /**
     * The size poperty can be used to indicate the marker size to a spherical
     * point or the line width of a curve.
     */
    public final static String KEY_SIZE = "size";

    private final static Pattern COLOR = Pattern.compile("^java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]$");

    private final Map<Class<? extends SphericalObject>, Map<String, Object>> defaults = new HashMap<Class<? extends SphericalObject>, Map<String, Object>>();

    {
        setDefault(AbstractPoint.class, KEY_COLOR,
                TangoColorScheme.Butter1.color);
        setDefault(AbstractPoint.class, KEY_SIZE, 5);

        setDefault(FreePoint.class, KEY_COLOR,
                TangoColorScheme.Orange2.color.brighter());
        setDefault(ParametricPoint.class, KEY_COLOR,
                TangoColorScheme.Orange2.color.brighter());

        setDefault(AbstractCurve.class, KEY_COLOR,
                TangoColorScheme.Aluminium6.color);
        setDefault(AbstractCurve.class, KEY_SIZE, 2);

        setDefault(Polygon.class, KEY_COLOR, TangoColorScheme.Plum1.color);
    }

    public void setDefault(Class<? extends SphericalObject> type,
                           String property, Object val) {
        Map<String, Object> mp = defaults.get(type);
        if (mp == null)
            defaults.put(type, mp = new HashMap<String, Object>());
        mp.put(property, val);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getDefaults(Class<? extends SphericalObject> type) {
        Map<String, Object> m = defaults.get(type);
        return (m == null) ? Collections.EMPTY_MAP : m;
    }

    /**
     * Displays the given spherical object. That is, sets the visibility
     * property to true.
     *
     * @param obj object to show
     */
    public static void show(SphericalObject obj) {
        obj.getAppearance().put(KEY_VISIBILITY, true);
    }

    /**
     * Hides the given spherical object. That is, sets the visibility property
     * to false on the appearance map.
     *
     * @param obj object to hide
     */
    public static void hide(SphericalObject obj) {
        obj.getAppearance().put(KEY_VISIBILITY, false);
    }

    public static Object decode(String key, String value) {
        if (key.equals(KEY_VISIBILITY))
            return decode(value, Boolean.class);
        if (key.equals(KEY_COLOR))
            return decode(value, Color.class);
        if (key.equals(KEY_SIZE))
            return decode(value, Integer.class);
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> T decode(String key, Class<T> clazz) {
        if (clazz == Boolean.class)
            return (T) Boolean.valueOf(key);
        if (clazz == Color.class) {
            Matcher m = COLOR.matcher(key);
            if (!m.matches())
                throw new RuntimeException("format err");

            int r = Integer.valueOf(m.group(1));
            int g = Integer.valueOf(m.group(2));
            int b = Integer.valueOf(m.group(3));
            return (T) new Color(r, g, b);
        }
        if (clazz == String.class)
            return (T) key;
        if (clazz == Integer.class)
            return (T) Integer.valueOf(key);
        if (clazz == Double.class)
            return (T) Double.valueOf(key);
        throw new RuntimeException("unknown class");
    }

    /**
     * Sets the default appearance information of the object.
     *
     * @param obj object to style
     */
    public void init(SphericalObject obj) {

        if (!obj.getAppearance().isEmpty())
            return;

        obj.getAppearance().put(KEY_VISIBILITY, Boolean.FALSE);

        obj.apply(this);
    }

    @Override
    public void visit(AbstractCurve curve) {
        curve.getAppearance().putAll(getDefaults(AbstractCurve.class));
        curve.getAppearance().putAll(getDefaults(curve.getClass()));
    }

    @Override
    public void visit(AbstractPoint point) {
        point.getAppearance().putAll(getDefaults(AbstractPoint.class));
        point.getAppearance().putAll(getDefaults(point.getClass()));
    }

    @Override
    public void visit(Polygon poly) {
        poly.getAppearance().putAll(getDefaults(Polygon.class));
    }
}
