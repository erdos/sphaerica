package org.sphaerica.math;

import java.awt.geom.Point2D;

/**
 * A 3d vector class with double precision representation.
 */
public class Vector3D {

    protected double x, y, z;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3D() {
    }

    public Vector3D(double a, double b, double c) {
        setXYZ(a, b, c);
    }

    public Vector3D(Vector3D v) {
        setXYZ(v.x, v.y, v.z);
    }

    public static Vector3D create(double a, double b, double c) {
        return new Vector3D(a, b, c);
    }

    public static Vector3D create(Vector3D other) {
        return new Vector3D(other);
    }

    /**
     * Copies the coordinates of the parameter vector.
     *
     * @param other location to copy values from.
     */
    public final void copy(Vector3D other) {
        setXYZ(other.x, other.y, other.z);
    }

    void setXYZ(double a, double b, double c) {
        x = a;
        y = b;
        z = c;
    }

    /**
     * Calculates cross product with the given vector.
     *
     * @param other vector to calculate cross product with.
     * @return cross product of the two vectors.
     */
    public Vector3D cross(Vector3D other) {
        return new Vector3D(y * other.z - z * other.y, z * other.x - x
                * other.z, x * other.y - y * other.x);
    }

    /**
     * Creates immutable copy of this vector. The immutable copy is going to
     * throw a runtime exception when a setter method is called on it. If the
     * vector is already immutable then this method returns <b>this</b>.
     *
     * @return Immutable copy object.
     */
    Vector3D immutable() {
        return new ImmutableVector3D(this);
    }

    private final class ImmutableVector3D extends Vector3D {
        public ImmutableVector3D(Vector3D v) {
            super();
            super.setXYZ(v.x, v.y, v.z);
        }

        void setXYZ(double x, double y, double z) {
            throw new RuntimeException("this vector can not be changed");
        }

        Vector3D immutable() {
            return this;
        }

    }

    public static final Vector3D NORTH_POLE = new UnitVector(0, 0, 1)
            .immutable();
    public static final Vector3D SOUTH_POLE = new UnitVector(0, 0, -1)
            .immutable();

    public UnitVector normalize() {
        return new UnitVector(this);
    }

    public Vector3D normalizeThis() {
        double r = Math.sqrt(x * x + y * y + z * z);
        x /= r;
        y /= r;
        z /= r;
        return this;
    }

    public double dot(Vector3D other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public double dot() {
        return x * x + y * y + z * z;
    }

    public Vector3D rotateZ(double q) {
        setXYZ(x * Math.cos(q) - y * Math.sin(q),
                x * Math.sin(q) + y * Math.cos(q), z);
        return this;
    }

    public Vector3D rotateX(double q) {
        setXYZ(x, y * Math.cos(q) - z * Math.sin(q),
                y * Math.sin(q) + z * Math.cos(q));
        return this;
    }

    public Vector3D rotateY(double q) {
        setXYZ(z * Math.sin(q) + x * Math.cos(q), y,
                z * Math.cos(q) - x * Math.sin(q));
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Vector3D))
            return false;
        Vector3D other = (Vector3D) obj;
        return (x == other.x && y == other.y && z == other.z);
    }

    public Vector3D antipode() {
        setXYZ(-x, -y, -z);
        return this;
    }

    /**
     * Removes the third coordinate of the point.
     *
     * @return shadow of the point
     */
    public Point2D getShadow() {
        return new Point2D.Double(x, y);
    }

    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

    /**
     * Returns an array representation of the object. A new copy of array is
     * creates on each call.
     *
     * @return array containing coordinates
     */
    public double[] toArray() {
        return new double[]{x, y, z};
    }

    /**
     * Calculates the euclidean distance of this vector to an other.
     *
     * @param other other vector
     * @return euclidean distance of two points
     */
    public double distanceTo(Vector3D other) {
        final double dx = this.x - other.x, dy = this.y - other.y, dz = this.z
                - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Calculates the square of euclidean distance of this vector to an other.
     *
     * @param other other vector
     * @return square of euclidean distance of two points
     */
    public double distanceSquareTo(Vector3D other) {
        final double dx = this.x - other.x, dy = this.y - other.y, dz = this.z
                - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Scales the vector by the ratio given in parameter
     *
     * @param s scale factor
     * @return this
     */
    public Vector3D scaleBy(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        return this;
    }

    /**
     * Scales the vector by the reciprocal of given in parameter
     *
     * @param s scale factor reciprocal
     * @return this
     */
    public Vector3D inverseScaleBy(double s) {
        this.x /= s;
        this.y /= s;
        this.z /= s;
        return this;
    }

    /**
     * Adds the vector in parameter to this vector.
     *
     * @param other vector to add to this
     * @return this
     */
    public Vector3D add(Vector3D other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    /**
     * Subtracts the vector in parameter from this vector.
     *
     * @param other vector to subtract to this
     * @return this changed
     */
    public Vector3D subtract(Vector3D other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }
}
