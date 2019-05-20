package org.sphaerica.worksheet;

/**
 * Visitor interface for spherical objects. When applied to a spherical object,
 * the corresponding method of the visitor will be called.
 */
public interface SphericalObjectVisitor {

    void visit(AbstractCurve c);

    void visit(Polygon p);

    void visit(AbstractPoint p);
}