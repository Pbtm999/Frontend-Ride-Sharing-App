package rsa.user;

import rsa.RideSharingAppException;

import java.io.*;
import java.util.HashMap;

/**
 * A collection of players. Contains methods for registration, authentication and retrieving players and their names.
 * Nicks acts as keys and cannot be changed. They must be a single word (no white characters) of letters, digits and underscores, starting with a letter
 * Users data is serialized for persistence.
 * @author Pedro Batista
 */
public class Users implements Serializable {

    private static Users usersInstance;
    private static File usersFile;

    private final HashMap<String, User> users;

    private Users() { users = new HashMap<>(); }

    /**
     * Returns the single instance of this class as proposed in the singleton design pattern.
     * If a backup of this class is available then the users instance is recreated from that data
     * @return instance of this class
     * @throws RideSharingAppException if I/O error occurs reading serialization
     */
    public static Users getInstance() throws RideSharingAppException {

        if (usersInstance == null) {
            usersInstance = new Users();

            if (usersFile == null)
                usersFile = new File("default_file_users.ser");

            if (usersFile.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(usersFile))) {
                    usersInstance = (Users) ois.readObject();
                } catch (EOFException | ClassNotFoundException e) {
                    return usersInstance;
                } catch (IOException e) {
                    throw new RideSharingAppException("Error loading users from file", e);
                }
            }

        }


        return usersInstance;
    }

    /**
     * Resets singleton for unit testing purposes.
     */
    public void reset() {
        users.clear();

        if (usersFile != null && usersFile.exists())
            usersFile.delete();

    }

    /**
     * Authenticate user with her key
     * @param nick of user
     * @param key of user
     * @return {@code true} is key if valid; {@code false} otherwise
     */
    public boolean authenticate(String nick, String key) {
        User userToAuth = getUser(nick);
        return userToAuth != null && userToAuth.authenticate(key);
    }

    /**
     * Get the user with given nick
     * @param nick of player
     * @return player instance
     */
    public User getUser(String nick) { return users.get(nick); }

    /**
     * Get existing user with nick, or create one if needed. Useful for unit testing.
     * @param nick of user
     * @param name of user
     * @return user with given nick (and name)
     * @throws RideSharingAppException on serialization error.
     */
    public User getOrCreateUser(String nick, String name) throws RideSharingAppException {
        User user = getUser(nick);
        return user == null ? register(nick, name) : user;
    }

    /**
     * Change pathname of file containing users' data
     * @param file contain serialization
     */
    public static void setUsersFile(File file) {
        if (file == null) return;
        usersFile = file;
    }

    /**
     * Name of file containing users' data
     * @return file containing serialization
     */
    public static File getUsersFile() { return usersFile; }

    /**
     * Validates the nick provided
     * @param nick of user
     * @return {@code true} if user only contains digits and letters a don't exist already, else {@code false}
     */
    private boolean isNickValid(String nick) {
        if (nick == null || nick.isEmpty()) return false;

        for (char c: nick.toCharArray())
            if (!Character.isLetterOrDigit(c) && c != '_') return false;

        return !users.containsKey(nick);
    }

    /**
     * Register a player with given nick and name.
     * Changes are immediately serialized. Nicks can have letters (upper and lowercase) and digits but not other characters.
     * @param nick of user
     * @param name of user
     * @return user with given nick and name, or null if nick already exists or is invalid.
     * @throws RideSharingAppException on I/O error in serialization
     */
    public User register(String nick, String name) throws RideSharingAppException {
        if (!isNickValid(nick))
            return null;

        User user =  new User(nick, name);
        users.put(nick, user);

        if (usersFile != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(usersFile))) {
                oos.writeObject(usersInstance);
            } catch (IOException e) {
                throw new RideSharingAppException();
            }
        }

        return user;
    }

}
