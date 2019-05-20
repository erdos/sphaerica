package org.sphaerica.worksheet;

import org.sphaerica.math.SphericalMath;
import org.sphaerica.math.UnitVector;

public class Polygon extends AbstractSphericalObject {

    private final AbstractPoint[] points;
    private double area = 0;

    public Polygon(AbstractPoint... points) {
        super(points);
        this.points = points;
    }

    @Override
    public void apply(SphericalObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public double distance(UnitVector o) {

        for (int i = 0; i < points.length - 2; i++)
            if (SphericalMath.isInsideTriangle(o, points[i].getLocation(), points[i + 1].getLocation(), points[i + 2].getLocation()))
                return 0;

        return Double.POSITIVE_INFINITY; // TODO do precise calculations.
    }

    @Override
    void updateImpl() {
        area = 0;
        for (int i = 0; i < points.length - 2; i++)
            area += Math.PI * 2 - SphericalMath.area(points[i].getLocation(), points[i + 1].getLocation(), points[i + 2].getLocation());
    }

    @Override
    boolean isRealImpl() {
        return !Double.isNaN(area);
    }

    public double getArea() {
        return area;
    }

    public AbstractPoint[] getPoints() {
        return points;
    }
}
