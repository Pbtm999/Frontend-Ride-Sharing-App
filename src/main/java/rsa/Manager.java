package rsa;

import rsa.match.Location;
import rsa.match.Matcher;
import rsa.match.PreferredMatch;
import rsa.match.RideMatch;
import rsa.user.User;
import rsa.user.Users;
import rsa.user.UserStars;
import rsa.ride.RideRole;

import java.util.Set;

/**
 * An instance of this class is responsible for managing the ride-sharing service,
 * handling user requests and matching their rides. The methods of this class are
 * those needed by web client thus it follows the Façade design pattern. It also
 * follows the Singleton design pattern to provide a single instance of this
 * class to the application
 * @author Pedro Batista
 */
public class Manager {

    private static Manager instance;
    private static Users usersInstance;
    private static Matcher matcher;

    private Manager() throws RideSharingAppException {
        usersInstance = Users.getInstance();
        matcher = new Matcher();
    }

    /**
     * Returns the single instance of this class as proposed in the singleton design pattern.
     * @return instance of this class
     * @throws RideSharingAppException if I/O error occurs reading users serialization.
     */
    public static Manager getInstance() throws RideSharingAppException {
        if (instance == null)
            return new Manager();

        return instance;
    }

    /**
     * Resets singleton for unit testing purposes.
     */
    void reset() {
        usersInstance.reset();
        instance = null;
    }

    /**
     * Accept a match
     * @param rideId id of rid to match
     * @param matchId - id of match to consider
     */
    public void acceptMatch(long rideId, long matchId) { matcher.acceptMatch(rideId, matchId); }

    /**
     * Register a player with given nick and name. Changes are stored in serialization file
     * @param nick of user
     * @param name of user
     * @return {@code true} if registered and false otherwise
     * @throws RideSharingAppException if I/O error occurs when serializing data
     */
    public User register(String nick, String name) throws RideSharingAppException {
        return usersInstance.register(nick, name);
    }

    /**
     * Authenticates user, key
     * @param nick of the user
     * @param key uuid for authentication
     * @throws RideSharingAppException if authentication fails
     */
    public boolean authenticate(String nick, String key) throws RideSharingAppException {
        if (!usersInstance.authenticate(nick, key))
            throw new RideSharingAppException("Invalid user");

        return true;
    }

    /**
     * Current preferred match for given authenticated user
     * @param nick of user
     * @param key of user
     * @return the current preferred match for this user
     * @throws RideSharingAppException if authentication fails
     */
    public PreferredMatch getPreferredMatch(String nick, String key) throws RideSharingAppException {
        authenticate(nick, key);
        return usersInstance.getUser(nick).getPreferredMatch();
    }

    /**
     * Set preferred match for given authenticated user
     * @param nick of user
     * @param key of user
     * @param preferred kind of match
     * @throws RideSharingAppException if authentication fails
     */
    public void setPreferredMatch(String nick, String key, PreferredMatch preferred) throws RideSharingAppException {
        authenticate(nick, key);
        usersInstance.getUser(nick).setPreferredMatch(preferred);
    }

    /**
     * Add a ride for user with given nick, from and to the given locations. A car license plate must be given if user is the driver, or null if passenger.
     * @param nick of user
     * @param key of user
     * @param from origin's location
     * @param to destination's location
     * @param plate of car (null if passenger)
     * @param cost of the ride (how must you charge, if you are the driver)
     * @return id of created ride
     * @throws RideSharingAppException if authentication fails
     */
    public long addRide(String nick, String key, Location from, Location to, String plate, float cost) throws RideSharingAppException {
        authenticate(nick, key);
        return matcher.addRide(usersInstance.getUser(nick), from, to, plate, cost);
    }

    /**
     * Update current location of user and receive a set of proposed ride matches
     * @param rideId of ride to update
     * @param current location of user
     * @return A Set of RideMatch
     */
    public Set<RideMatch> updateRide(long rideId, Location current) {
        return matcher.updateRide(rideId, current);
    }

    /**
     * Conclude a ride and provide feedback on the other partner
     * @param rideId of the ride to conclude
     * @param classification of the ride partner (in starts)
     */
    public void concludeRide(long rideId, UserStars classification) {
        matcher.concludeRide(rideId, classification);
    }

    /**
     * The average number of stars of given user in given role
     * @param nick of user
     * @param role of interest
     * @return average stars on user in role
     * @throws RideSharingAppException in user's nick is not found
     */
    public double getAverage(String nick, RideRole role) throws RideSharingAppException {
        User user = usersInstance.getUser(nick);
        if (user == null)
            throw new RideSharingAppException("Invalid user");

        return user.getAverage(role);
    }

}
