package rsa.quad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

/**
 * Trie with 4 sub tries with equal dimensions covering all its area. This class corresponds to the Container in the Composite design pattern.
 * @param <T>
 */
public class NodeTrie<T extends HasPoint> extends Trie<T> {
    final Map<Trie.Quadrant, Trie<T>> tries;

    /**
     * Create a node in given rectangle.
     * @param topLeftX of rectangle.
     * @param topLeftY of rectangle.
     * @param bottomRightX of rectangle.
     * @param bottomRightY of rectangle.
     */
    NodeTrie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        super(topLeftX, topLeftY, bottomRightX, bottomRightY);
        tries = new HashMap<Quadrant, Trie<T>>();
    }

    @Override
    /**
     * {@inheritDoc}
     * Find a recorded point with the same coordinates of given point
     * @param point with requested coordinates
     * @return recorded point, if found; null otherwise
     */
    T find(T point) {
        Trie.Quadrant quadrant = quadrantOf(point);
        Trie<T> child = tries.get(quadrant);

        if (child != null)
            return child.find(point);

        return null;
    }

    /**
     * Quadrant of a point in this node.
     * @param point - to compute quadrant.
     * @return quadrant.
     */
    Trie.Quadrant quadrantOf(T point) {
        double centerX = topLeftX + (bottomRightX-topLeftX) / 2;
        double centerY = bottomRightY + (topLeftY-bottomRightY) / 2;

        if (point.x() >= centerX && point.y() <= centerY) {
            return Trie.Quadrant.SE;
        } else if (point.x() <= centerX && point.y() >= centerY) {
            return Trie.Quadrant.NW;
        } else if (point.x() >= centerX && point.y() >= centerY) {
            return Trie.Quadrant.NE;
        }

        return Trie.Quadrant.SW;
    }

    /**
     * Get the child Trie for the given quadrant, creating it if necessary.
     * @param quadrant to get or create
     * @return the child Trie corresponding to the quadrant
     */
    Trie<T> getOrCreateChild(Trie.Quadrant quadrant) {

        Trie<T> child = tries.get(quadrant);

        if (child == null) {
            double midX = (topLeftX + bottomRightX) / 2;
            double midY = (topLeftY + bottomRightY) / 2;

            switch (quadrant) {
                case NE -> child = new LeafTrie<>(midX, topLeftY, bottomRightX, midY);
                case NW -> child = new LeafTrie<>(topLeftX, topLeftY, midX, midY);
                case SE -> child = new LeafTrie<>(midX, midY, bottomRightX, bottomRightY);
                case SW -> child = new LeafTrie<>(topLeftX, midY, midX, bottomRightY);
            }
            tries.put(quadrant, child);
        }

        return child;
    }

    @Override
    /**
     * {@inheritDoc}
     * Insert given point.
     * @param point to be inserted
     * @return changed parent node.
     */
    Trie<T> insert(T point) {
        Trie.Quadrant quadrant = quadrantOf(point);
        if (topLeftX > point.x() || topLeftY < point.y() || bottomRightX < point.x() || bottomRightY > point.y())
            throw new PointOutOfBoundException();

        Trie<T> child = getOrCreateChild(quadrant);

        return child.insert(point);
    }

    @Override
    /**
     * {@inheritDoc}
     * Insert given point, replacing existing points in same location
     * @param point point to be inserted
     * @return changed parent node.
     */
    Trie<T> insertReplace(T point) {

        Trie.Quadrant quadrant = quadrantOf(point);
        if (topLeftX > point.x() || topLeftY < point.y() || bottomRightX < point.x() || bottomRightY > point.y())
            throw new PointOutOfBoundException();

        Trie<T> child = getOrCreateChild(quadrant);

        return child.insertReplace(point);
    }

    @Override
    /**
     * {@inheritDoc}
     * Delete given point
     * @param point to delete
     */
    void delete(T point) {
        Trie.Quadrant quadrant = quadrantOf(point);

        Trie<T> child = tries.get(quadrant);

        if (child != null)
            child.delete(point);

    }

    @Override
    /**
     * {@inheritDoc}
     * Collect points at a distance smaller or equal to radius from (x,y) and place them in given list
     * @param x coordinate of point
     * @param y coordinate of point
     * @param radius from given point
     * @param nodes set for collecting points
     */
    void collectNear(double x, double y, double radius, Set<T> nodes) {

        for (Map.Entry<Trie.Quadrant, Trie<T>> entry : tries.entrySet()) {
            Trie<T> child = entry.getValue();

            if (child.overlaps(x, y, radius))
                child.collectNear(x, y, radius, nodes);

        }

    }

    @Override
    /**
     * {@inheritDoc}
     * Collect all points in this node and its descendants in given set
     * @param nodes set of HasPoint for collecting points
     */
    void collectAll(Set<T> nodes) {
        for (Trie<T> child : tries.values())
            child.collectAll(nodes);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return super.toString();
    }

    /**
     * Accept a visitor to operate on a node of the composite structure
     * @param visitor to the node
     */
    public void accept(Visitor<T> visitor) {
        visitor.visit((NodeTrie<T>) this);

        for (Trie<T> child : tries.values())
            child.accept(visitor);

    }

    /**
     * A collection of tries that descend from this one
     * @return collection tries
     */
    Collection<Trie<T>> getTries() { return tries.values(); }

}
