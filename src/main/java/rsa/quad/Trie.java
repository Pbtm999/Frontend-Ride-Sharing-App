package rsa.quad;

import java.util.Set;

/**
 * Abstract class common to all classes implementing the trie structure. Defines methods required by those classes and provides general methods for checking overlaps and computing distances.
 * This class corresponds to the Component in the Composite design pattern.
 * @param <T> type that extends interface HasPoint
 * @author Pedro Batista
 */
public abstract class Trie<T extends HasPoint> implements Element<T> {

    /** Top left corner X coordinate. */
    protected final double topLeftX;

    /** Top left corner Y coordinate. */
    protected final double topLeftY;

    /** Bottom right corner X coordinate. */
    protected final double bottomRightX;

    /** Bottom right corner Y coordinate. */
    protected final double bottomRightY;

    /** Maximum number of points in a trie. */
    static int capacity;

    /**
     * Creates a Trie node bounded by top-left and bottom-right coordinates.
     *
     * @param topLeftX Top-left corner X coordinate.
     * @param topLeftY Top-left corner Y coordinate.
     * @param bottomRightX Bottom-right corner X coordinate.
     * @param bottomRightY Bottom-right corner Y coordinate.
     */
    protected Trie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.bottomRightX = bottomRightX;
        this.bottomRightY = bottomRightY;
    }

    /**
     * Get capacity of a bucket
     * @return capacity
     */
    public static int getCapacity() { return capacity; }

    /**
     * Set capacity of a bucket
     * @param capacity of bucket
     */
    public static void setCapacity(int capacity) { Trie.capacity = capacity; }

    /**
     * Find a recorded point with the same coordinates of given point
     * @param point with requested coordinates
     * @return recorded point, if found; null otherwise
     */
    abstract T find(T point);

    /**
     * Insert given point
     * @param point to be inserted
     * @return changed parent node
     */
    abstract Trie<T> insert(T point);

    /**
     * Insert given point, replacing existing points in same location
     * @param point point to be inserted
     * @return changed parent node
     */
    abstract Trie<T> insertReplace(T point);

    /**
     * Collect points at a distance smaller or equal to radius from (x,y) and place them in given list
     * @param x coordinate of point
     * @param y coordinate of point
     * @param radius from given point
     * @param points set for collecting points
     */
    abstract void collectNear(double x, double y, double radius, Set<T> points);

    /**
     * Collect all points in this node and its descendants in given set
     * @param points set of HasPoint for collecting points
     */
    abstract void collectAll(Set<T> points);

    /**
     * Delete given point
     * @param point to delete
     */
    abstract void delete(T point);

    /**
     * Check if overlaps with given circle
     * @param x coordinate of circle
     * @param y coordinate of circle
     * @param radius of circle
     * @return {@code true} if overlaps and {@code false} otherwise
     */
    boolean overlaps(double x, double y, double radius) {
        double closestX = Math.max(topLeftX, Math.min(x, bottomRightX));
        double closestY = Math.min(topLeftY, Math.max(y, bottomRightY));

        double dx = closestX - x;
        double dy = closestY - y;

        return dx * dx + dy * dy <= radius * radius;
    }

    /**
     * Euclidean distance between two pair of coordinates of two points
     * @param x1 x coordinate of first point
     * @param y1 y coordinate of first point
     * @param x2 x coordinate of second point
     * @param y2 y coordinate of second point
     * @return distance between given points
     */
    public static double getDistance(double x1, double y1, double x2, double y2) {
        double vectorX = x2 - x1;
        double vectorY = y2 - y1;

        return Math.sqrt(vectorX * vectorX + vectorY * vectorY);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "[" +  topLeftX + ", " + topLeftY + ", " + bottomRightX + ", " + bottomRightY + "]";
    }

    /**
     * Quadrants of NodeTries. Names are from the compass NE = North East, etc.
     */
    static enum Quadrant {
        /**
         * NorthEast, upper right corner.
         */
        NE,

        /**
         * NorthWest, upper left corner.
         */
        NW,

        /**
         * SouthEast, Bottom right corner.
         */
        SE,

        /**
         * SouthWest, Bottom left corner.
         */
        SW
    }
}
