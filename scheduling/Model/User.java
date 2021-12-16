package scheduling.Model;

import javafx.util.StringConverter;

/**
 * User class representing data for a row in the users table
 * @author Jason Philpy
 */
public class User {
    public static User.UserStringConverter userStringConverter = new UserStringConverter();
    private int id;
    private final String username;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public static class UserStringConverter extends StringConverter<User> {
        @Override
        public String toString(User user) {
            return user.getUsername();
        }

        @Override
        public User fromString(String s) {
            return null;
        }
    }
}
