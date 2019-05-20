package org.sphaerica.math;

/**
 * Class to do projection of points on the unit sphere to a plane et vice versa.
 */
public interface Projection {

    /**
     * Orthographic projection instance.
     */
    Projection ORTHOGRAPHIC = new Projection() {

        @Override
        public double[] onSphere(double[] plane) {
            plane[2] = Math.sqrt(1 - plane[0] * plane[0] - plane[1] * plane[1]);
            return plane;
        }

        @Override
        public double[] onPlane(double[] sphere) {
            return sphere;
        }
    };

    /**
     * Stereographic projection instance.
     */
    Projection STEREOGRAPHIC = new Projection() {

        @Override
        public double[] onSphere(double[] plane) {
            final double x = plane[0], y = plane[1];
            final double d = 1.0 + x * x + y * y;
            plane[0] = 2.0 * x / d;
            plane[1] = 2.0 * y / d;
            plane[2] = 1.0 - 2.0 / d;
            return plane;
        }

        @Override
        public double[] onPlane(double[] sphere) {
            sphere[0] /= 1.0 - sphere[2];
            sphere[1] /= 1.0 - sphere[2];
            sphere[2] = 0;
            return sphere;
        }
    };

    Projection GNOMONIC = new Projection() {

        @Override
        public double[] onSphere(double[] plane) {
            double x = plane[0], y = plane[1], z = 1.0 / Math.sqrt(x * x + y * y + 1.0);
            plane[0] *= z;
            plane[1] *= z;
            plane[2] = z;
            return plane;
        }

        @Override
        public double[] onPlane(double[] sphere) {
            sphere[0] /= sphere[2];
            sphere[1] /= sphere[2];
            return sphere;
        }

    };

    /**
     * This projection is not implemented (yet).
     */
    Projection LAMBERT_AZIMUTHAL = null, MERCATOR = null, EQUIRECTANGULAR = null;

    /**
     * <p>
     * Maps from plane coordinates to spherical coordinates.
     * </p>
     * <p>
     * Please note, that this method changes the values of the array parameter. The argument must have at least 3
     * elements, otherwise an {@link ArrayIndexOutOfBoundsException} excepting will be thrown.
     * </p>
     * <p>
     * If the numeric values passed are out of range, the result must contain a Double.NaN value.
     * </p>
     *
     * @param sphere The coordinates of a point on a plane.
     * @return The coordinates of a spherical point.
     * @throws ArrayIndexOutOfBoundsException if argument is smaller than 3 elems
     * @since Sphaerica version 0.9
     */
    double[] onSphere(double[] sphere);

    /**
     * Maps coordinates from sphere to plane.
     *
     * @param plane The coordinates of a point on the spher
     * @return Coordinates of the point projected to the plane. The third value will indicate if the point is on the
     * nearest side of the sphere with a non-negative value.
     */
    double[] onPlane(double[] plane);
}
