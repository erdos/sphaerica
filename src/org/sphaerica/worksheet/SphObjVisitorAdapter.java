package org.sphaerica.worksheet;

import org.sphaerica.worksheet.AbstractCurve.CurveVisitor;
import org.sphaerica.worksheet.AbstractPoint.PointVisitor;

/**
 * This adapter class provides a default empty implemnetation for the spherical
 * object visitor interfaces.
 */
public class SphObjVisitorAdapter implements SphericalObjectVisitor,
        CurveVisitor, PointVisitor {

    @Override
    public void visit(Midpoint midpoint) {
        visitDefaults(midpoint);
    }

    @Override
    public void visit(FreePoint freepoint) {
        visitDefaults(freepoint);
    }

    @Override
    public void visit(ParametricPoint parametric) {
        visitDefaults(parametric);
    }

    @Override
    public void visit(Intersection intersection) {
        visitDefaults(intersection);
    }

    @Override
    public void visit(AbstractCurve c) {
        c.applyCurveVisitor(this);
    }

    @Override
    public void visit(Polygon p) {
        visitDefaults(p);
    }

    @Override
    public void visit(AbstractPoint p) {
        p.applyPointVisitor(this);
    }

    @Override
    public void visit(LineSegment s) {
        visitDefaults(s);
    }

    @Override
    public void visit(Circle circle) {
        visitDefaults(circle);
    }

    void visitDefaults(SphericalObject obj) {
        throw new RuntimeException("obj not handled" + obj);
    }

}
