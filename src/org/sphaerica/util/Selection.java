package org.sphaerica.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-threaded provide-consume mechanism. Objects of this class take
 * requests for data of a given class. Their threads are blocked until the
 * object of the correct class is given to the Selection object.
 *
 * @param <S> base class for typed requests
 */
public class Selection<S> {
    private volatile Object transferObject;
    private volatile Class<? extends S> transferClass;
    private final Object lock = new Object();
    private final Object lock2 = new Object();
    private final List<ActionListener> listeners = new ArrayList<ActionListener>();

    public final static String BEFORE = "pre-select", AFTER = "post-select";

    /**
     * Requests an object of type in given parameter. Blocks the current thread
     * until right class of object is given.
     *
     * @param T type of object to request
     * @return Object of the given type
     * @throws InterruptedException when blocking is unterruped by calling the
     *                              interruptSelection() method.
     */
    @SuppressWarnings("unchecked")
    public <T extends S> T expect(Class<T> c) throws InterruptedException {
        interruptSelection();

        synchronized (this.lock2) {
            final ActionEvent before = new ActionEvent(this, 0, BEFORE);
            final ActionEvent after = new ActionEvent(this, 0, AFTER);

            transferClass = c;

            for (ActionListener listener : listeners)
                listener.actionPerformed(before);

            synchronized (this.lock) {
                this.lock.wait();
            }
            this.transferClass = null;

            for (ActionListener listener : this.listeners) {
                listener.actionPerformed(after);
            }

            if (this.transferObject == null)
                throw new InterruptedException();

            return (T) this.transferObject;
        }
    }

    /**
     * Provides a selection and notifies the blocking threads.
     *
     * @param o selected object to pass to expecting threads.
     */
    public void provide(S o) {
        if (isAcceptable(o.getClass())) {
            this.transferObject = o;
            synchronized (this.lock) {
                this.lock.notify();
            }
        }
    }

    /**
     * Returns type class of current blocking request. Returns null if no
     * request is waiting at the moment.
     *
     * @return Type of current request.
     */
    public Class<? extends S> getSelectionClass() {
        return this.transferClass;
    }

    /**
     * Returns true iff objects of given class are acceptable by the provide()
     * method.
     *
     * @param c class to test if acceptable by provide()
     * @return true iff provide() accepts objects of class param.
     */
    public boolean isAcceptable(Class<?> c) {
        return (this.transferClass != null) && (c != null)
                && (this.transferClass.isAssignableFrom(c));
    }

    /**
     * Interrupts current selection request and notifies waiting threads.
     */
    public void interruptSelection() {
        if (this.transferClass != null) {
            this.transferClass = null;
            this.transferObject = null;
            synchronized (this.lock) {
                this.lock.notify();
            }
        }
    }

    /**
     * Adds event listener to listener pool. The event listener will be called
     * with action strings Selection.BEFORE and Selection.AFTER before and after
     * an acceptable selection object is given to provide() method. See also
     * removeListener()
     *
     * @param listener event listener
     */
    public void addListener(ActionListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes event listener from listener pool
     *
     * @param listener event listener
     */
    public void removeListener(ActionListener listener) {
        this.listeners.remove(listener);
    }

}
