package rsa.match;

import rsa.ride.Ride;
import rsa.ride.RideRole;
import rsa.user.Car;
import rsa.user.User;

/**
 * A match between 2 rides. Each has specific role, either as driver or as passenger and they must be different.
 * It is assumed that both rides have the same destination, although not checked in this class.
 * @author Pedro Batista
 */
public class RideMatch {

    private final long id;
    private final Ride left;
    private final Ride right;
    private static long nextId = 0;

    /**
     * Create a possible ride match for a pair of rides (rides have no particular order)
     * @param left ride
     * @param right ride
     */
    public RideMatch(Ride left, Ride right) {
        this.left = left;
        this.right = right;
        id = ++nextId;
    }

    /**
     * Generated unique identifier of this ride match.
     * @return this ride match identifier
     */
    public long getId() { return id; }

    /**
     * Ride of user with given role
     * @param role of user
     * @return the driver Ride
     */
    public Ride getRide(RideRole role) {
        if (left.getRideRole() == role)
            return left;

        return right;
    }

    public double getDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    /**
     * Are these rides matchable?
     *     Do they fill both roles (user and passenger)?
     *     Are they both unmatched?
     *     Are they currently in (roughly) the same place?
     *     Are they both going to (roughly) the same destination?
     * Locations are considered different if their distance exceeds radius Matcher.getRadius()
     * @return {@code true} if it's a match, {@code false} otherwise.
     */
    boolean matchable() {
        double radius = Matcher.getRadius();

        // Do they fill both roles (user and passenger)?
        if ((left.getRideRole() == RideRole.PASSENGER || left.getRideRole() == RideRole.DRIVER) &&
                right.getRideRole() == left.getRideRole())
            return false;

        // Are they both unmatched?
        if (left.isMatched() || right.isMatched())
            return false;

        // Are they currently in (roughly) the same place? already checked but its kind of double check
        if (getDistance(left.x(), left.y(), right.x(), right.y()) > radius * radius)
            return false;

        Location leftTo = left.getTo();
        Location rightTo = right.getTo();

        return (!(getDistance(leftTo.x(), leftTo.y(), rightTo.x(), rightTo.y()) > radius * radius));
    }

    /**
     * Retrieves the user associated with the given role in the match.
     * @param role of user in match
     * @return the User object corresponding to the given role
     */
    private User getUser(RideRole role) {
        return getRide(role).getUser();
    }

    /**
     * Get name of user with given role
     * @param role of user in match
     * @return name of user with given role
     */
    public String getName(RideRole role) {
        return getUser(role).getName();
    }

    /**
     * Get average number of stars of user with given role
     * @param role of user in match
     * @return stars average of user with given role
     */
    public float getStars(RideRole role) {
        return getUser(role).getAverage(role);
    }

    /**
     * The location of a user with given role
     * @param role of user in match
     * @return location of user with given role
     */
    public Location getWhere(RideRole role) {
        return getRide(role).getCurrent();
    }

    /**
     * Get car used in this ride
     * @return car used in ride
     */
    public Car getCar() {
        Ride driverRide = getRide(RideRole.DRIVER);
        User driverUser = driverRide.getUser();
        return driverUser.getCar(driverRide.getPlate());
    }

    /**
     * Cost of this ride, paid by the passenger to the driver
     * @return cost of this ride
     */
    public float getCost() { return left.getCost(); }
}
