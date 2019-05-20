package org.sphaerica.util;

import java.awt.*;

/**
 * This is a color scheme from the Tango Desktop project.
 */
public enum TangoColorScheme {

    Aluminium1(0xeeeeec),
    Aluminium2(0xd3d7cf),
    Aluminium3(0xbabdb6),
    Aluminium6(0x2e3436),

    Butter1(0xfce94f),
    Butter2(0xedd400),
    Butter3(0xc4a000),

    Chameleon1(0x8ae234),
    Chameleon2(0x73d216),
    Chameleon3(0x4e9a06),

    Chocolate1(0xe9b96e),
    Chocolate2(0xc17d11),
    Chocolate3(0x8f5902),

    Orange2(0xf57900),
    Orange3(0xce5c00),

    Plum1(0xad7fa8),
    Plum2(0x75507b),
    Plum3(0x5c3566),

    ScarletRed1(0xef2929),
    ScarletRed2(0xcc0000),
    ScarletRed3(0xa40000),

    SkyBlue1(0x729fcf),
    SkyBlue2(0x3465a4),
    SkyBlue3(0x204a87);

    /**
     * Creates an array of colors in this color scheme.
     *
     * @return array of colors in this scheme.
     */
    public static Color[] colors() {
        Color[] colors = new Color[values().length];
        for (int i = values().length - 1; i >= 0; i--)
            colors[i] = values()[i].color;
        return colors;
    }

    public final Color color;

    TangoColorScheme(int i) {
        color = new Color(i);
    }
}