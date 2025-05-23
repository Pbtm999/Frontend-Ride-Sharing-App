package rsa.user;

public enum UserStars {

    /** * Great ride */
    FIVE_STARS,

    /** * Good ride */
    FOUR_STARS,

    /** * Average ride */
    THREE_STARS,

    /** * Bad ride */
    TWO_STARS,

    /** * Lousy ride */
    ONE_STAR;

    /**
     * Get number of stars as an integer
     * @return number of stars
     */
    public int getStars() {
        return switch (this) {
            case FIVE_STARS -> 5;
            case FOUR_STARS -> 4;
            case THREE_STARS -> 3;
            case TWO_STARS -> 2;
            case ONE_STAR -> 1;
            default -> 0;
        };
    }
}
