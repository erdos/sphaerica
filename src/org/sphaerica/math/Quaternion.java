package org.sphaerica.math;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Quaternion implements Cloneable {

    private double x, y, z, w;

    /**
     * Creates an identity quaternion. that is [0,0,0,1] by definition.
     */
    public Quaternion() {
        setXYZW(0, 0, 0, 1);
    }

    /**
     * Creates a quaternion with the given coordinates.
     */
    public Quaternion(double a, double b, double c, double d) {
        setXYZW(a, b, c, d);
    }

    /**
     * Makes a copy of the given quaternion.
     *
     * @param q The quaternion to copy.
     */
    public Quaternion(Quaternion q) {
        setXYZW(q.x, q.y, q.z, q.w);
    }

    /**
     * Multiplies the current quaternion by the one in the parameter. Returns this.
     *
     * @param q quaternion to muliply by.
     * @return the multiplied quaternion.
     */
    public Quaternion multiplyBy(Quaternion q) {
        double xx = w * q.x + x * q.w + y * q.z - z * q.y;
        double yy = w * q.y + y * q.w + z * q.x - x * q.z;
        double zz = w * q.z + z * q.w + x * q.y - y * q.x;
        double ww = w * q.w - x * q.x - y * q.y - z * q.z;
        setXYZW(xx, yy, zz, ww);
        return this;
    }

    /**
     * Divides the current quaternion by the one in the paramteter.
     * Same as this.invert().multiplyBy(q) by definition.
     * Returns this with state modified.
     *
     * @param q quaternion to divide by.
     * @return this
     */
    public Quaternion divideBy(Quaternion q) {
        return invert().multiplyBy(q);
    }

    /**
     * Normalizes the quaternion by scaling it to unit length. Returns this.
     *
     * @return this
     */
    public Quaternion normalize() {
        final double scale = Math.sqrt(dot());
        setXYZW(x / scale, y / scale, z / scale, w / scale);
        return this;
    }

    /**
     * Inverts quaternion. Flips all coordinates.
     *
     * @return Current quaternion with coordinates flipped.
     */
    public Quaternion invert() {
        setXYZW(-x, -y, -z, -w);
        return this;
    }

    /**
     * Conjugates quaternion. That is the first three coordinates flipped.
     *
     * @return Current quaternion with first three coordinates flipped.
     */
    public Quaternion conjugate() {
        setXYZW(-x, -y, -z, w);
        return this;
    }

    /**
     * Sets coordinates of the quaternion.
     *
     * @param a new first coordinate
     * @param b new second coordinate
     * @param c new third coordinate
     * @param d new fourth coordinate
     */
    public void setXYZW(double a, double b, double c, double d) {
        x = a;
        y = b;
        z = c;
        w = d;
    }

    /**
     * Calculates dot product of the coordinates. That is x*x+y*y+z*z+w*w by definition.
     *
     * @return Dot product of the coordinates.
     */
    public double dot() {
        return x * x + y * y + z * z + w * w;
    }

    /**
     * Clones the current quaternion.
     *
     * @return a clone of the object.
     */
    public Quaternion clone() {
        try {
            return (Quaternion) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return first coordinate of the quaternion.
     */
    public double getX() {
        return x;
    }

    /**
     * @return second coordinate of the quaternion
     */
    public double getY() {
        return y;
    }

    /**
     * @return third coordinate of the quaternion
     */
    public double getZ() {
        return z;
    }

    /**
     * @return fourth coordinate of the quaternion
     */
    public double getW() {
        return w;
    }
}
