package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;

public abstract class AbstractCurve extends AbstractSphericalObject {

    AbstractCurve(SphericalObject... p) {
        super(p);
    }

    private double length = 0;

    public abstract UnitVector f(double param);

    public abstract double fInverse(UnitVector v);

    @Override
    public void updateImpl() {
        length = getLengthImpl();
    }

    public final double getLength() {
        return length;
    }

    abstract double getLengthImpl();

    public void apply(SphericalObjectVisitor v) {
        v.visit(this);
    }

    public double distance(UnitVector cursor) {
        return f(fInverse(cursor)).sphericalDistanceTo(cursor);
    }

    public abstract void applyCurveVisitor(CurveVisitor visitor);

    public interface CurveVisitor {

        void visit(LineSegment s);

        void visit(AbstractCurve c);

        void visit(Circle circle);

    }

}
