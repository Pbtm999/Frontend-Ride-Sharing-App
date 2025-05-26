package rsa.ride;

import rsa.match.Location;
import rsa.match.PreferredMatch;
import rsa.match.RideMatch;
import rsa.quad.HasPoint;
import rsa.user.User;

import java.util.Comparator;

/**
 *  A user's (intention to) ride between two locations. The user can be either the driver of the passenger.
 *  There will be an attempt to match this ride with another of complementary type.
 *  That is driver's rides will be matched with passenger's rides and vice versa.
 * This class provides a comparator of ride matches adjusted to this ride.
 * Ride matches are sent to clients as RideMatch instances.
 * If more than one is available then they are sorted. The order depends on ride that is being matched.
 * To produce comparators this class is a concrete participant of the Factory Method design pattern.
 * It implements the RideMatchSorter interface and subclasses Comparator
 * @author Pedro Batista
 */
public class Ride implements HasPoint, RideMatchSorter {
    private User user;
    private String plate;
    private Location from;
    private Location to;
    private Location current;
    private float cost;
    private RideMatch match;
    private final long id;
    private static long nextId = 0;

    /**
     * Creates a ride from given arguments. Current location is initialized as the starting point (from).
     * @param user providing or requiring a ride
     * @param from origin location
     * @param to destination location
     * @param plate of then car (if null then it is a passenger)
     * @param cost of the ride (how must you charge, if you are the driver)
     */
    public Ride(User user, Location from, Location to, String plate, float cost) {
        this.user = user;
        this.plate = plate;
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.id = ++nextId;
        this.current = from;
    }

    /**
     * Generated unique identifier of this ride. Identifiers are non-negative integers.
     * @return this ride identifier
     */
    public long getId() {return id;}

    /**
     * User of this ride
     * @return the user
     */
    public User getUser() { return user; }

    /**
     * Change user of this ride
     * @param user to set
     */
    public void setUser(User user) { this.user = user; }

    /**
     * Car's registration plate for this ride
     * @return the plate null if passenger
     */
    public String getPlate() { return plate; }

    /**
     * Change car registration plate for this ride
     * @param plate of car to set (null if passenger)
     */
    public void setPlate(String plate) { this.plate = plate; }

    /**
     * Is the user the driver in this ride?
     * @return {@code true} if user is the driver; {@code false} otherwise
     */
    public boolean isDriver() { return plate != null; }

    /**
     * Role of user in ride, depending on a car's license plate being registered
     * @return RideRole depending on plate
     */
    public RideRole getRideRole() { return plate == null ? RideRole.PASSENGER : RideRole.DRIVER; }

    /**
     * Is the user the driver in this ride?
     * @return {@code true} if user is the passenger; {@code false} otherwise
     */
    public boolean isPassenger() { return plate == null; }

    /**
     * Cost of this ride (only meaningful for driver)
     * @return the cost
     */
    public float getCost() { return cost; }

    /**
     * Change cost of this ride (only meaningful for driver)
     * @param cost the cost to set
     */
    public void setCost(float cost) { this.cost = cost; }

    /**
     * Get the origin of this ride
     * @return the location from which this ride comes
     */
    public Location getFrom() { return from; }

    /**
     * Change the origin of this ride
     * @param from the location from which this ride will come
     */
    public void setFrom(Location from) { this.from = from; }

    /**
     * Get destination of this ride
     * @return the location to which this ride is going
     */
    public Location getTo() { return to; }

    /**
     * Change destination of this ride
     * @param to the location to which this ride will go
     */
    public void setTo(Location to) { this.to = to; }

    /**
     * Get current location of this ride
     * @return current location
     */
    public Location getCurrent() { return current; }

    /**
     * Change current location
     * @param current location to set
     */
    public void setCurrent(Location current) { this.current = current; }

    /**
     * Current match of this ride
     * @return the match
     */
    public RideMatch getMatch() { return this.match; }

    /**
     * Assign a match to this ride
     * @param match the match to set
     */
    public void setMatch(RideMatch match) { this.match = match; }

    /**
     * This ride was match with another
     * @return {@code true} is this ride is matched
     */
    public boolean isMatched() { return match != null; }


    /**
     * Point's X coordinate.
     * @return x coordinate.
     */
    @Override
    public double x() {
        return current.x();
    }

    /**
     * Point's Y coordinate.
     * @return y coordinate.
     */
    @Override
    public double y() {
        return current.y();
    }

    /**
     * Get a comparator of RideMatch instances for the given ride. Instances of RideMatchInfo are compared based on the preferences of the ride's user (PreferredMatch).
     * BETTER
     * the ride with the user in the other role with higher average stars is the smaller
     * CLOSER
     * the ride with the location of the other role closer to the current location of this ride is the smaller
     * CHEAPER
     * the ride with the cheapest cost is the smaller

     If the two matches have the same average/distance/cost then they are considered equal (returns 0).
     * @return a comparator of RideMatchInfo
     */
    @Override
    public Comparator<RideMatch> getComparator() {
        return (match1, match2) -> {
            User user = getUser();
            PreferredMatch preference = user.getPreferredMatch();
            RideRole otherRole = isPassenger() ? RideRole.DRIVER : RideRole.PASSENGER;

            switch (preference) {
                case BETTER:
                    double stars1 = match1.getStars(otherRole);
                    double stars2 = match2.getStars(otherRole);
                    return Double.compare(stars2, stars1);
                case CHEAPER:
                    double cost1 = match1.getCost();
                    double cost2 = match2.getCost();
                    return Double.compare(cost1, cost2);

                case CLOSER:
                    Location loc1 = match1.getWhere(otherRole);
                    Location loc2 = match2.getWhere(otherRole);
                    Location userLoc = match1.getWhere(getRideRole());

                    double dist1X = userLoc.x() - loc1.x();
                    double dist1Y = userLoc.y() - loc1.y();
                    double dist2X = userLoc.x() - loc2.x();
                    double dist2Y = userLoc.y() - loc2.y();

                    double dist1 = dist1X * dist1X + dist1Y * dist1Y;
                    double dist2 = dist2X * dist2X + dist2Y * dist2Y;

                    return Double.compare(dist1, dist2);

                default:
                    return 0;
            }
        };
    }
}

