package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;

public final class LineSegment extends AbstractCurve {

    final AbstractPoint a, b;

    LineSegment(AbstractPoint aa, AbstractPoint bb) {
        super(aa, bb);
        a = aa;
        b = bb;
        // update();

    }

    @Override
    public UnitVector f(double t) {
        final UnitVector l1 = a.getLocation(), l2 = b.getLocation();
        final double alpha = l1.sphericalDistanceTo(l2), s = (Math.sin(t * alpha) / Math.sin(3.141592653589793D - t * alpha - 1.570796326794897D + alpha / 2.0F) / (2.0D * Math.sin(alpha / 2.0F)));
        return new UnitVector(l1.getX() * (1.0F - s) + l2.getX() * s, l1.getY() * (1.0F - s) + l2.getY() * s, l1.getZ() * (1.0F - s) + l2.getZ() * s);
    }

    @Override
    public double fInverse(UnitVector v) {
        final UnitVector origo = a.getLocation().cross(b.getLocation()).normalize(), projected = origo.cross(v).cross(origo).normalize();
        final double length = getLength(), f1 = this.a.getLocation().sphericalDistanceTo(projected), f2 = this.b.getLocation().sphericalDistanceTo(projected);
        if (f1 > length) {
            return 1.0F;
        }
        if (f2 > length) {
            return 0.0F;
        }
        return f1 / length;
    }

    @Override
    double getLengthImpl() {
        return a.getLocation().sphericalDistanceTo(b.getLocation());
    }

    @Override
    public void applyCurveVisitor(CurveVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean isRealImpl() {
        final double len = a.getLocation().sphericalDistanceTo(b.getLocation());
        return ((len > 0) && (len < 3.14159265));
    }

    @Override
    public double distance(UnitVector loc) {
        final UnitVector aa = a.getLocation(), // point
                bb = b.getLocation(); // point
        if (aa.equals(bb))
            return aa.sphericalDistanceTo(loc);
        final UnitVector o = bb.cross(aa).normalize(), // origo
                vetulet = o.cross(loc).cross(o).normalize(); // proj of loc to seg
        final double hossz = aa.sphericalDistanceTo(bb);

        if (vetulet.sphericalDistanceTo(aa) + vetulet.sphericalDistanceTo(bb) > hossz + 0.0000001d)
            return Math.min(aa.sphericalDistanceTo(loc), bb.sphericalDistanceTo(loc));
        else
            return loc.sphericalDistanceTo(vetulet);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LineSegment))
            return false;
        if (obj == this)
            return true;
        final LineSegment seg = (LineSegment) obj;
        return (seg.a == a && seg.b == b) || (seg.a == b && seg.b == a);
    }

    public final AbstractPoint from() {
        return a;
    }

    public final AbstractPoint to() {
        return b;
    }

}
