package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;

public final class ParametricPoint extends AbstractPoint {

	private double param, speed = 0;
	AbstractCurve parent;

	public ParametricPoint(AbstractCurve curve, double param) {
		super(curve);
		this.param = param;
		this.parent = curve;

		updateImpl();
	}

	public void setParam(double p) {

		if (p != 1)
			p = (p + 1.0) % 1.0;
		if (p == param)
			return;
		param = p;
		invalidate();
	}

	public double getParam() {
		return param;
	}

	public void setSpeed(double s) {
		speed = s;
	}

	public double getSpeed() {
		return speed;
	}

	public AbstractCurve getCurve() {
		return parent;
	}

	@Override
	UnitVector getLocationImpl() {
		return parent.f(param);
	}

	@Override
	public void applyPointVisitor(PointVisitor pv) {
		pv.visit(this);
	}

	@Override
	boolean isRealImpl() {
		return true;
	}

	public void step() {
		setParam(param + speed);
	}
}
