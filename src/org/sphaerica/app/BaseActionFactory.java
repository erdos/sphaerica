package org.sphaerica.app;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A static factory class that creates Action instances for basic user actions
 * in the program. See public methods for the supported actions.
 */
final class BaseActionFactory {

    private BaseActionFactory() {
    }

    /**
     * Creates an undo Action instance for the given window. Sets the default
     * translated undo action text and undo icon.
     *
     * @param context application in which undo actions will be executed. Null is
     *                not permitted.
     * @return Action instance for undo actions.
     * @throws IllegalArgumentException when null parameter is given
     */
    static Action createUndo(final ApplicationWindow context)
            throws IllegalArgumentException {
        if (context == null)
            throw new IllegalArgumentException("null param is not permitted.");

        final String text = context.parent.resources.translate("undo");
        final Icon icon = context.parent.resources.icon("actions/edit-undo");
        @SuppressWarnings("serial") final Action button = new AbstractAction(text, icon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                context.worker.getUndoable().undo();
            }
        };

        final ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(context.worker.getUndoable().canUndo());
            }
        };

        context.worker.getUndoable().addActionListener(listener);
        listener.actionPerformed(null);
        return button;
    }

    /**
     * Creates an redo Action instance for the given window. Sets the default
     * translated redo action text and redo icon.
     *
     * @param context application in which redo actions will be executed. Null is
     *                not permitted.
     * @return Action instance for redo actions.
     * @throws IllegalArgumentException when null parameter is given
     */
    static Action createRedo(final ApplicationWindow context)
            throws IllegalArgumentException {
        if (context == null)
            throw new IllegalArgumentException("null param is not permitted.");

        final String text = context.parent.resources.translate("redo");
        final Icon icon = context.parent.resources.icon("actions/edit-redo");
        @SuppressWarnings("serial") final Action button = new AbstractAction(text, icon) {

            @Override
            public void actionPerformed(ActionEvent e) {
                context.worker.getUndoable().redo();
            }
        };

        final ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(context.worker.getUndoable().canRedo());
            }
        };

        context.worker.getUndoable().addActionListener(listener);
        listener.actionPerformed(null);
        return button;
    }

    /**
     * Created Action instance for the play/pause button of the window. Sets
     * default icon. Null param is not permitted.
     *
     * @param context application window for play/pause actions
     * @return Action instance for play/pause actions.
     * @throws IllegalArgumentException when null parameter is given
     */
    static Action createPlay(final ApplicationWindow context)
            throws IllegalArgumentException {
        if (context == null)
            throw new IllegalArgumentException("null param is not permitted");

        final Icon start = context.parent.resources
                .icon("actions/media-playback-start");
        final Icon stop = context.parent.resources
                .icon("actions/media-playback-stop");

        @SuppressWarnings("serial") final Action button = new AbstractAction("play", start) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (context.animator.isPlaying()) {
                    putValue(AbstractAction.LARGE_ICON_KEY, start);
                    putValue(AbstractAction.SMALL_ICON, start);
                    context.animator.pause();
                } else {
                    putValue(AbstractAction.LARGE_ICON_KEY, stop);
                    putValue(AbstractAction.SMALL_ICON, stop);
                    context.animator.play();
                }
            }
        };
        return button;
    }
}
