package com.bugtracker.user.userFactory;

import com.bugtracker.fileio.UserInput;
import com.bugtracker.user.Developer;
import com.bugtracker.user.Manager;
import com.bugtracker.user.Reporter;
import com.bugtracker.user.User;

public final class UserFactory {
    private UserFactory() { }

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
