package org.sphaerica.math;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A vector of 3 coordinates laying on the unit sphere.
 */
public class UnitVector extends Vector3D {

    /**
     * Pattern used for parsing vector strings
     */
    private final static Pattern pattern = Pattern
            .compile("^\\(\\s*([+-]?(?:0|1|0\\.\\d+))\\s*,\\s*([+-]?(?:0|1|0\\.\\d+))\\s*,\\s*([+-]?(?:0|1|0\\.\\d+))\\s*\\)$");

    /**
     * String format used for pretty printing
     */
    private final static String format = "(%f, %f, %f)";

    /**
     * Copies vector.
     *
     * @param v other vector to copy.
     */
    public UnitVector(Vector3D v) {
        super(v);
    }

    /**
     * Creates an unit vector of spherical coordinates.
     *
     * @param azimuth     azimuth of unit vector
     * @param inclination inclination of unit vector
     */
    public UnitVector(double azimuth, double inclination) {
        super(Math.cos(azimuth) * Math.sin(inclination), Math.sin(azimuth)
                * Math.sin(inclination), Math.cos(inclination));
    }

    /**
     * Creates a vector of the given coordinates. Warning: this method does not
     * normalize coordinates given.
     *
     * @param a first coordinate
     * @param b second coordinate
     * @param c third coordinate
     */
    public UnitVector(double a, double b, double c) {
        super(a, b, c);
    }

    /**
     * Creates an unit vector with the given coordinates. Warning: this method
     * does not normalize the coordinates given.
     *
     * @param coords coordinates of the new vector
     */
    public UnitVector(double... coords) {
        super(coords[0], coords[1], coords[2]);
    }

    /**
     * Sets the coordinates for this vector and maps it to the unit sphere.
     */
    @Override
    public void setXYZ(double x, double y, double z) {
        if (x == 0 && y == 0 && z == 0)
            throw new IllegalArgumentException(
                    "can not set unit length null vector");

        double r = Math.sqrt(x * x + y * y + z * z);
        super.setXYZ(x / r, y / r, z / r);
    }

    /**
     * Calculates spherical distance to other unit vector.
     *
     * @param vec other vector to measure distance to
     * @return spherical distance in radians.
     */
    public double sphericalDistanceTo(UnitVector vec) {
        double dot = x * vec.x + y * vec.y + z * vec.z;
        if (dot > 1)
            return 0;
        if (dot < -1)
            return 0;
        return Math.acos(dot);
    }

    /**
     * @return Inclination of this spherical point.
     */
    public double getInclination() {
        return Math.acos(z);
    }

    /**
     * Azimuth of this spherical point.
     */
    public double getAzimuth() {
        return (Math.atan2(y, x) + 12.566370614359172D) % 6.283185307179586D;
    }

    /**
     * The overriding method returns this as an unit vector.
     */
    @Override
    public UnitVector rotateX(double x) {
        super.rotateX(x);
        return this;
    }

    /**
     * The overriding method returns this as an unit vector.
     */
    @Override
    public UnitVector rotateY(double x) {
        super.rotateY(x);
        return this;
    }

    /**
     * The overriding method returns this as an unit vector.
     */
    @Override
    public UnitVector rotateZ(double x) {
        super.rotateZ(x);
        return this;
    }

    /**
     * @return current unit vector unchanged. Does not check anomalies.
     */
    @Override
    public UnitVector normalize() {
        return this;
    }

    /**
     * @return current unit vector unchanged. Does not check anomalies.
     */
    @Override
    public UnitVector normalizeThis() {
        return this;
    }

    /**
     * Creates antipodal point on the surface of the sphere. Does not check for
     * anomalies.
     */
    @Override
    public UnitVector antipode() {
        return new UnitVector(-x, -y, -z);
    }

    /**
     * @return Human readable serialized string version of the vector.
     */
    public String encode() {
        return String.format(format, x, y, z);
    }

    /**
     * Deserializes human readable string to UnitVector instance.
     *
     * @param s serialized string
     * @return deserialized UnitVector instance.
     */
    public static UnitVector decode(String s) {
        if (s == null)
            throw new NullPointerException("can not decode null vector!");
        final Matcher m = pattern.matcher(s);
        if (!m.matches())
            throw new IllegalArgumentException("could not convert '" + s
                    + "' to vector");
        return new UnitVector(Double.valueOf(m.group(1)), Double.valueOf(m
                .group(2)), Double.valueOf(m.group(3)));
    }

    /**
     * This implementation throws a runtime exception.
     *
     * @return -
     */
    @Override
    public Vector3D scaleBy(double d) {
        throw new RuntimeException("Can not scale unit vector.");
    }

    /**
     * This implementation throws a runtime exception.
     *
     * @return -
     */
    @Override
    public Vector3D inverseScaleBy(double d) {
        throw new RuntimeException("Can not scale unit vector.");
    }


    /**
     * The current implementation throws a runtime exception
     *
     * @return -
     */
    @Override
    public Vector3D add(Vector3D other) {
        throw new RuntimeException("Can not add unit vectors.");
    }

    /**
     * The current implementation throws a runtime exception
     *
     * @return -
     */
    @Override
    public Vector3D subtract(Vector3D other) {
        throw new RuntimeException("Can not subtract unit vectors.");
    }
}
