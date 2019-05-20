package org.sphaerica.util;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This undoable manager class provides a stack of undoables for nested undoing
 * of actions. The user pushes a new undoable to the stack. All the undoable
 * actons are merged to the top element of the stack. After that, the user can
 * pop the latest undoable from the stack, then wrap it in and undoable and
 * merge it to the second latest on the stack. This mechanism provides a simple
 * recursive mechanism for nested transactions. The event handlers can be used
 * to keep track of the undo/redo and push/pop actions.
 */
public final class StackedUndoManager implements UndoableEdit {
    private final Stack<UndoableEdit> stack = new Stack<UndoableEdit>();
    private final List<ActionListener> listeners = new ArrayList<ActionListener>();

    private final static String UNDO = "event-undo", REDO = "event-redo";
    private final static String PUSH = "event-push", POP = "event-pop";
    private final static String ADD = "event-add", DIE = "event-die";
    private final static String CLEAR = "clear";

    public StackedUndoManager() {
        stack.addElement(new UndoManager());
    }

    /**
     * Pushes an undoable to the stack. All undo/redo or addEdit operations will
     * be performed on the top elem of the stack. That is, this method starts a
     * new nested transaction.
     */
    public void pushUndoable() {
        UndoManager manager = new UndoManager();
        manager.setLimit(-1);
        stack.addElement(manager);
        fireActionListeners(PUSH);
    }

    /**
     * Pops the top elem from the stack, wraps it and merges it to the new top
     * of the stack. That is, this method ends the latest nested transaction.
     */
    public void popUndoable() {
        UndoableEdit last = new UndoList(stack.pop());
        stack.peek().addEdit(last);
        fireActionListeners(POP);
    }

    /**
     * Performs and undo command on the top element of the stack.
     */
    public synchronized void undo() {
        stack.peek().undo();
        fireActionListeners(UNDO);
    }

    /**
     * Perdorms a redo command on the top element of the stack.
     */
    public synchronized void redo() {
        stack.peek().redo();
        fireActionListeners(REDO);
    }

    /**
     * Returns true iff the top element can be undone.
     */
    public boolean canUndo() {
        return stack.peek().canUndo();
    }

    /**
     * Adds the undoable to the one on the top of the stack. Calls action
     * listeners with the ADD event command.
     */
    public boolean addEdit(UndoableEdit anEdit) {
        if (stack.peek().addEdit(anEdit)) {
            fireActionListeners(ADD);
            return true;
        }
        return false;
    }

    /**
     * Return true iff the top element can be redone.
     */
    public boolean canRedo() {
        return stack.peek().canRedo();
    }

    /**
     * Calls die() on each element of the stack and then calls cleaer() and
     * calls event listeners with DIE event.
     */
    public void die() {
        for (UndoableEdit edit : stack)
            edit.die();
        stack.clear();
        fireActionListeners(DIE);
    }

    /**
     * Clears the stack and calls event listeners with CLEAR action command.
     */
    public void clear() {
        stack.clear();
        pushUndoable();
        fireActionListeners(CLEAR);
    }

    public String getPresentationName() {
        return "A stack of undoables.";
    }

    public String getRedoPresentationName() {
        return null;
    }

    public String getUndoPresentationName() {
        return null;
    }

    public boolean isSignificant() {
        return true;
    }

    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    /**
     * Adds an action listener to the list of listeners.
     *
     * @param listener registers this listener.
     */
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes an action listener from the list of listeners.
     *
     * @param listener deregisters this listener.
     */
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires each action listener with the given action command.
     *
     * @param command action command string to send to listeners.
     */
    void fireActionListeners(String command) {
        ActionEvent event = new ActionEvent(this, 0, command);
        for (ActionListener l : listeners)
            l.actionPerformed(event);
    }

    /**
     * The undo list is a wrapper for undoables. All undo/redo methods will be
     * called on each items of this list in order/reverse order.
     */
    @SuppressWarnings("serial")
    private final class UndoList extends AbstractUndoableEdit {

        private final UndoableEdit manager;

        UndoList(UndoableEdit content) {
            manager = content;
        }

        public void undo() {
            super.undo();
            while (manager.canUndo())
                manager.undo();
        }

        public void redo() {
            super.redo();
            while (manager.canRedo())
                manager.redo();
        }
    }

}