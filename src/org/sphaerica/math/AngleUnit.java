package org.sphaerica.math;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Angle unit class formats angles in radians to other angle units.
 */
public enum AngleUnit {
    /**
     * Degree angle unit. A linear unit with zero angle being 0 and full angle
     * being 360. This is one of the most common used unit.
     */
    DEGREE {
        public String getReadable(double angle) {
            return String.format("%1$.2f", angle / Math.PI / 2 * 360) + '\u00b0';
        }
    },
    /**
     * Radian angle unit. A linear unit with zero angle being 0 and full angle
     * being 2*PI. This is other common used unit.
     */
    RADIAN {
        @Override
        public String getReadable(double angle) {
            double key = angle / Math.PI;
            String suffix = "";
            if (ratios.containsKey(key))
                suffix = " = " + ratios.get(key) + "π";

            return String.format("%1$.2f rad" + suffix, angle);
        }
    },
    /**
     * Gradian angle unit. A linear unit with zero angle bering 0 and full angle
     * being 400. This measure is not used often nowadays.
     */
    GRADIAN {
        @Override
        public String getReadable(double angle) {
            return String.format("%1$.2f gon", angle / Math.PI / 2 * 400);
        }
    };

    /**
     * An unmodifiable map of special ratios.
     */
    public final static Map<Double, String> ratios;

    static {
        Map<Double, String> rs = new HashMap<Double, String>();

        rs.put(2d, "2");
        rs.put(1d, "1");
        rs.put(1d / 2d, "½");
        rs.put(1d / 3d, "⅓");
        rs.put(1d / 4d, "¼");
        rs.put(1d / 5d, "⅕");
        rs.put(1d / 6d, "⅙");

        rs.put(3d / 4, "¾");
        rs.put(2d / 3, "⅔");
        for (int i = 1; i < 10; i++)
            for (int j = 2; j < 10; j++) {
                double r = 1d * (double) i / (double) j;
                if (!rs.containsKey(r))
                    rs.put(r, i + "⁄" + j);
            }
        ratios = Collections.unmodifiableMap(rs);
    }

    /**
     * Gets human readable form of the angle unit.
     *
     * @param angle in radians
     * @return human readable formatted string
     */
    public abstract String getReadable(double angle);

    /**
     * Returns an AngleUnit instance for a given name.
     *
     * @param s angle unit name.
     * @return AngleUnit instance for name or null if no such instance is found.
     */
    public static AngleUnit forName(String s) {
        if (s.startsWith("deg")) // deg, degree, degrees, etc...
            return DEGREE;
        else if (s.startsWith("rad")) // rad, radian, radians, etc...
            return RADIAN;
        else if (s.startsWith("grad")) // grad, gradian, gradians, etc...
            return GRADIAN;
        else
            return null;
    }

}
