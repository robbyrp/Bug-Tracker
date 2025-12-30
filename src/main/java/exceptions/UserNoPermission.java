package exceptions;

public class UserNoPermission extends RuntimeException {
    public UserNoPermission(final String message) {
        super(message);
    }
}
