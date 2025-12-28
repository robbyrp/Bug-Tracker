package user.userFactory;

import fileio.UserInput;
import user.Developer;
import user.Manager;
import user.Reporter;
import user.User;

public final class UserFactory {

    public static User createUser(final UserInput input)
            throws IllegalArgumentException {
        return switch (input.getRole()) {
            case "REPORTER" -> new Reporter(input);
            case "MANAGER" -> new Manager(input);
            case "DEVELOPER" -> new Developer(input);
            default -> throw new IllegalArgumentException("Unknown role: " + input.getRole());
        };
    }
}
