package org.vaadin.example;

import org.springframework.stereotype.Service;
import rsa.Manager;
import rsa.match.Location;
import rsa.match.Matcher;
import rsa.match.PreferredMatch;
import rsa.ride.RideRole;
import rsa.user.User;
import rsa.RideSharingAppException;

@Service
public class ManagerService {
    private final Manager manager;

    public ManagerService() throws RideSharingAppException {
        Matcher.setTopLeft(new Location(0,1000));
        Matcher.setBottomRight(new Location(1000,0));
        Matcher.setRadius(0.5);

        try {
            manager = Manager.getInstance();
        } catch (RideSharingAppException e) {
            throw new RideSharingAppException(e);
        }
    }

    public boolean authenticate(String nick, String key) {
        try {
            return manager.authenticate(nick, key);
        } catch (RideSharingAppException e) {
            return false;
        }

    }

    public User register(String nick, String name) throws RideSharingAppException {
        return manager.register(nick, name);
    }

    public PreferredMatch getPreferredMatch(String nick, String key) throws RideSharingAppException {
        return manager.getPreferredMatch(nick, key);
    }

    public void setPreferredMatch(String nick, String key, PreferredMatch preferred) {
        try {
            manager.setPreferredMatch(nick, key, preferred);
        } catch (RideSharingAppException e) {
            throw new RuntimeException(e);
        }

    }

    public double getAverage(String nick, RideRole role) {
        try {
            return manager.getAverage(nick, role);
        } catch (RideSharingAppException e) {
            return 0;
        }
    }
}
