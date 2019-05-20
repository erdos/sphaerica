package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;

public final class Circle extends AbstractCurve {

    private final UnitVector origo = new UnitVector(0, 0, 1),
            previousOrigo = new UnitVector(0, 0, 1);

    private final AbstractPoint center, circum;

    public Circle(AbstractPoint c) {
        super(c);
        center = c;
        circum = null;
    }

    public Circle(AbstractPoint c, AbstractPoint circ) {
        super(c, circ);
        center = c;
        circum = circ;
    }

    public double getRadiusLength() {
        return (circum == null) ? Math.PI / 2 : center.getLocation().sphericalDistanceTo(circum.getLocation());
    }

    public AbstractPoint getOrigo() {
        return center;
    }

    public UnitVector getOrigoVector() {
        return origo;
    }

    @Override
    boolean isRealImpl() {
        return true;
    }

    public void origoChanged(UnitVector from, UnitVector to) {

        if (from == null || to.dot(from) >= 1) {
            return;
        }
        final UnitVector tengely = to.cross(from).normalize();
        final double angle = to.sphericalDistanceTo(from);
        for (SphericalObject p : getChildren()) {
            if (!(p instanceof ParametricPoint))
                continue;

            ParametricPoint point = (ParametricPoint) p;
            UnitVector loc = new UnitVector(point.getLocation()).rotateZ(-tengely.getAzimuth()).rotateY(-tengely.getInclination()).rotateZ(-angle).rotateY(tengely.getInclination()).rotateZ(tengely.getAzimuth()).normalize();
            point.setParam(fInverse(loc));
        }
    }

    @Override
    public void updateImpl() {

        super.updateImpl();

        this.previousOrigo.copy(this.origo);
        this.origo.copy(center.getLocation());

        if (!this.origo.equals(this.previousOrigo))
            origoChanged(this.previousOrigo, this.origo);

    }

    @Override
    public UnitVector f(double t) {
        return new UnitVector(t * Math.PI * 2.0D, getRadiusLength()).rotateY(origo.getInclination()).rotateZ(origo.getAzimuth()).normalize();
    }

    @Override
    public double fInverse(UnitVector loc) {
        return ((loc.rotateZ(-origo.getAzimuth()).rotateY(-origo.getInclination()).getAzimuth()) / 6.283185307179586D);
    }

    @Override
    public double getLengthImpl() {
        return Math.sin(getRadiusLength()) * 3.141592653589793D * 2.0D;
    }

    @Override
    public final double distance(UnitVector loc) {
        return Math.abs(getRadiusLength() - getOrigo().getLocation().sphericalDistanceTo(loc));
    }

    @Override
    public void applyCurveVisitor(CurveVisitor visitor) {
        visitor.visit(this);
    }

    public Object getRadii() {
        return circum;
    }

}
