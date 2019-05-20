package org.sphaerica.display;

import java.awt.*;

/**
 * This helper class contains methods on painting projections of spherical
 * objects with style.
 */
class PainterHelper {

    /**
     * Creates a dimmed version of the color that represents the same opaque
     * version of the same color visible from the hidden side of the sphere.
     *
     * @param color original color to tint
     * @return same color visible from the other side of the sphere
     */
    static Color backfaceColor(Color color) {
        int red = color.getRed() / 2 + 128;
        int green = color.getGreen() / 2 + 128;
        int blue = color.getBlue() / 2 + 128;
        return new Color(red, green, blue);
    }
}
