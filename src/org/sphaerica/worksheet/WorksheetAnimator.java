package org.sphaerica.worksheet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Worksheet Animation manager object.
 * This class is responsible for running a background thread doing interval refreshing for smooth animation.
 * The implementation visits all ParametricPoint instances in the worksheets and calls the step() methods.
 */
public class WorksheetAnimator implements ActionListener {
    private final Timer timer = new Timer(50, this);
    private final Worksheet sheet;

    public WorksheetAnimator(Worksheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        boolean changed = false;
        synchronized (sheet) {
            for (SphericalObject object : sheet.getConstruction()) {
                if (!(object instanceof ParametricPoint))
                    continue;
                ((ParametricPoint) object).step();
                if (!object.isValid()) {
                    changed = true;
                    object.update();
                }
            }
        }
        if (changed)
            sheet.fireChangeListeners();
    }

    /**
     * Starts background of animation.
     */
    public void play() {
        timer.start();
    }

    /**
     * Stops background thread of animation.
     */
    public void pause() {
        timer.stop();
    }

    /**
     * Decides if the background animation thread is running.
     *
     * @return true iff background animation thread is running.
     */
    public boolean isPlaying() {
        return timer.isRunning();
    }
}
