package org.sphaerica.app;

import org.sphaerica.data.Resources;
import org.sphaerica.math.AngleUnit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * This class handles one instance of the running sphaerica program. It
 * dispatches file open requests to the instance as well as window handling
 * requests and event handling.
 */
public class ApplicationContext implements ActionListener {
    final Resources resources = new Resources();

    /**
     * Creates a new program window or a new tab in the default program window.
     */
    public void createWindow() {
        ApplicationWindow win = new ApplicationWindow(this);
        win.setVisible(true);
        win.requestFocus();
    }

    /**
     * Opens the given file for editing in a new window or in a new tab of the
     * default program window.
     *
     * @param f the sphaerica worksheet file to open.
     * @throws IOException
     */
    public void openWindow(File f) throws IOException {
        ApplicationWindow win = acquireWindow();

        win.documentHandling.open(f);
        if (win.documentHandling.getFile() != null)
            win.setVisible(true);
    }

    /**
     * Returns a new or existing sphaerica window instance.
     *
     * @return a sphaerica window instance
     */
    private ApplicationWindow acquireWindow() {
        return new ApplicationWindow(this);
    }

    /**
     * Displays new file open dialog for loading worksheet files.
     */
    private void openWindow() {
        try {
            ApplicationWindow win = new ApplicationWindow(this);
            win.documentHandling.open();
            if (win.documentHandling.getFile() != null)
                win.setVisible(true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final String action = e.getActionCommand();

        if (action.equals("open"))
            openWindow();
        else if (action.equals("new"))
            createWindow();
        else if (action.equals("radians"))
            resources.loadAngleUnit(AngleUnit.RADIAN);
        else if (action.equals("degrees"))
            resources.loadAngleUnit(AngleUnit.DEGREE);
        else if (action.equals("gradians"))
            resources.loadAngleUnit(AngleUnit.GRADIAN);
        else if (action.startsWith("lang:")) {
            System.out.println("[i] switching to language: " + action);
            resources.loadLanguage(action.substring(5));
        } else {
            throw new RuntimeException("could not handle: " + e.getActionCommand());
        }
    }
}
