package org.sphaerica.worksheet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.*;

public abstract class AbstractSphericalObject implements SphericalObject {

    private boolean valid = false, real = false;
    private final Map<String, Object> appearance = new HashMap<String, Object>();
    private final List<SphericalObject> children = new ArrayList<SphericalObject>();
    private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    private final SphericalObject[] parents;

    protected AbstractSphericalObject(SphericalObject... p) {
        parents = p;
    }

    @Override
    public final boolean isValid() {
        return valid;
    }

    public final boolean isReal() {
        return real;
    }

    @Override
    public final void update() {

        if (valid)
            return;

        // a _real_ flag fugg a szulok allapotatol is.
        real = true;
        for (SphericalObject parent : getParents())
            if (!(real = parent.isReal()))
                break;

        if (real) {
            updateImpl();
            real = isRealImpl();
        }


        valid = true;

        final ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : listeners)
            l.stateChanged(e);
    }

    /**
     * Invalidates the tree hierarchy
     */
    final void invalidate() {
        if (!valid)
            return;

        valid = false;

        final SphericalObjectVisitor visitor = new SphericalObjectVisitor() {

            @Override
            public void visit(Polygon poly) {
                poly.invalidate();
            }

            @Override
            public void visit(AbstractPoint point) {
                point.invalidate();
            }

            @Override
            public void visit(AbstractCurve curve) {
                curve.invalidate();
            }
        };

        for (SphericalObject obj : getChildren())
            obj.apply(visitor);
    }

    /**
     * Custom implementation code for updating geometrical object.
     */
    abstract void updateImpl();

    /**
     * Custom implementation code to decide if object exists with the given object
     * if parent nodes exist.
     *
     * @return is object real
     */
    abstract boolean isRealImpl();

    @Override
    public final void registerChild(SphericalObject obj) {
        children.add(obj);
    }

    @Override
    public final void unregisterChild(SphericalObject obj) {
        children.remove(obj);
    }

    @Override
    public final Iterable<SphericalObject> getChildren() {
        return children;
    }

    @Override
    public final Map<String, Object> getAppearance() {
        return appearance;
    }

    @Override
    public final Iterable<SphericalObject> getParents() {
        return Arrays.asList(parents);
    }

    @Override
    public final void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public final void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public final void show() {
        ObjectAppearanceFactory.show(this);
    }

    public final void hide() {
        ObjectAppearanceFactory.hide(this);
    }
}
