package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;

/**
 * The midpoint is the point between any two not antipodal points on the sphere.
 * Constructing the midpoint is not a trivial task on the sphere. The circle
 * method used in euclidean geometry can not be used as a general method: with
 * points at a distance greater than 2/3*pi the two circles do not intersect.
 */
public final class Midpoint extends AbstractPoint {

    AbstractPoint a, b;

    public Midpoint(AbstractPoint p1, AbstractPoint p2) {
        super(p1, p2);
        a = p1;
        b = p2;
    }

    @Override
    UnitVector getLocationImpl() {
        final UnitVector p1 = a.getLocation(), p2 = b.getLocation();
        return new UnitVector((p1.getX() + p2.getX()) / 2,
                (p1.getY() + p2.getY()) / 2, (p1.getZ() + p2.getZ()) / 2);
    }

    @Override
    public void applyPointVisitor(PointVisitor pv) {
        pv.visit(this);
    }

    @Override
    boolean isRealImpl() {
        double distance = a.getLocation().sphericalDistanceTo(b.getLocation());
        return (distance != Math.PI) && (distance != 0);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Midpoint))
            return false;
        if (obj == this)
            return true;
        final Midpoint seg = (Midpoint) obj;
        return (seg.a == a && seg.b == b) || (seg.a == b && seg.b == a);
    }

}
