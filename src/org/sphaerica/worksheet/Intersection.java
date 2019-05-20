package org.sphaerica.worksheet;

import org.sphaerica.math.SphericalMath;
import org.sphaerica.math.UnitVector;
import org.sphaerica.math.Vector3D;

/**
 * The intersection point has a location that can be found on both of its parent
 * circles. The current implementation supports all circle like objects.
 */
public final class Intersection extends AbstractPoint {

    final AbstractCurve firstObject, secondObject;

    public Intersection(AbstractCurve first, AbstractCurve second) {
        super(first, second);
        firstObject = first;
        secondObject = second;
        if (first == second)
            throw new IllegalArgumentException("curves must be different!");
    }

    public UnitVector getLocationImpl() {
        final double r1 = circleRadius(firstObject), r2 = circleRadius(secondObject);
        final UnitVector o1 = circleOrigo(firstObject), o2 = circleOrigo(secondObject);

        final UnitVector loc = intersectCircleCircle(o1, r1, o2, r2, true);

        if (loc == null)
            return null;
        else if (SphericalMath.isCCOrColinear(o1, o2, loc))
            return loc;
        else
            return intersectCircleCircle(o1, r1, o2, r2, false);
    }

    /**
     * If the curve is a circle or line segment, returns its radius. Returns NaN
     * otherwise.
     *
     * @param curve any curve not null
     * @return radius of circle or NaN
     */
    private static double circleRadius(AbstractCurve curve) {
        if (curve instanceof Circle)
            return ((Circle) curve).getRadiusLength();
        else if (curve instanceof LineSegment)
            return Math.PI / 2;
        else
            return Double.NaN;
    }

    /**
     * If the curve is a circle or line segment, returns its central point.
     * Returns null otherwise.
     *
     * @param curve any curve not null
     * @return central point of circle or null
     */
    private static UnitVector circleOrigo(AbstractCurve curve) {
        if (curve instanceof Circle)
            return ((Circle) curve).getOrigoVector();
        else if (curve instanceof LineSegment)
            return ((LineSegment) curve).a.getLocation()
                    .cross(((LineSegment) curve).b.getLocation()).normalize();
        else
            return null;
    }

    /**
     * Returns an intersection point of the two circles.
     *
     * @param ac   center of first circle
     * @param ar   radius of first circle
     * @param bc   center of second circle
     * @param br   radius of second circle
     * @param step first or second intersection point should be returned
     * @return intersection point locatioin of two circles
     */
    private static UnitVector intersectCircleCircle(UnitVector ac,
                                                    double ar, UnitVector bc, double br, boolean step) {
        final Vector3D cross = ac.cross(bc);
        final double dotA = Math.cos(ar), dotB = Math.cos(br);

        if (dotA == 0 && dotB == 0)
            return new UnitVector(cross);

        double a = cross.dot();
        double b = 2f * (dotB * ac.cross(cross).getZ() - dotA
                * bc.cross(cross).getZ());
        double c = dotB * dotB
                * (ac.getX() * ac.getX() + ac.getY() * ac.getY()) + dotA * dotA
                * (bc.getX() * bc.getX() + bc.getY() * bc.getY()) - 2f * dotA
                * dotB * (ac.getX() * bc.getX() + ac.getY() * bc.getY())
                - cross.getZ() * cross.getZ();
        final double z = (-b + (step ? +1.0 : -1.0) * Math.sqrt(b * b - 4f * a * c))
                / (2f * a);
        final double x = (bc.getY() * dotA - ac.getY() * dotB + cross.getX() * z)
                / cross.getZ();
        final double y = (ac.getX() * dotB - bc.getX() * dotA + cross.getY() * z)
                / cross.getZ();

        if (Double.isNaN(z))
            return null;
        else
            return new UnitVector(x, y, z);
    }

    @Override
    public boolean isRealImpl() {
        final UnitVector l = getLocation();
        final double precision = 0.02;

        return (l != null && firstObject.distance(loc) <= precision && secondObject
                .distance(loc) <= precision);
    }

    @Override
    public void applyPointVisitor(PointVisitor pv) {
        pv.visit(this);
    }
}