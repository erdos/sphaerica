package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;
import org.sphaerica.util.Selection;
import org.sphaerica.util.StackedUndoManager;

import javax.swing.undo.AbstractUndoableEdit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

@SuppressWarnings("serial")
public final class WorksheetWorker implements ActionListener {


    final Worksheet worksheet;
    final StackedUndoManager undoManager = new StackedUndoManager();
    final Selection<SphericalObject> selection = new Selection<SphericalObject>();

    // a single cache to speed up some geometrical constructions
    private final Map<SphericalObject, Circle> lineCache = new WeakHashMap<SphericalObject, Circle>();

    public WorksheetWorker(Worksheet sheet) {
        this.worksheet = sheet;

        undoManager.addActionListener(this);
    }

    public SphericalObject object() throws InterruptedException {
        return selection.expect(SphericalObject.class);
    }

    public AbstractPoint point() throws InterruptedException {
        return selection.expect(AbstractPoint.class);
    }

    /**
     * Creates and returns an immutable collection of all point object found in the worksheet associated with this worker.
     *
     * @return immutable collection of points in worksheet
     */
    public Collection<AbstractPoint> points() {
        final List<AbstractPoint> pts = new ArrayList<AbstractPoint>();
        for (SphericalObject obj : worksheet.getConstruction())
            if (obj instanceof AbstractPoint)
                pts.add((AbstractPoint) obj);
        return Collections.unmodifiableCollection(pts);
    }

    /**
     * Creates and returns an immutable collection of all free point object found in the worksheet associated with this worker.
     *
     * @return immutable collection of points in worksheet
     */
    public Collection<FreePoint> freepoints() {
        final List<FreePoint> pts = new ArrayList<FreePoint>();
        for (SphericalObject obj : worksheet.getConstruction())
            if (obj instanceof FreePoint)
                pts.add((FreePoint) obj);
        return Collections.unmodifiableCollection(pts);
    }

    public AbstractCurve curve() throws InterruptedException {
        return selection.expect(AbstractCurve.class);
    }

    public Circle circle() throws InterruptedException {
        return selection.expect(Circle.class);
    }

    public void select(SphericalObject obj) {
        selection.provide(obj);
    }

    public boolean accept(SphericalObject obj) {
        return selection.isAcceptable(obj.getClass());
    }

    public boolean accept(Class<? extends SphericalObject> sphClass) {
        return selection.isAcceptable(sphClass);
    }

    public FreePoint point(UnitVector vec) {
        FreePoint p = new FreePoint(vec);
        add(p);
        return p;
    }

    public FreePoint point(float azimuth, float inclination) {
        return point(new UnitVector(azimuth, inclination));
    }

    public FreePoint point(float x, float y, float z) {
        return point(new UnitVector(x, y, z));
    }

    public ParametricPoint point(AbstractCurve c, double param) {
        ParametricPoint point = new ParametricPoint(c, param);
        add(point);
        return point;
    }

    public Midpoint midpoint(AbstractPoint p1, AbstractPoint p2) {
        Midpoint p = new Midpoint(p1, p2);

        for (SphericalObject obj : worksheet.getConstruction())
            if (p.equals(obj))
                return (Midpoint) obj;

        add(p);
        return p;
    }

    public LineSegment segment(AbstractPoint from, AbstractPoint to) {
        LineSegment s = new LineSegment(from, to);
        for (SphericalObject obj : worksheet.getConstruction())
            if (s.equals(obj))
                return (LineSegment) obj;
        add(s);
        return s;
    }

    public AbstractPoint antipode(AbstractPoint to) {
        Circle l1 = line(to);
        Circle l2 = line(point(l1, 0));
        Circle l3 = line(intersection(l1, l2));
        return intersection(l3, l2);
    }

    public Circle line(AbstractPoint p) {
        if (lineCache.containsKey(p))
            return lineCache.get(p);

        Circle line = new Circle(p);
        add(line);
        lineCache.put(p, line);
        return line;
    }

    public Circle circle(AbstractPoint a, AbstractPoint b) {
        Circle line = new Circle(a, b);
        add(line);
        return line;
    }

    public Polygon poly(AbstractPoint... points) {
        Polygon poly = new Polygon(points);
        add(poly);
        return poly;
    }

    public Intersection intersection(AbstractCurve a, AbstractCurve b) {
        Intersection inter = new Intersection(a, b);
        add(inter);
        return inter;
    }

    public void add(final SphericalObject obj) {

        undoManager.addEdit(new AbstractUndoableEdit() {
            private final Set<SphericalObject> alles = new HashSet<SphericalObject>();

            {
                upload(obj);
                super.undo();
                redo();
            }

            private void upload(SphericalObject o) {
                this.alles.add(o);
                for (SphericalObject child : o.getChildren())
                    upload(child);
            }

            public void redo() {
                super.redo();

                for (SphericalObject obj : this.alles)
                    worksheet.getConstruction().add(obj);
            }

            public void undo() {
                super.undo();
                for (SphericalObject obj : this.alles)
                    worksheet.getConstruction().remove(obj);
            }
        });

    }

    public void remove(final SphericalObject obj) {

        if (obj == null) throw new IllegalArgumentException("can not remove null object.");

        undoManager.addEdit(new AbstractUndoableEdit() {
            private final Set<SphericalObject> alles = new HashSet<SphericalObject>();

            {
                upload(obj);
                super.undo();
                redo();
            }

            private void upload(SphericalObject o) {
                this.alles.add(o);
                for (SphericalObject child : o.getChildren())
                    upload(child);
            }

            public void redo() {
                super.redo();
                worksheet.getConstruction().remove(obj);
            }

            public void undo() {
                super.undo();
                for (SphericalObject obj : this.alles)
                    worksheet.getConstruction().add(obj);
            }
        });

    }

    public void change(final SphericalObject o, final String key, final Object newValue) {
        if (!worksheet.getConstruction().contains(o))
            return;

        final Object oldValue = o.getAppearance().get(key);

        if (((oldValue == null) && (newValue == null)) || (oldValue.equals(newValue)))
            return;

        o.getAppearance().put(key, newValue);
        undoManager.addEdit(new AbstractUndoableEdit() {

            public void redo() {
                super.redo();
                o.getAppearance().put(key, newValue);
            }

            public void undo() {
                super.undo();
                o.getAppearance().put(key, oldValue);
            }
        });
    }

    public void update() {
        for (SphericalObject obj : worksheet.getConstruction())
            obj.update();
    }

    public void fire() {
        worksheet.fireChangeListeners();
    }

    public Worksheet getWorksheet() {
        return worksheet;
    }

    public StackedUndoManager getUndoable() {
        return undoManager;
    }

    public Selection<SphericalObject> getSelection() {
        return selection;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fire();
    }


    // XXX implement symmetrical modifers.
    // XXX also, impleemnt basic symmetry groups.
    private boolean sym = false;

    public void setSymmetrical(boolean sym) {
        this.sym = sym;
    }

    public boolean isSymmetrical() {
        return sym;
    }

}
