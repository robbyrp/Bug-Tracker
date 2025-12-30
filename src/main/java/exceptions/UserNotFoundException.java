package exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(final String username) {
        super("The user" + username + "does not exist");
    }
}
