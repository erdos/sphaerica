package org.sphaerica.math;


/**
 * ArcBall class for handling rotation of the sphere.
 */
public class ArcBall {
    private final Quaternion quaternion = new Quaternion(0, 0, 0, 240);

    public void reset() {
        quaternion.setXYZW(0, 0, 0, 240);
    }

    public void rotate(UnitVector axis, double angle) {

        if (axis == null)
            throw new IllegalArgumentException("null axis is not allowed.");
        if (Double.isNaN(angle))
            throw new IllegalArgumentException("angle is not a number: "
                    + angle);

        angle *= -1;

        final double result = Math.sin(angle / 2.0);
        final Quaternion rot = new Quaternion(axis.getX() * result, axis.getY()
                * result, axis.getZ() * result, Math.cos(angle / 2.0));

        quaternion.multiplyBy(rot.normalize());
    }

    public double[] doRotation(double[] vector) {
        if (vector.length < 3)
            throw new IllegalArgumentException("too small array passed");

        Quaternion q1 = quaternion.clone(), q2 = quaternion.clone();
        q1.multiplyBy(new Quaternion(vector[0], vector[1], vector[2], 0))
                .multiplyBy(q2.conjugate());

        double r = Math.sqrt(q1.getX() * q1.getX() + q1.getY() * q1.getY()
                + q1.getZ() * q1.getZ());

        vector[0] = q1.getX() / r;
        vector[1] = q1.getY() / r;
        vector[2] = q1.getZ() / r;

        return vector;
    }

    public Vector3D doTransformation(final UnitVector vector) {
        if (vector == null)
            throw new RuntimeException("can not rotate null vector");
        Quaternion q1 = quaternion.clone(), q2 = quaternion.clone();
        q1.multiplyBy(
                new Quaternion(vector.getX(), vector.getY(), vector.getZ(), 0))
                .multiplyBy(q2.conjugate());

        return new Vector3D(q1.getX(), q1.getY(), q1.getZ());
    }

    /**
     * Returns size of the sphere.
     */
    public double getScale() {
        return Math.sqrt(quaternion.dot());
    }

    /**
     * Sets the size of the sphere.
     */
    public void setScale(double d) {
        quaternion.normalize().multiplyBy(new Quaternion(0, 0, 0, d));
    }

    // transforms the vector back
    public UnitVector doInverseTransformation(final Vector3D vector) {
        if (vector == null)
            throw new RuntimeException("can not rotate null vector");
        Quaternion q1 = quaternion.clone().invert(), q2 = quaternion.clone()
                .invert();
        q1.conjugate()
                .multiplyBy(
                        new Quaternion(vector.getX(), vector.getY(), vector
                                .getZ(), 0).normalize()).multiplyBy(q2);

        return new UnitVector(q1.getX(), q1.getY(), q1.getZ());
    }
}
