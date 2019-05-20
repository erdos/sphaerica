package org.sphaerica;

import org.sphaerica.app.ApplicationContext;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Sphaerica program main class. This class starts a sphaerica program session.
 * It handles the loading of initializer methods and the processing of command
 * line parameters.
 */
public class SphaericaMain {

    /**
     * This method sets a native look and feel for the application. It also
     * provides special GUI tweaks for MacOs X. Please note that this method
     * will not throw exceptions but dump them to the standard output.
     */
    private static void initLookAndFeel() {
        try {
            // menu bar on top of screen
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            // bottom right corner
            System.setProperty("apple.awt.showGrowBox", "true");
            // program name in "About" menu item
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    "Sphaerica");

            // Native MacOsX laf.
            // System.setProperty("apple.awt.brushMetalLook", "true");

            /*
             * We load the system look and feel, that is to provide native
             * looking application gui.
             */
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sphaerica program main function.
     *
     * @param args - array of argument strings with first item being the program name.
     */
    public static void main(String[] args) throws IOException {

        System.out.println(" *  Sphaerica " + SphaericaInfo.VERSION);
        System.out.println("[i] java runtime: "
                + System.getProperty("java.runtime.version"));

        initLookAndFeel();

        final ApplicationContext context = new ApplicationContext();

        for (String argument : args) {
            final File f = new File(argument);
            if (f.exists())
                context.openWindow(f);
            else
                System.err.printf("[E] invalid argument: %s \n", argument);
        }
        if (args.length == 0)
            context.createWindow();
    }
}
