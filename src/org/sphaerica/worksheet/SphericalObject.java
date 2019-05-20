package org.sphaerica.worksheet;

import org.sphaerica.math.UnitVector;

import javax.swing.event.ChangeListener;
import java.util.Map;

public interface SphericalObject {

    /**
     * An object is in real state iff it mathematically exists with the given parameters and conditions.
     * For example a line always exists, but an intersection of two circles exists only if the circles are
     * close enough. Similarly an object may not exist if its parent does not exist.
     *
     * @return true iff mathematical solution exists.
     */
    boolean isReal();

    /**
     * @return state of the valid flag.
     */
    boolean isValid();

    /**
     * Updates the object, sets the valid flag to true.
     */
    void update();// updates the object, sets the valid flag true;

    /**
     * @return Parent nodes of this object. Empty array if object is root node.
     */
    Iterable<SphericalObject> getParents();

    /**
     * @return Children nodes of this object. Returns empty iterable if no children are found.
     */
    Iterable<SphericalObject> getChildren();

    void registerChild(SphericalObject obj);

    void unregisterChild(SphericalObject obj);

    void addChangeListener(ChangeListener listener);

    void removeChangeListener(ChangeListener listener);

    /**
     * Visitor pattern helper function. Uses double dispatch to call method on object.
     *
     * @param visitor visitor object of the pattern.
     */
    void apply(SphericalObjectVisitor visitor);

    /**
     * This method returns the distance of the closest point of object to the cursor.
     * This is used mainly for intersection checking and user on-screen object selection.
     *
     * @param cursor point on the sphere
     * @return spherical distance from given point
     */
    double distance(UnitVector cursor);

    /**
     * The appearance map may contain custom properties for each object. Such properties include
     * styling information, like color and size, label text or other metadata.
     *
     * @return appearance map
     */
    Map<String, Object> getAppearance();
}