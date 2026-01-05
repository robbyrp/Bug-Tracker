package exceptions;

import enums.Role;

import java.util.List;
import java.util.stream.Collectors;

public class UserNoPermissionException extends RuntimeException {
    public UserNoPermissionException(final List<Role> requiredRoles, final Role userRole) {
        super(generateMessage(requiredRoles, userRole));
    }

    /**
     * Used functional programming (streams) to transform the enum list into
     * the format inquired by the task
     * @param requiredRoles
     * @param userRole
     * @return
     */
    private static String generateMessage(final List<Role> requiredRoles,
                                          final Role userRole) {
        String rolesString = requiredRoles.stream()
                .map(Role::toString)
                .collect(Collectors.joining(", "));

        return String.format(
                "The user does not have permission to execute "
                        + "this command: required role %s; user role %s.",
                rolesString,
                userRole.toString()
        );
    }
}
