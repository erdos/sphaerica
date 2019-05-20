package org.sphaerica.display;

import org.sphaerica.math.ArcBall;
import org.sphaerica.math.Projection;
import org.sphaerica.worksheet.SphericalObjectVisitor;

import java.awt.*;
import java.util.Deque;

/**
 * This interface is used for the painting of projections of spherical objects.
 * The display panel creates a double ended queue that represents the layers of
 * the sphere. It calls createVisitorPainter to create a visitor that adds
 * layers to the deque for each spherical object it is applied to. Then The
 * layers are rendered in order.
 */
public interface ProjectionPainter {

    Projection getProjection();

    /**
     * Returns the arcball used by this projection for rotating the sphere. It
     * is advised to use the default arc ball provided by the sphere display
     * object.
     *
     * @return arc ball used for rotations
     */
    ArcBall getArcBall();

    /**
     * Creates a visitor that puts a projection shape to the deque for the
     * visited objects.
     *
     * @param deque layers of the sphere
     * @return visitor object
     */
    SphericalObjectVisitor createVisitorPainter(Deque<SphereDisplayPanel.PaintRecord> deque);

    /**
     * Returns the shape of the projection of the sphere. If the image of the
     * sphere is an infinite plane, this method returns null;
     *
     * @return shape of the image of sphere or null if infinite
     */
    Shape getMapShape();
}
