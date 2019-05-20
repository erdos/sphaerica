package org.sphaerica.display;

import org.sphaerica.display.SphereDisplayPanel.PaintRecord;
import org.sphaerica.math.ArcBall;
import org.sphaerica.math.Projection;
import org.sphaerica.math.SphericalMath;
import org.sphaerica.math.UnitVector;
import org.sphaerica.worksheet.*;
import org.sphaerica.worksheet.AbstractCurve.CurveVisitor;
import org.sphaerica.worksheet.Polygon;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Deque;

/**
 * A visitor painter implementation with default behavior for painting spherical
 * objects. This class can be used as a base class for specific algorithms on
 * displaying projections of spherical objects.
 */
public class DummyVisitorPainter implements SphericalObjectVisitor,
        CurveVisitor {

    private final Deque<PaintRecord> deck;
    private final ArcBall ball;
    private final Projection proj;

    /**
     * Constructs the visitor using the predefined arcball and projection. The
     * constructor will throw an IllegalArgumentException if called with any
     * null parameter.
     *
     * @param deck       double ended queue for the layers of the sphere, not null
     * @param ball       arcball used by the user, not null
     * @param projection projection used with this painter, not null
     */
    DummyVisitorPainter(Deque<PaintRecord> deck, ArcBall ball,
                        Projection projection) {
        if (deck == null)
            throw new IllegalArgumentException("null deck param is forbidden.");
        if (ball == null)
            throw new IllegalArgumentException("null ball param is forbidden.");
        if (projection == null)
            throw new IllegalArgumentException(
                    "null projection param is forbidden.");

        this.deck = deck;
        this.ball = ball;
        this.proj = projection;
    }

    @Override
    public void visit(AbstractCurve curve) {
        curve.applyCurveVisitor(this);
    }

    @Override
    public void visit(AbstractPoint point) {
        final int ps = calculateSize(point);
        final Color pc = (Color) point.getAppearance().get("color");
        assert (point.getLocation() != null);

        final double[] d = onPlaneCoords(point.getLocation());
        if (d[2] < 0 || Double.isNaN(d[0]))
            return;

        final double r = ball.getScale();

        final Shape sh = new Ellipse2D.Double(d[0] * r - ps, d[1] * r - ps,
                2 * ps, 2 * ps);

        deck.add(new PaintRecord(sh, pc));
        deck.add(new PaintRecord(new BasicStroke(1).createStrokedShape(sh), pc
                .darker()));
    }

    /**
     * Calculates projected plane coordinates of unit vector rotated by the
     * arcball.
     *
     * @param vector unit vector on the sphere
     * @return projection of the rotated vector
     */
    final double[] onPlaneCoords(UnitVector vector) {
        return proj.onPlane(ball.doRotation(vector.toArray()));
    }

    @Override
    public void visit(LineSegment segment) {

        final Shape s1 = createSegment(segment.from().getLocation(), segment
                .to().getLocation(), true);
        final Shape s2 = createSegment(segment.from().getLocation(), segment
                .to().getLocation(), false); // backface

        final Color pc = (Color) segment.getAppearance().get("color");

        if (s1 != null)
            deck.addLast(new PaintRecord(
                    stroke(segment).createStrokedShape(s1), pc));

        if (s2 != null)
            deck.addFirst(new PaintRecord(stroke(segment)
                    .createStrokedShape(s2), PainterHelper.backfaceColor(pc)));

    }

    @Override
    public void visit(Circle circle) {

        final Color color = (Color) circle.getAppearance().get("color");

        // front stroke
        final Shape outline = createCircle(circle.getOrigoVector(),
                circle.getRadiusLength(), true);
        if (outline != null)
            deck.addLast(new PaintRecord(stroke(circle).createStrokedShape(
                    outline), color));

        // back stroke
        final Shape outline2 = createCircle(circle.getOrigoVector(),
                circle.getRadiusLength(), false);
        if (outline2 != null)
            deck.addFirst(new PaintRecord(stroke(circle).createStrokedShape(
                    outline2), PainterHelper.backfaceColor(color)));
    }

    /**
     * Creates a projection of the spherical circle for the given origo and
     * radius length. This function can be used to create parts of the
     * projection from both the visible and hidden parts of the sphere.
     *
     * @param origoVector  origo of circle on sphere
     * @param radiusLength radius of circle
     * @param front        tells if the projection of the visible or invisible part of
     *                     the sphere shall be created
     * @return shape for projection of circle
     */
    protected Shape createCircle(UnitVector origoVector, double radiusLength, boolean front) {
        throw new RuntimeException("method not implemented yet!!");
    }

    /**
     * Calculates size property of the given object. Mouse hover is also
     * considered.
     *
     * @param obj object to query size
     * @return size property of object
     */
    private int calculateSize(SphericalObject obj) {
        return (Integer) obj.getAppearance().get("size")
                + (obj.getAppearance().containsKey("hovered") ? 2 : 0);
    }

    /**
     * Calculates the color property of the object.
     *
     * @param obj object to query color
     * @return color for object
     */
    public final Color color(SphericalObject obj) {
        return (Color) obj.getAppearance().get("color");
    }

    /**
     * Creates stroke for given object. This method uses the calculateSize
     * method for stroke size.
     *
     * @param obj object to paint with stroke
     * @return Stroke instance for object
     */
    final Stroke stroke(SphericalObject obj) {
        return new BasicStroke(calculateSize(obj));
    }

    @Override
    public void visit(Polygon poly) {

        AbstractPoint[] points = poly.getPoints();
        Color frontColor = (Color) poly.getAppearance().get("color");
        Color backColor = PainterHelper.backfaceColor(frontColor);

        Shape front = null, back = null;

        for (int i = 0; i < points.length - 2; i++) {
            UnitVector a = points[i].getLocation(), b = points[i + 1]
                    .getLocation(), c = points[i + 2].getLocation();

            if (!SphericalMath.isCounterClockwise(a, b, c)) {
                UnitVector t = a;
                a = b;
                b = t;
            }

            // deck.addLast(new PaintRecord(createTriangle(a, b, c, true),
            // color));
            // deck.addFirst(new PaintRecord(createTriangle(a, b, c, false),
            // backface));

            front = mergeShapes(front, createTriangle(a, b, c, true));
            back = mergeShapes(back, createTriangle(a, b, c, false));
        }

        deck.addLast(new PaintRecord(front, frontColor));
        deck.addFirst(new PaintRecord(back, backColor));

        // deck.add(new PaintRecord(connect(edges.toArray(new Path2D[] {})),
        // Color.ORANGE));
    }

    /**
     * Merges two shapes. Null parameters are permitted. If both parameters are
     * null, returns null.
     *
     * @param s1 first shape
     * @param s2 second shape
     * @return two shapes merged or null if both parameters are null.
     */
    private static Shape mergeShapes(Shape s1, Shape s2) {
        if (s1 == null)
            return s2;
        if (s2 == null)
            return s1;
        Path2D p = new Path2D.Float(Path2D.WIND_NON_ZERO);
        p.append(s1, false);
        p.append(s2, false);
        return p;
    }

    /**
     * Creates a projected shape of a spheical triangle.
     *
     * @param a   first point of triangle
     * @param b   second point of triangle
     * @param c   third point of triangle
     * @param top decides if the calculated projection is from the visible or
     *            hidden region of the sphere.
     * @return shape of projection of spherical triangle
     */
    protected Shape createTriangle(UnitVector a, UnitVector b, UnitVector c,
                                   boolean top) {
        return connect(createSegment(a, b, top), createSegment(b, c, top),
                createSegment(c, a, top));
    }

    /**
     * Creates a projection of a spherical line segment. This method can be used
     * to generate parts of the segment from both the visible and invisible
     * regions of the sphere.
     *
     * @param from  one end of the segment
     * @param to    other end of the segment
     * @param front decides if the projection is from the visible region of the
     *              sphere or from the invisible part.
     * @return shape projection of a spherical line segment.
     */
    private Shape createSegment(UnitVector from, UnitVector to,
                                boolean front) {
        double[] a = onPlaneCoords(from), b = onPlaneCoords(to);
        double r = ball.getScale();
        return createSegment(new Point2D.Double(a[0] * r, a[1] * r),
                new Point2D.Double(b[0] * r, b[1] * r), a[2] >= 0 == front,
                b[2] >= 0 == front);
    }

    /**
     * Creates a projection of a spherica segment connecting projections of
     * spherical points.
     *
     * @param first    one end of spherical segment
     * @param second   other end of sphericalSegment
     * @param firstUp  first end is on the visible or hidden region of sphere
     * @param secondUp second end is on visible or hidden region of sphere
     * @return shape of projection of line segment
     */
    protected Shape createSegment(Point2D first, Point2D second,
                                  boolean firstUp, boolean secondUp) {
        throw new RuntimeException("method not implemented yet!");
    }

    /**
     * Connects shapes to one continuous shape.
     *
     * @param paths array of shapes
     * @return composition of the shapes
     */
    private Path2D connect(Shape... paths) {
        final Path2D retur = new Path2D.Double();

        Point2D last = null;
        for (int i = paths.length; i-- > 0; ) {
            if (paths[i] != null) {
                last = last(paths[i]);
                break;
            }
        }

        if (last == null)
            return retur;

        for (Shape path : paths) {
            if (path == null)
                continue;
            Point2D first = first(path);
            if (last.distance(first) > 2)
                retur.append(createSegment(last, first, true, true), true);
            retur.append(path, true);
            last = last(path);
        }

        return retur;
    }

    /**
     * Returns first point of Shape object.
     *
     * @return Point2D object first point
     */
    private Point2D first(Shape shape) {
        double pont[] = new double[3];
        shape.getPathIterator(null).currentSegment(pont);
        return new Point2D.Double(pont[0], pont[1]);
    }

    /**
     * Returns last point of Shape2D object.
     */
    private Point2D last(Shape shape) {
        if (shape == null)
            return null;
        return new Path2D.Double(shape).getCurrentPoint();
    }
}
