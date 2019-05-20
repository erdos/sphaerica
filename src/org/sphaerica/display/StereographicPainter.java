package org.sphaerica.display;

import org.sphaerica.display.SphereDisplayPanel.PaintRecord;
import org.sphaerica.math.*;
import org.sphaerica.worksheet.AbstractCurve;
import org.sphaerica.worksheet.Circle;
import org.sphaerica.worksheet.SphericalObjectVisitor;

import java.awt.*;
import java.awt.geom.*;
import java.util.Deque;

/**
 * An object painter and collection of algorithms for stereographic projection.
 */
public final class StereographicPainter implements ProjectionPainter {

    private ArcBall ball;

    /**
     * Circles with origin point this far from the centre and radius bigger than this
     * value will not behave smoothly and continuously. This is a measured value
     * and may be adjusted over time.
     */
    private final static int CIRCLE_ANOMALY_RADIUS = 5000;

    public StereographicPainter(ArcBall b) {
        ball = b;
    }

    @Override
    public Projection getProjection() {
        return Projection.STEREOGRAPHIC;
    }

    @Override
    public SphericalObjectVisitor createVisitorPainter(
            final Deque<PaintRecord> deque) {
        return new DummyVisitorPainter(deque, ball, getProjection()) {

            public void visit(Circle circle) {

                double start = circle.fInverse(ball
                        .doInverseTransformation(new Vector3D(0, 0, 1)));
                double s = ball.getScale();

                double[] one = super.onPlaneCoords(circle.f(start));
                double[] two = super.onPlaneCoords(circle.f((start + 0.5) % 1));
                double radi = Math.sqrt((one[0] - two[0]) * (one[0] - two[0])
                        + (one[1] - two[1]) * (one[1] - two[1]))
                        * s / 2;

                if (radi > CIRCLE_ANOMALY_RADIUS) {
                    handleCurve(circle);
                    return;
                }

                double[] centro = new double[]{(one[0] + two[0]) / 2 * s,
                        (one[1] + two[1]) / 2 * s};
                final Color color = color(circle);
                final Ellipse2D ellipse = new Ellipse2D.Double();
                ellipse.setFrameFromDiagonal(centro[0] - radi,
                        centro[1] - radi, centro[0] + radi, centro[1] + radi);
                deque.add(new PaintRecord(
                        stroke(circle).createStrokedShape(ellipse), color));
            }

            protected Shape createSegment(Point2D first, Point2D second,
                                          boolean firstUp, boolean secondUp) {

                if (!firstUp || !secondUp || first.equals(second))
                    return null;

                final double r = ball.getScale();

                final UnitVector a = new UnitVector(
                        Projection.STEREOGRAPHIC.onSphere(new double[]{
                                first.getX() / r, first.getY() / r, 0}));
                final UnitVector b = new UnitVector(
                        Projection.STEREOGRAPHIC.onSphere(new double[]{
                                second.getX() / r, second.getY() / r, 0}));
                final UnitVector cross = a.cross(b).normalize();
                final double shadow = cross.getShadow().distance(0, 0);

                final double d1 = Math
                        .sin(cross.getInclination() + Math.PI / 2)
                        / (1d - Math.cos(cross.getInclination() + Math.PI / 2));
                final double d2 = Math
                        .sin(cross.getInclination() - Math.PI / 2)
                        / (1d - Math.cos(cross.getInclination() - Math.PI / 2));

                Point2D center = new Point2D.Double(cross.getX() / shadow * r
                        * (d1 + d2) / 2, cross.getY() / shadow * r * (d1 + d2)
                        / 2);

                double radius = Math.abs((d1 - d2) / 2 * r);
                Arc2D arc = new Arc2D.Double();
                arc.setFrame(center.getX() - radius, center.getY() - radius,
                        radius * 2, radius * 2);
                double start = -Math.atan2(first.getY() - center.getY(),
                        first.getX() - center.getX());
                double start2 = -Math.atan2(second.getY() - center.getY(),
                        second.getX() - center.getX());
                double ext = SphericalMath.angularDistance(start2, start);

                arc.setAngleStart(start / Math.PI * 180);
                arc.setAngleExtent(ext / Math.PI * 180);
                if (arc.contains(new Point2D.Double(0, 0)))
                    arc.setAngleExtent(arc.getAngleExtent() - 360
                            * Math.signum(ext));

                return arc;
            }

            protected Shape createTriangle(UnitVector a, UnitVector b,
                                           UnitVector c, boolean top) {
                if (!top)
                    return null;

                if (SphericalMath.isInsideTriangle(ball
                        .doInverseTransformation(new UnitVector(0, 0, 1))
                        .normalize(), a, b, c)) {
                    Area area = new Area(new Rectangle2D.Double(-10000, -10000,
                            20000, 20000));
                    area.subtract(new Area(super.createTriangle(b, a, c, true)));
                    return area;
                } else
                    return super.createTriangle(a, b, c, true);
            }

            private final void handleCurve(AbstractCurve curve) {

                int steps = 60;
                double s = ball.getScale();

                Path2D path = new Path2D.Double();
                // path.moveTo(0, 0);

                double start = curve.fInverse(ball
                        .doInverseTransformation(new Vector3D(0, 0, 1)));
                path.moveTo(0, 0);

                {
                    double[] temp = super.onPlaneCoords(curve.f(start));
                    path.moveTo(temp[0] * s, temp[1] * s);
                }

                for (int i = steps; i > 0; i--) {
                    double x = start + 1.0 * i / steps;
                    final UnitVector current = curve.f(x);
                    double[] temp = super.onPlaneCoords(current);
                    path.lineTo(temp[0] * s, temp[1] * s);
                }
                path.closePath();

                deque.add(new PaintRecord(stroke(curve).createStrokedShape(path), color(curve)));
            }
        };
    }

    @Override
    public Shape getMapShape() {
        return null;
    }

    @Override
    public ArcBall getArcBall() {
        return ball;
    }

}
