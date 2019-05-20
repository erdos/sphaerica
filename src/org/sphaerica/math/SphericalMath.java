package org.sphaerica.math;

/**
 * A general math class for spherical trigonometry.
 */
public class SphericalMath {

    /**
     * Converts a distance in euclidean space to a distance in spherical space
     *
     * @param d distance in euclidean space
     * @return distance in spherical space
     */
    public static double euclideanToSpherical(double d) {
        return 2.0 * Math.sin(d / 2);
    }

    /**
     * Converts a distance in spherical space to a distance in Euclidean space
     *
     * @param d distance in spherical space
     * @return distance in euclidean space
     */
    public static double sphericalToEuclidean(double d) {
        return 2.0 * Math.asin(d / 2);
    }

    public static double angle(UnitVector seg1, UnitVector center, UnitVector seg2) {
        if (seg1.equals(center) || seg2.equals(center))
            return 0;
        return seg1.cross(center).cross(center).normalize().sphericalDistanceTo(seg2.cross(center).cross(center).normalize());
    }

    /**
     * <pre>
     *  .
     *  |\
     *  | \
     * a|  \c
     *  |___\
     *    b </pre>
     *
     * @param a
     * @param b
     * @return <b>c</b>
     */
    public static double pythagorean(double a, double b) {
        return Math.acos(Math.cos(a) * Math.cos(b));
    }

    /**
     * Determines if the given three points lay in a counterclockwise order on the surface of the sphere.
     *
     * @param p1 first  point
     * @param p2 second point
     * @param p3 third  point
     * @return true iff point lay in counter clockwise order
     */
    public static boolean isCounterClockwise(Vector3D p1, Vector3D p2, Vector3D p3) {
        return p1.x * p2.y * p3.z + p2.x * p3.y * p1.z + p3.x * p1.y * p2.z - p3.x * p2.y * p1.z - p2.x * p1.y * p3.z - p1.x * p3.y * p2.z > 0;
    }

    /**
     * Please see {@link isCounterClockwise}
     */
    public static boolean isCCOrColinear(Vector3D p1, Vector3D p2, Vector3D p3) {
        // a harom vektor matrixanak determinansa > 0 ?
        return p1.x * p2.y * p3.z + p2.x * p3.y * p1.z + p3.x * p1.y * p2.z - p3.x * p2.y * p1.z - p2.x * p1.y * p3.z - p1.x * p3.y * p2.z >= 0;
    }

    /**
     * Checks if the given  vector lays inside the spherical triangle defined by the three points.
     *
     * @param vector
     * @param a
     * @param b
     * @param c
     * @return true iff vector lays inside triangle defined by the other three points.
     */
    public static boolean isInsideTriangle(UnitVector vector, UnitVector a, UnitVector b, UnitVector c) {
        boolean aa = SphericalMath.isCounterClockwise(vector, a, b);
        boolean bb = SphericalMath.isCounterClockwise(vector, b, c);
        boolean cc = SphericalMath.isCounterClockwise(vector, c, a);
        boolean fr = SphericalMath.isCounterClockwise(a, b, c);
        return (aa == bb) && (bb == cc) && (cc == fr);
    }

    /**
     * Calculates angular distance of two angles. That is their difference modulo PI
     *
     * @return angular difference.
     */
    public static double angularDistance(double angle1, double angle2) {
        double d = angle1 - angle2;
        while (d < -Math.PI) d += Math.PI * 2;
        while (d > Math.PI) d -= Math.PI * 2;
        return d;
    }

    /**
     * Calculates the spherical are of the triangle defined by the three points.
     *
     * @return spherical area of triangle in steradians.
     */
    public static double area(UnitVector a, UnitVector b, UnitVector c) {
        return Math.PI * 2 - angle(a, b, c) - angle(b, c, a) - angle(c, a, b) + Math.PI;
    }
}
