package org.sphaerica.worksheet;

import org.sphaerica.util.MinimaLISP;
import org.sphaerica.worksheet.Worksheet.ScriptHandle;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The worksheet scripter creates and handles a scripting engine to evaluate
 * user scripts. This class creates default bindings for the worksheet data and
 * worker methods in the script engine. Please note, that most of the geometric
 * constructions are created using these scripts.
 */
public class WorksheetScripter implements ScriptHandle {

    /**
     * Worker instance for binding to script engine
     */
    private final WorksheetWorker worker;

    /**
     * Lock used for blocking eval threads.
     */
    private final Lock locker = new ReentrantLock(true);

    /**
     * Callback object
     */
    private ScriptHandle output;

    /**
     * Scripting engine instance
     */
    private final MinimaLISP lisp = new MinimaLISP();

    /**
     * The constructor initializes the scripting engine by loading the prelude
     * file and initializing worksheet bindings.
     *
     * @param w worksheet worker to bind to scripts
     */
    public WorksheetScripter(WorksheetWorker w) {
        worker = w;
        output = this;

        try {
            lisp.addBinding("&worker", worker);
            lisp.prelude();

            System.out.println("[i] prelude loaded");
            w.undoManager.clear();

        } catch (IOException e) {
            System.out.println("[E] error on loading prelude");
            e.printStackTrace();
        } finally {
            worker.fire();
        }

    }

    /**
     * Evaluates the script in parameter in a new thread. The callback functions
     * will be called on results and errors.
     *
     * @param s script string to evaluate in new thread
     */
    public void evalInNewThread(final String s) {
        final Thread t = new Thread("evaluation") {
            public void run() {
                try {
                    Object obj = eval(s);
                    output.result(obj);
                } catch (Exception e) {
                    output.error(e);
                    e.printStackTrace();
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    /**
     * Evaluates script in this thread and returns result object of the script.
     * If there is an other eval call, the latter method call blocks until the
     * former ends.
     *
     * @param q script string to evaluate
     * @return result of script evaluated
     */
    private Object eval(final String q) {
        try {

            worker.selection.interruptSelection();

            locker.lock();
            worker.getUndoable().pushUndoable();

            try {
                return lisp.callprint(q);
            } catch (Exception e) {
                if (e.getCause() instanceof InterruptedException)
                    return null;
                else
                    throw new RuntimeException(e);
            }
        } finally {
            worker.getUndoable().popUndoable();
            locker.unlock();
            worker.fire();
        }
    }

    @Override
    public void error(Throwable thr) {
        thr.printStackTrace();
    }

    @Override
    public void result(Object obj) {
        if (obj instanceof SphericalObject)
            ObjectAppearanceFactory.show((SphericalObject) obj);
    }

    @Override
    public void print(String str) {
        System.out.print(str);
    }

    @Override
    public void err(String str) {
        System.err.print(str);
    }

    /**
     * Sets callback object used by script engine
     *
     * @param handle callback obejct
     */
    public void setHandle(ScriptHandle handle) {
        this.output = handle;
    }
}
