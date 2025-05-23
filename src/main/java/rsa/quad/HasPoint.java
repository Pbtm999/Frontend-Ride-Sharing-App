package rsa.quad;

/**
 * An object with x and y coordinates, each with its own getter.
 * @author Pedro Batista
 */
public interface HasPoint {
    /**
     * Point's X coordinate.
     * @return x coordinate.
     */
    double x();

    /**
     * Point's Y coordinate.
     * @return y coordinate.
     */
    double y();
}
