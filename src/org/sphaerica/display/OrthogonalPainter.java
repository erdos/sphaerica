package org.sphaerica.display;

import org.sphaerica.display.SphereDisplayPanel.PaintRecord;
import org.sphaerica.math.ArcBall;
import org.sphaerica.math.Projection;
import org.sphaerica.math.SphericalMath;
import org.sphaerica.math.UnitVector;
import org.sphaerica.worksheet.SphericalObjectVisitor;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Deque;

/**
 * A spherical object painter and collection of algorithms for orthogonal
 * projection of the sphere. Many algorithms use the simple fact that with this
 * projection, the image of spherical circles will be sime ellipses.
 */
public final class OrthogonalPainter implements ProjectionPainter {

    private ArcBall ball;

    public OrthogonalPainter(ArcBall b) {
        ball = b;
    }

    @Override
    public Projection getProjection() {
        return Projection.ORTHOGRAPHIC;
    }

    @Override
    public SphericalObjectVisitor createVisitorPainter(
            final Deque<PaintRecord> deque) {
        return new DummyVisitorPainter(deque, ball, getProjection()) {

            public Shape createCircle(UnitVector origo, double radius,
                                      boolean front) {

                if (radius >= Math.PI / 2) {
                    return createCircle(new UnitVector(origo).antipode(),
                            Math.PI - radius - 0.00001, front);
                }

                final double r = ball.getScale();
                double[] kozep = onPlaneCoords(origo);
                double circleRadius = radius;
                double depth = kozep[2];
                double theta = -Math.atan2(kozep[1], kozep[0]) + Math.PI / 2
                        + Math.PI;

                final double press = Math.abs(depth);
                final double gamma = Math.acos(press);
                double o, opening;

                if (gamma + circleRadius > Math.PI / 2)
                    o = Math.acos(Math.tan(Math.PI / 2 - gamma)
                            / Math.tan(circleRadius));
                else
                    o = 0;

                if (depth < 0)
                    o = Math.PI - o;

                opening = o * 180 / Math.PI;

                final double kerek = 90 + (depth < 0 ? 180 : 0);
                final double offset = -Math.sin(gamma) * Math.cos(circleRadius)
                        * r;
                final double sugar = Math.sin(circleRadius) * r;

                final AffineTransform rot = AffineTransform.getRotateInstance(
                        -theta, 0, 0);
                final Shape sh;

                if (front && opening < 180) {
                    sh = rot.createTransformedShape(new Arc2D.Double(-sugar,
                            -sugar * press + offset, 2 * sugar, 2 * sugar
                            * press, kerek + opening,
                            360 - opening * 2, Arc2D.OPEN));
                } else if (opening > 0 && !front) {
                    sh = rot.createTransformedShape(new Arc2D.Double(-sugar,
                            -sugar * press + offset, 2 * sugar, 2 * sugar
                            * press, kerek - opening, opening * 2,
                            Arc2D.OPEN));
                } else
                    return null;
                return sh;
            }

            @Override
            public Shape createSegment(final Point2D a, final Point2D b,
                                       boolean firstUp, boolean secondUp) {
                if (!firstUp && !secondUp)
                    return null;

                final double R = ball.getScale();
                final double ax = a.getX(), ay = a.getY(), aa = R * R - ax * ax
                        - ay * ay, az = (aa > 0) ? (Math.sqrt(aa) * (firstUp ? 1
                        : -1))
                        : 0;
                final double bx = b.getX(), by = b.getY(), bb = R * R - bx * bx
                        - by * by, bz = (bb > 0) ? (Math.sqrt(bb) * (secondUp ? 1
                        : -1))
                        : 0;
                final double ox = ay * bz - by * az, oy = az * bx - ax * bz, oz = ax
                        * by - ay * bx;

                final double theta = Math.atan2(oy, ox) - Math.PI / 2;
                double beta = Math.acos(Math.cos(theta - Math.atan2(ay, ax))
                        * Math.min(1, a.distance(0, 0) / R));

                if (ox == 0 && oy == 0) {
                    double angle = -Math.atan2(ay, ax);
                    double len = SphericalMath.angularDistance(
                            Math.atan2(ay, ax), Math.atan2(by, bx));
                    return new Arc2D.Double(-R, -R, R + R, R + R, angle
                            / Math.PI * 180, len / Math.PI * 180, Arc2D.OPEN);
                }

                final double len, angle;
                if (firstUp && secondUp) {
                    len = -2.0
                            * Math.acos(0.5
                            * Math.sqrt((ax + bx) * (ax + bx)
                            + (ay + by) * (ay + by) + (az + bz)
                            * (az + bz)) / R);
                    angle = +beta;
                } else if (firstUp && !secondUp) {
                    len = -beta;
                    angle = +beta;
                } else if (!firstUp && secondUp) {
                    len = Math.acos(Math.cos(theta - Math.atan2(by, bx))
                            * b.distance(0, 0) / R)
                            - Math.PI;
                    angle = Math.PI;
                } else
                    return null;

                final AffineTransform transform = new AffineTransform();
                transform.rotate(theta);
                transform.scale(1, oz / Math.sqrt(ox * ox + oy * oy + oz * oz));
                return transform.createTransformedShape(new Arc2D.Double(-R,
                        -R, 2 * R, 2 * R, angle / Math.PI * 180, len / Math.PI
                        * 180, Arc2D.OPEN));
            }

        };
    }

    @Override
    public Shape getMapShape() {
        return new Ellipse2D.Double(-1, -1, 2, 2);
    }

    @Override
    public ArcBall getArcBall() {
        return ball;
    }

}
