package rsa.quad;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class follows the Facade design pattern and presents a single access point to manage quad trees.
 * It provides methods for inserting, deleting and finding elements implementing HasPoint.
 * This class corresponds to the Client in the Composite design pattern used in this package.
 * @param <T> a type extending HasPoint
 */
public class PointQuadtree<T extends HasPoint> implements Iterable<T> {
    /**
     * Root of this quad tree.
     */
    Trie<T> top;

    /**
     * Create a quad tree for points in a rectangle with given top left and bottom right corners.
     * @param topLeftX x coordinate of top left corner
     * @param topLeftY y coordinate of top left corner
     * @param bottomRightX x coordinate of bottom right corner
     * @param bottomRightY y coordinate of bottom right corner
     */
    public PointQuadtree(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        top = new NodeTrie<>(topLeftX, topLeftY, bottomRightX, bottomRightY);
    }

    /**
     * Find a recorded point with the same coordinates of given point
     * @param point with requested coordinates
     * @return recorded point, if found; null otherwise
     */
    public T find(T point) { return top.find(point); }

    /**
     * Insert given point in the QuadTree
     * @param point to be inserted
     */
    public void insert(T point) { top.insert(point); }

    /**
     * Insert point, replacing existing point in the same position
     * @param point point to be inserted
     */
    public void insertReplace(T point) { top.insertReplace(point); }

    /**
     * Returns a set of points at a distance smaller or equal to radius from point with given coordinates.
     * @param x coordinate of point
     * @param y coordinate of point
     * @param radius from given point
     * @return set of instances of type HasPoint
     */
    public Set<T> findNear(double x, double y, double radius) {
        Set<T> result = new HashSet<>();
        top.collectNear(x,y,radius,result);

        return result;
    }

    /**
     * Delete given point from QuadTree, if it exists there
     * @param point to be deleted
     */
    public void delete(T point) { top.delete(point); }

    /**
     * A set with all points in the QuadTree
     * @return set of instances of type HasPoint
     */
    public Set<T> getAll() {
        Set<T> result = new HashSet<>();
        top.collectAll(result);
        return result;
    }

    /**
     * Returns an iterator over the points stored in the quad tree
     * @return iterator in interface Iterable<T extends HasPoint>
     */
    public Iterator<T> iterator() {
        return getAll().iterator();
    }

}
