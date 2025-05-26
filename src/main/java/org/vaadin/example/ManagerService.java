package org.vaadin.example;

import org.springframework.stereotype.Service;
import rsa.Manager;
import rsa.match.Location;
import rsa.match.Matcher;
import rsa.match.PreferredMatch;
import rsa.match.RideMatch;
import rsa.ride.RideRole;
import rsa.user.User;
import rsa.RideSharingAppException;
import rsa.user.UserStars;

import java.util.Set;

/**
 * Service class acting as a facade for the underlying Manager.
 * Handles user authentication, registration, rides, and preferences.
 */
@Service
public class ManagerService {

    private final Manager manager;

    /**
     * Initializes the Manager singleton and configures matching area.
     *
     * @throws RideSharingAppException if initialization fails
     */
    public ManagerService() throws RideSharingAppException {
        // Configure Matcher bounding box and radius
        Matcher.setTopLeft(new Location(-9.251777, 42.214865));
        Matcher.setBottomRight(new Location(-6.424838, 36.920869));
        Matcher.setRadius(1.0);

        // Initialize Manager instance (singleton)
        try {
            manager = Manager.getInstance();
        } catch (RideSharingAppException e) {
            throw new RideSharingAppException("Failed to initialize Manager", e);
        }
    }

    /**
     * Authenticates a user by nickname and key.
     *
     * @param nick user nickname
     * @param key  user authentication key
     * @return true if authentication succeeds, false otherwise
     */
    public boolean authenticate(String nick, String key) {
        try {
            return manager.authenticate(nick, key);
        } catch (RideSharingAppException e) {
            return false;
        }
    }

    /**
     * Registers a new user.
     *
     * @param nick user nickname
     * @param name user full name
     * @return the registered User object
     * @throws RideSharingAppException if registration fails
     */
    public User register(String nick, String name) throws RideSharingAppException {
        return manager.register(nick, name);
    }

    /**
     * Gets the preferred match criteria of a user.
     *
     * @param nick user nickname
     * @param key  user authentication key
     * @return user's preferred match setting
     * @throws RideSharingAppException if retrieval fails
     */
    public PreferredMatch getPreferredMatch(String nick, String key) throws RideSharingAppException {
        return manager.getPreferredMatch(nick, key);
    }

    /**
     * Sets the preferred match criteria for a user.
     *
     * @param nick      user nickname
     * @param key       user authentication key
     * @param preferred preferred match to set
     */
    public void setPreferredMatch(String nick, String key, PreferredMatch preferred) {
        try {
            manager.setPreferredMatch(nick, key, preferred);
        } catch (RideSharingAppException e) {
            throw new RuntimeException("Failed to set preferred match", e);
        }
    }

    /**
     * Retrieves the average rating of a user for a given role.
     *
     * @param nick user nickname
     * @param role role (DRIVER or PASSENGER)
     * @return average rating or 0 if unavailable
     */
    public double getAverage(String nick, RideRole role) {
        try {
            return manager.getAverage(nick, role);
        } catch (RideSharingAppException e) {
            return 0;
        }
    }

    /**
     * Adds a new ride for a user.
     *
     * @param nick  user nickname
     * @param key   user authentication key
     * @param from  starting location
     * @param to    destination location
     * @param plate car plate number
     * @param cost  ride cost
     * @return ride ID of the created ride
     * @throws RideSharingAppException if adding ride fails
     */
    public long addRide(String nick, String key, Location from, Location to, String plate, float cost) throws RideSharingAppException {
        return manager.addRide(nick, key, from, to, plate, cost);
    }

    /**
     * Updates the current location of a ride and returns possible matches.
     *
     * @param rideId  ride ID
     * @param current current location
     * @return set of ride matches
     */
    public Set<RideMatch> updateRide(long rideId, Location current) {
        return manager.updateRide(rideId, current);
    }

    /**
     * Accepts a match for a ride.
     *
     * @param rideId  ride ID
     * @param matchId match ID to accept
     */
    public void acceptMatch(long rideId, long matchId) {
        manager.acceptMatch(rideId, matchId);
    }

    /**
     * Concludes a ride with a user rating.
     *
     * @param rideId       ride ID
     * @param classification user rating and classification
     */
    public void concludeRide(long rideId, UserStars classification) {
        manager.concludeRide(rideId, classification);
    }
}