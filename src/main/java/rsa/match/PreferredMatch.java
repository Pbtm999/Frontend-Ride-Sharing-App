package rsa.match;

/**
 * Preferred way to sort matches. Users will set their preferences using this values.
 * @author Pedro Batista
 */
public enum PreferredMatch {
    /**
     * Prefer to ride with better users (higher average stars; this is the default).
     */
    BETTER,

    /**
     * Prefer cheaper rides (if you are a passenger)
     */
    CHEAPER,

    /**
     * Prefer to ride with nearby users
     */
    CLOSER;

}
