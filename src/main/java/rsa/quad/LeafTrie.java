package rsa.quad;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;

/**
 * A Trie that has no descendants.
 * @param <T>
 */
class LeafTrie<T extends HasPoint> extends Trie<T> {

    private final List<T> points;

    /**
     * Create a leaf in given rectangle.
     * @param topLeftX of rectangle.
     * @param topLeftY of rectangle.
     * @param bottomRightX of rectangle.
     * @param bottomRightY of rectangle.
     */
    LeafTrie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        super(topLeftX, topLeftY, bottomRightX, bottomRightY);
        points = new ArrayList<T>();
    }

    @Override
    /**
     * {@inheritDoc}
     * Find a recorded point with the same coordinates of given point
     * @param point with requested coordinates
     * @return recorded point, if found; null otherwise
     */
    T find(T point) {
        for (T p : points) {
            if (p.x() == point.x() && p.y() == point.y()) {
                return p;
            }
        }

        return null;
    }

    @Override
    /**
     * {@inheritDoc}
     * Insert given point
     * @param point to be inserted
     * @return changed parent node
     */
    Trie<T> insert(T point) { points.add(point); return this; }

    @Override
    /**
     * {@inheritDoc}
     * Insert given point, replacing existing points in same location
     * @param point point to be inserted
     * @return changed parent node
     */
    Trie<T> insertReplace(T point) {
        T existing = null;
        for (T p : points) {
            if (Double.compare(p.x(), point.x()) == 0 &&
                    Double.compare(p.y(), point.y()) == 0) {
                existing = p;
                break;
            }
        }

        if (existing != null) {
            points.remove(existing);
        }

        points.add(point);

        return this;
    }

    @Override
    /**
     * {@inheritDoc}
     * Delete given point
     * @param point to delete
     */
    void delete(T point) { points.remove(point); }

    @Override
    /**
     * {@inheritDoc}
     * @param x coordinate of point
     * @param y coordinate of point
     * @param radius from given point
     * @param nodes set for collecting points
     */
    void collectNear(double x, double y, double radius, Set<T> nodes) {
        for (T point : points) {
            double dx = point.x() - x;
            double dy = point.y() - y;

            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= radius) nodes.add(point);
        }
    }

    @Override
    /**
     * {@inheritDoc}
     * Collect all points in this node and its descendants in given set
     * @param nodes set of HasPoint for collecting points
     */
    void collectAll(Set<T> nodes) { nodes.addAll(points); }

    @Override
    public String toString() { return super.toString(); }

    @Override
    /**
     * Accept a visitor to operate on a node of the composite structure
     * @param visitor to the node
     */
    public void accept(Visitor<T> visitor) { visitor.visit((LeafTrie<T>) this); }

    /**
     * A collection of points currently in this leaf
     * @return collection of points
     */
    Collection<T> getPoints() { return points; }
}
