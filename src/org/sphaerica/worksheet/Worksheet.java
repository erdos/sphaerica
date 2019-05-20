package org.sphaerica.worksheet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * The worksheet is a collection for the geometric constructions of spherical
 * objects. The worksheet also holds information about properties of the
 * construction and the sphere.
 */
public class Worksheet {
    private final Construction construction = new Construction();
    private final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
    private final ObjectAppearanceFactory appearance = new ObjectAppearanceFactory();

    /**
     * Creates a new worker instance for this worksheet.
     *
     * @return new worker instance
     */
    public WorksheetWorker createWorker() {
        return new WorksheetWorker(this);
    }

    public WorksheetAnimator createAnimator() {
        return new WorksheetAnimator(this);
    }

    public void fireChangeListeners() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener listener : this.changeListeners)
            listener.stateChanged(e);
    }

    public void addChangeListener(ChangeListener l) {
        this.changeListeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        this.changeListeners.remove(l);
    }

    public ObjectAppearanceFactory getAppearanceFactory() {
        return this.appearance;
    }

    public Construction getConstruction() {
        return this.construction;
    }

    /**
     * The construction holds the list of geometric objects. It also manager the
     * addition and removal of object hiearchies.
     */
    public class Construction implements Iterable<SphericalObject> {
        private final List<SphericalObject> contents = new ArrayList<SphericalObject>();

        public Construction() {
        }

        public boolean contains(SphericalObject obj) {
            return contents.contains(obj);
        }

        public void clear() {
            Iterator<SphericalObject> iter = iterator();
            while (iter.hasNext()) {
                iter.remove();
                iter.next();
            }
        }

        public void add(SphericalObject object) {
            if (this.contents.contains(object)) {
                return;
            }
            for (SphericalObject parent : object.getParents()) {
                add(parent);
            }

            getAppearanceFactory().init(object);

            this.contents.add(object);

            for (SphericalObject parent : object.getParents())
                parent.registerChild(object);

        }

        public void remove(SphericalObject object) {
            if (!this.contents.contains(object)) {
                return;
            }

            Stack<SphericalObject> levels = new Stack<SphericalObject>();
            levels.push(object);

            while (!levels.empty()) {
                SphericalObject obj = levels.pop();

                if (!contents.remove(obj))
                    continue;

                for (SphericalObject child : obj.getChildren())
                    levels.push(child);
            }

            for (SphericalObject parent : object.getParents())
                parent.unregisterChild(object);
        }

        public Iterator<SphericalObject> iterator() {
            return new Iterator<SphericalObject>() {
                int i = 0;
                final SphericalObject[] array = contents
                        .toArray(new SphericalObject[0]);

                public boolean hasNext() {
                    return this.i < this.array.length;
                }

                public SphericalObject next() {
                    return this.array[(i++)];
                }

                public void remove() {
                    Worksheet.Construction.this.remove(this.array[i]);
                }
            };
        }
    }

    public interface ScriptHandle {
        void error(Throwable thr);

        void result(Object obj);

        void print(String str);

        void err(String str);

    }
}
