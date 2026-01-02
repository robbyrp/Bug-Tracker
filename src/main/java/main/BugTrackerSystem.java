package main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Command;
import command.LostInvestorsCommand;
import command.ReportTicketCommand;
import command.viewTickets.ViewTicketsCommand;
import enums.Role;
import exceptions.UserNoPermissionException;
import fileio.CommandInput;
import fileio.OutputFormatter;
import lombok.Getter;
import lombok.Setter;
import ticket.TicketDatabase;
import user.User;
import user.UserDatabase;
import exceptions.UserNotFoundException;

import java.util.List;

@Getter
public final class BugTrackerSystem {
    private UserDatabase userDatabase = new UserDatabase();
    private TicketDatabase ticketDatabase = new TicketDatabase();
    private OutputFormatter outputFormatter = new OutputFormatter();
    @Setter
    private boolean activeStatus = true;

    /**
     * Method that calls the execute method() from the command pattern
     * After validating the username and permissions
     * @param commandInputs
     * @param outputs
     */
    public void executeCommands(final List<CommandInput> commandInputs, final List<ObjectNode> outputs) {
        // First, validate username
        for (CommandInput input : commandInputs) {
            if (!activeStatus) {
                return;
            }

            User activeUser = validateUsername(input, outputs);
            if (activeUser == null) {
                continue;
            }

            Command command = getCommandFromInput(input, activeUser);
            boolean valid = validateUserPermission(input, command, activeUser, outputs);
            if (!valid) {
                continue;
            }

            command.execute(this, outputs);

        }
    }

    /**
     * Private method that validates usernames.
     * @param input
     * @param outputs
     * @return If valid, returns the User object associated with the username. Null otherwise.
     */
    private User validateUsername(final CommandInput input, List<ObjectNode> outputs) {
        String username = input.getUsername();
        if (!userDatabase.getUsers().containsKey(username)) {
            UserNotFoundException e = new UserNotFoundException(username);

            outputs.add(OutputFormatter.createError(
                    input.getCommand(),
                    username,
                    input.getTimestamp(),
                    e.getMessage()
            ));
            return null;
        }
        return userDatabase.getUsers().get(username);
    }

    /**
     * Private method that validates the user's permission of executing
     * the current command from the input.
     * @param input
     * @param user
     * @param outputs
     * @return True if the user is allowed, false otherwise
     */
    private boolean validateUserPermission(final CommandInput input, final Command command,
                                           final User user, List<ObjectNode> outputs) {
        List<Role> requiredRoles = command.getRequiredRoles();

        if (requiredRoles != null) {
            if (!requiredRoles.contains(user.getRole())) {
                UserNoPermissionException e = new
                        UserNoPermissionException(requiredRoles, user.getRole());

                outputs.add(OutputFormatter.createError(
                        input.getCommand(),
                        input.getUsername(),
                        input.getTimestamp(),
                        e.getMessage()
                ));

                return false;
            }
        }
        return true;
    }
    private Command getCommandFromInput(final CommandInput input, final User user) {
        switch (input.getCommand()) {
            case "reportTicket":
                return new ReportTicketCommand(input, user);
            case "viewTickets":
                return new ViewTicketsCommand(input, user);

            case "lostInvestors":
                return new LostInvestorsCommand(input, user);

            default:
                throw new IllegalArgumentException("Unknown command " + input.getCommand());
        }
    }

}
