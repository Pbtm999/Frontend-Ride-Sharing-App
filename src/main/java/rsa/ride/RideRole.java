package rsa.ride;

import java.io.Serializable;

public enum RideRole implements Serializable {
    /**
     * This user is driving the car
     */
    DRIVER,

    /**
     * This user is the passenger
     */
    PASSENGER;

    /**
     * The other role: if driver then passenger, otherwise driver
     * @return other role
     */
    public RideRole other() {
        return this == DRIVER ? PASSENGER : DRIVER;
    }
}
