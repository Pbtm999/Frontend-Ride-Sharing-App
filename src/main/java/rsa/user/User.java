package rsa.user;

import rsa.match.PreferredMatch;
import rsa.ride.RideRole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * A user of the Ride Sharing App. An instance of this class records the user's authentication and other relevant data.
 * @author Pedro Batista
 */
public class User implements Serializable {

    private String name;
    private final String nick;
    private final String key;
    private PreferredMatch preferredMatch;
    private final List<UserStars> driverStars;
    private final List<UserStars> passengerStars;
    private final HashMap<String, Car> userCars;

    /**
     * Creates a User instance. This is the only constructor and is package private.
     * Hence, users can only be instanced in this package, using the method {@link Users#register(String, String)}.
     * @param nick of user, must be unique.
     * @param name of user
     */
    User(String nick, String name) {
        this.nick = nick;
        this.name = name;
        this.key = generateKey();
        this.userCars = new HashMap<>();
        this.driverStars = new ArrayList<>();
        this.passengerStars = new ArrayList<>();
        this.preferredMatch = PreferredMatch.BETTER;
    }

    /**
     * Authenticates given key against the stored private key.
     * @param key to check
     * @return {@code true} is keys match; otherwise {@code false}
     */
    boolean authenticate(String key) { return this.key.equals(key); }

    /**
     * Get user authentication key
     * @return key of user
     */
    public String getKey() { return key; }

    /**
     * A key is generated to enable user authentication.
     * @return key for this user
     */
    String generateKey() {
        UUID uuid = UUID.nameUUIDFromBytes((nick+name).getBytes());
        return uuid.toString();
    }

    /**
     * The nick of this user: Cannot be changed as it a key.
     * @return nick
     */
    public String getNick() { return nick; }

    /**
     * Name of user
     * @return user's name
     */
    public String getName() { return name; }

    /**
     * Change user's name
     * @param name to change
     */
    public void setName(String name) { this.name = name; }

    /**
     * Bind a car to this user. Can be used to change car features.
     * @param car to add
     */
    public void addCar(Car car) { userCars.put(car.getPlate(), car); }

    /**
     * Car with given license plate
     * @param plate of car
     * @return car
     */
    public Car getCar(String plate)  { return userCars.get(plate); }

    /**
     * Remove binding between use and car
     * @param plate plate - of car to remove from this user
     */
    void deleteCar(String plate) { userCars.remove(plate); }

    /**
     * Add stars to user according to a role. The registered values are used to compute an average.
     * @param moreStars to add to this user
     * @param role in which stars are added
     */
    public void addStars(UserStars moreStars, RideRole role) {
        switch (role) {
            case DRIVER:
                driverStars.add(moreStars);
                break;
            case PASSENGER:
                passengerStars.add(moreStars);
                break;
        }
    }

    /**
     * Returns the average number of stars in given role
     * @param role of user
     * @return average number of stars
     */
    public float getAverage(RideRole role) {

        List<UserStars> stars;

        switch (role) {
            case DRIVER:
                stars = driverStars;
                break;
            case PASSENGER:
                stars = passengerStars;
                break;
            default:
                return 0;
        }

        if (stars.isEmpty()) return 0;

        int averageStars = 0;

        for (UserStars star : stars)
            averageStars += star.getStars();

        return (float) averageStars / stars.size();
    }

    /**
     * Current preference for sorting matches. Defaults to BETTER
     * @return preferred match by this user
     */
    public PreferredMatch getPreferredMatch() { return preferredMatch; }

    /**
     * Change preference for sorting matches
     * @param preferredMatch preferredMatch - to set for this user
     */
    public void setPreferredMatch(PreferredMatch preferredMatch) {
        if (preferredMatch != null)
            this.preferredMatch = preferredMatch;
    }
}
