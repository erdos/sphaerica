package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;

public abstract class AbstractPoint extends AbstractSphericalObject {

    UnitVector loc;

    protected AbstractPoint(SphericalObject... p) {
        super(p);
    }

    public void updateImpl() {
        loc = getLocationImpl();
    }

    abstract UnitVector getLocationImpl();

    public final UnitVector getLocation() {
        return loc;
    }

    public final void apply(SphericalObjectVisitor v) {
        v.visit(this);
    }

    public double distance(UnitVector vector) {
        return loc.sphericalDistanceTo(vector);
    }

    public abstract void applyPointVisitor(PointVisitor pv);

    public interface PointVisitor {
        void visit(Midpoint midpoint);

        void visit(FreePoint freepoint);

        void visit(ParametricPoint parametric);

        void visit(Intersection intersection);
    }
}
