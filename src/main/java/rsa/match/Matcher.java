package rsa.match;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Set;
import java.util.TreeSet;

import rsa.quad.PointQuadtree;
import rsa.ride.RideRole;
import rsa.user.User;
import rsa.ride.Ride;
import rsa.user.UserStars;


/**
 * A matcher of nearby driver and passenger rides. An instance of this class will match a pair of rides that:
 *     are not yet matched
 *     are currently in the same position (within a radius);
 *     have the same destination;
 *     have complementary roles (driver and passenger).
 * Matching occurs when rides positions are updated and use quad trees {rsa.quad} to locate matches currently in nearby locations.
 * @author Pedro Batista
 */
public class Matcher implements Serializable {

    private static Location topLeft;
    private static Location bottomRight;
    private static double radius;
    private final PointQuadtree<Ride> quadtree;
    private final Map<Long, Ride> rides;

    private final Map<Long, RideMatch> activeMatches;

    /**
     * Constructs a ride matcher.
     */
    public Matcher() {
        quadtree = new PointQuadtree<>(topLeft.x(), topLeft.y(), bottomRight.x(), bottomRight.y());
        rides = new HashMap<>();
        activeMatches = new HashMap<>();
    }

    /**
     * Location of top left corner of matching region
     * @return the topLeft
     */
    public static Location getTopLeft() { return topLeft; }

    /**
     * Change location of top left corner of matching region
     * @param topLeftParam the topLeft to set
     */
    public static void setTopLeft(Location topLeftParam) { topLeft = topLeftParam; }

    /**
     * Location of bottom right corner of matching region
     * @return the bottomRight
     */
    public static Location getBottomRight() { return bottomRight; }

    /**
     * Change location of bottom right corner of matching region
     * @param bottomRightParam the bottomRight to set
     */
    public static void setBottomRight(Location bottomRightParam) { bottomRight = bottomRightParam; }

    /**
     * Maximum distance between two users eligible for a match
     * @param radiusParam the radius
     */
    public static void setRadius(double radiusParam) { radius = radiusParam; }

    /**
     * Set distance to consider a match
     * @return the radius to set
     */
    public static double getRadius() { return radius; }

    /**
     * Add a ride to the matcher
     * @param user providing or requiring a ride
     * @param from origin location
     * @param to destination location
     * @param plate of then car (if null then it is a passenger)
     * @param cost of the ride (how must you charge, if you are the driver)
     * @return ride identifier
     */
    public long addRide(User user, Location from, Location to, String plate, float cost) {
        Ride newRide = new Ride(user, from, to, plate, cost);

        long id = newRide.getId();
        rides.put(id, newRide);
        quadtree.insert(newRide);

        return id;
    }

    /**
     * Update current location of ride with given id. If ride is not yet matched, returns a set RideMatch.
     * Proposed ride matches are currently near (use PointQuadtree) have different roles (one is a driver, the other a passenger)
     * and go almost to the same destination (differ by radius).
     * @param rideId of the ride to update
     * @param current location
     * @return set of RideMatch
     */
    public SortedSet<RideMatch> updateRide(long rideId, Location current) {

        Ride ride = rides.get(rideId);
        ride.setCurrent(current);

        if (ride.isMatched())
            return  null;

        quadtree.delete(ride);
        quadtree.insert(ride);

        Set<Ride> nearby = quadtree.findNear(ride.x(), ride.y(), radius);

        SortedSet<RideMatch> matches = new TreeSet<>(ride.getComparator());

        for (Ride other : nearby) {

            RideMatch match = new RideMatch(ride, other);

            if (!match.matchable())
                continue;

            matches.add(match);
            activeMatches.put(match.getId(), matches.first());
        }


        return matches;
    }

    /**
     * Accept the proposed match (identified by matchId) for given ride (identified by rideId)
     * @param rideId id of ride
     * @param matchId of match to accept
     */
    public void acceptMatch(long rideId, long matchId) {
        Ride ride = rides.get(rideId);
        RideMatch match = activeMatches.get(matchId);
        ride.setMatch(match);

        activeMatches.remove(matchId);
    }

    /**
     * Mark ride as concluded and classify other using stars
     * @param rideId of the ride to conclude
     * @param stars to assign to other user
     */
    public void concludeRide(long rideId, UserStars stars) {
        Ride ride = rides.get(rideId);

        RideRole otherRole = ride.getRideRole() == RideRole.DRIVER ? RideRole.PASSENGER : RideRole.DRIVER;
        RideMatch match = ride.getMatch();

        Ride other = match.getRide(otherRole);

        rides.remove(rideId);

        other.getUser().addStars(stars, other.getRideRole());
    }

}
