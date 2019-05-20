package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;

public final class FreePoint extends AbstractPoint {

    private final UnitVector loc = new UnitVector(0, 0, 1);

    FreePoint(UnitVector vec) {
        super();
        setLocation(vec);
        update();
    }

    @Override
    UnitVector getLocationImpl() {
        return loc;
    }

    public final void setLocation(UnitVector location) {
        loc.copy(location);
        invalidate();
    }

    @Override
    boolean isRealImpl() {
        return true;
    }

    @Override
    public void applyPointVisitor(PointVisitor pv) {
        pv.visit(this);
    }

}
