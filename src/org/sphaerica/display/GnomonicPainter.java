package org.sphaerica.display;

import org.sphaerica.display.SphereDisplayPanel.PaintRecord;
import org.sphaerica.math.ArcBall;
import org.sphaerica.math.Projection;
import org.sphaerica.math.UnitVector;
import org.sphaerica.math.Vector3D;
import org.sphaerica.worksheet.AbstractCurve;
import org.sphaerica.worksheet.Circle;
import org.sphaerica.worksheet.SphericalObjectVisitor;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Deque;

/**
 * A painter object and a collection of algorithms for the gnomonic projection.
 */
public final class GnomonicPainter implements ProjectionPainter {

    private ArcBall ball;

    public GnomonicPainter(ArcBall b) {
        ball = b;
    }

    @Override
    public Projection getProjection() {
        return Projection.GNOMONIC;
    }

    @Override
    public SphericalObjectVisitor createVisitorPainter(final Deque<PaintRecord> deque) {
        return new DummyVisitorPainter(deque, ball, getProjection()) {

            public void visit(Circle circle) {
                final Color color = color(circle);
                final Stroke stroke = stroke(circle);

                final Shape p1 = handleCurve(circle, true);
                deque.addLast(new PaintRecord(stroke.createStrokedShape(p1), color));

                final Shape p2 = handleCurve(circle, false);
                deque.addFirst(new PaintRecord(stroke.createStrokedShape(p2), PainterHelper.backfaceColor(color)));
            }

            protected Shape createSegment(Point2D first, Point2D second,
                                          boolean firstUp, boolean secondUp) {

                if ((!firstUp && !secondUp) || first.equals(second))
                    return null;

                if (firstUp && secondUp)
                    return new Line2D.Double(first, second);

                final double r = ball.getScale();

                final UnitVector a = new UnitVector(
                        Projection.GNOMONIC.onSphere(new double[]{
                                first.getX() / r, first.getY() / r, 0}));
                final UnitVector b = new UnitVector(
                        Projection.GNOMONIC.onSphere(new double[]{
                                second.getX() / r, second.getY() / r, 0}));
                final UnitVector cross = a.cross(b).normalize();

                final UnitVector border = cross.cross(UnitVector.NORTH_POLE)
                        .normalize();
                final double irany = -Math.atan2(border.getY(), border.getX())
                        - Math.PI / 2;

                final double sugar = 10000;

                if (firstUp && !secondUp) {
                    Point2D seco = new Point2D.Double(first.getX() + sugar
                            * Math.sin(irany), first.getY() + sugar
                            * Math.cos(irany));
                    return new Line2D.Double(first, seco);

                } else {
                    Point2D seco = new Point2D.Double(second.getX() - sugar
                            * Math.sin(irany), second.getY() - sugar
                            * Math.cos(irany));
                    return new Line2D.Double(seco, second);
                }
            }

            private Shape handleCurve(AbstractCurve curve, boolean front) {

                int steps = 60;
                double s = ball.getScale();

                Path2D path = new Path2D.Double();

                double start = curve.fInverse(ball
                        .doInverseTransformation(new Vector3D(0, 0, front ? -1
                                : +1)));
                double[] latest = super.onPlaneCoords(curve.f(start));
                for (int i = 0; i < steps; i++) {
                    double x = start + 1.0 * i / steps;
                    final UnitVector current = curve.f(x);
                    double[] temp = super.onPlaneCoords(current);

                    if (temp[2] < 0 == front) {
                        latest = temp;
                        continue;
                    }
                    if (temp[0] * temp[0] + temp[1] * temp[1] > 1000000) {
                        latest = temp;
                        continue;
                    }

                    if (latest[0] * temp[0] + latest[1] * temp[1] < 10000
                            && path.getCurrentPoint() != null)
                        path.lineTo(temp[0] * s, temp[1] * s);
                    else
                        path.moveTo(temp[0] * s, temp[1] * s);
                    latest = temp;
                }

                if (latest[2] > 0 == front)
                    path.closePath();
                return path;
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
