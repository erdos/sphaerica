package org.sphaerica.display;

import org.sphaerica.math.ArcBall;
import org.sphaerica.math.UnitVector;
import org.sphaerica.worksheet.*;
import org.sphaerica.worksheet.Polygon;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * This panel displays the image of the sphere and the spherical objects using a
 * projection and an arc ball.
 */
@SuppressWarnings("serial")
public final class SphereDisplayPanel extends JPanel implements
        MouseInputListener, MouseMotionListener, MouseWheelListener {

    final static class PaintRecord {
        final Paint paint;
        final Shape shape;

        PaintRecord(Shape s, Paint p) {
            shape = s;
            paint = p;
        }
    }

    public interface SphereCanvasCallback {
        void locationSelected(UnitVector vec);

        void objectSelected(SphericalObject obj);

        void repainted();
    }

    /**
     * Background color. It is not always visible.
     */
    private Color background = new Color(0xbabcb6);

    /**
     * Foreground color is the color of the sphere.
     */
    private Color foreground = new Color(0xeeeeec);

    /**
     * Default arc ball used for the rotation and scaling of the sphere
     */
    private final ArcBall ball = new ArcBall();

    /**
     * Current object under the mouse cursor.
     */
    private SphericalObject hover = null;

    /**
     * Location of cursor on the sphere.
     */
    private Point cursorOnScreen = null;

    private final SphereCanvasCallback callback;

    private ProjectionPainter projection = new OrthogonalPainter(ball);

    // maps from screen to sphere. does all calculations, even transitions.

    private final Worksheet sheet;

    public SphereDisplayPanel(Worksheet sheet, SphereCanvasCallback callback) {
        this.sheet = sheet;
        this.callback = callback;

        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
        setBackground(Color.white);
    }

    /**
     * Decides if this object is selectable by the mouse cursor.
     *
     * @param obj spherical object under the mouse cursor
     * @return true iff object is visible and selectable
     */
    private boolean canProcess(SphericalObject obj) {
        obj.update();
        if (!obj.isReal())
            return false;
        if (!obj.getAppearance().containsKey(
                ObjectAppearanceFactory.KEY_VISIBILITY))
            return false;
        if (obj.getAppearance().get(ObjectAppearanceFactory.KEY_VISIBILITY)
                .equals(Boolean.FALSE))
            return false;
        return true;
    }

    /**
     * Returns default arc ball used for rotation and scaling of the sphere.
     *
     * @return default arc ball
     */
    public final ArcBall getArcBall() {
        return ball;
    }

    /**
     * Calculates the spherical location of the mouse cursor
     *
     * @return spherical location of cursor
     */
    public UnitVector getCursorVector() {
        return map(cursorOnScreen);
    }

    /**
     * Maps screen point to spherical location. Returns null if the point is not
     * on the image of sphere.
     *
     * @param screen point on the screen
     * @return location on the surface of the sphere or null of not on it.
     */
    private UnitVector map(Point2D screen) {
        double[] a = new double[]{
                (screen.getX() - getWidth() / 2) / ball.getScale(),
                (screen.getY() - getHeight() / 2) / ball.getScale(), 0};
        projection.getProjection().onSphere(a);
        if (Double.isNaN(a[2]) || Double.isNaN(a[1]))
            return null;
        return ball.doInverseTransformation(new UnitVector(a[0], a[1], a[2]))
                .normalize();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (cursorOnScreen == null)
            return; // TODO quick fix for first run without window focus.

        final UnitVector cursor = map(cursorOnScreen);

        if (hover != null)
            callback.objectSelected(hover);
        else if (cursor != null)
            callback.locationSelected(cursor);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        final Point from = cursorOnScreen == null ? e.getPoint()
                : cursorOnScreen;
        final UnitVector cursor = map(cursorOnScreen = e.getPoint());

        if ((hover != null) & ((hover instanceof FreePoint)) & (cursor != null)) {
            ((FreePoint) hover).setLocation(cursor);
        } else if ((hover != null) & (hover instanceof ParametricPoint)
                & (cursor != null)) {
            ParametricPoint pp = (ParametricPoint) hover;
            pp.setParam(pp.getCurve().fInverse(cursor));
        } else {
            int cx = getWidth() / 2, cy = getHeight() / 2;
            final UnitVector a = map(from), b = map(cursorOnScreen);
            if (a == null || b == null) {
                ball.rotate(
                        map(new Point2D.Double(getWidth() / 2, getHeight() / 2)),
                        Math.atan2(from.y - cy, from.x - cx)
                                - Math.atan2(cursorOnScreen.y - cy,
                                cursorOnScreen.x - cx));
            } else if (a.sphericalDistanceTo(b) > 0.0000001d)
                ball.rotate(b.cross(a).normalize(), a.sphericalDistanceTo(b));
        }

        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        final UnitVector cursor = map(cursorOnScreen = e.getPoint());

        final SphericalObject previousHover = hover;
        hover = null;
        if (cursor != null) {
            for (SphericalObject item : sheet.getConstruction()) {
                if (!canProcess(item))
                    continue;
                else if (item instanceof AbstractPoint) {
                    if (item.distance(cursor) < 0.04d) {
                        hover = item;
                        break;
                    }
                } else if (item.distance(cursor) < 0.02D)
                    hover = item;
            }
        }
        if (hover != previousHover) {
            if (previousHover != null)
                previousHover.getAppearance().remove("hovered");
            if (hover != null)
                hover.getAppearance().put("hovered", true);
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK) // horizontal
            ball.rotate(ball.doInverseTransformation(new UnitVector(0, 1, 0)),
                    -0.01 * e.getWheelRotation());
        else
            // vertical
            ball.rotate(ball.doInverseTransformation(new UnitVector(1, 0, 0)),
                    0.01 * e.getWheelRotation());
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {

        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        final AffineTransform center = new AffineTransform(g2d.getTransform()), original = new AffineTransform(
                g2d.getTransform());
        center.translate(getWidth() / 2, getHeight() / 2);

        g2d.setTransform(new AffineTransform());
        if (projection.getMapShape() == null) {
            g2d.setColor(foreground);
            g2d.fill(g2d.getClipBounds());
        } else {
            g2d.setColor(background);
            g2d.fill(g2d.getClipBounds());
            final AffineTransform centro = new AffineTransform(center);
            centro.scale(ball.getScale(), ball.getScale());
            g2d.setColor(foreground);
            g2d.fill(centro.createTransformedShape(projection.getMapShape()));
        }
        g2d.setTransform(center);

        final Deque<PaintRecord> deck = new LinkedList<PaintRecord>();

        final SphericalObjectVisitor paintVisitor = projection
                .createVisitorPainter(deck);

        for (SphericalObject obj : sheet.getConstruction())
            obj.update();

        for (SphericalObject obj : order())
            obj.apply(paintVisitor);

        for (PaintRecord record : deck) {
            if (record.shape == null)
                continue;
            g2d.setPaint(record.paint);
            g2d.fill(record.shape);
        }

        g2d.setTransform(original);
        callback.repainted();

    }

    // returns ordered construction list
    private Collection<SphericalObject> order() {
        final List<SphericalObject> points = new LinkedList<SphericalObject>(), curves = new LinkedList<SphericalObject>(), polyga = new LinkedList<SphericalObject>();

        SphericalObjectVisitor visitor = new SphericalObjectVisitor() {

            @Override
            public void visit(Polygon poly) {
                polyga.add(poly);
            }

            @Override
            public void visit(AbstractPoint point) {
                points.add(point);
            }

            @Override
            public void visit(AbstractCurve curve) {
                curves.add(curve);
            }
        };

        for (SphericalObject obj : sheet.getConstruction())
            if (canProcess(obj))
                obj.apply(visitor);

        final List<SphericalObject> retur = new LinkedList<SphericalObject>();
        retur.addAll(polyga);
        retur.addAll(curves);
        retur.addAll(points);

        return retur;
    }

    /**
     * Returns the projection used by the display.
     *
     * @return projection used
     */
    ProjectionPainter getProjection() {
        return projection;
    }

    /**
     * Sets the projection used by the display. Throws IllegalArgumentException
     * on null param.
     *
     * @param p projection used. not null.
     */
    public void setProjection(ProjectionPainter p) {
        if (p == null)
            throw new IllegalArgumentException(
                    "null projection is not allowed.");
        projection = p;
    }
}
