package main;
import com.fasterxml.jackson.databind.node.ObjectNode;
import command.*;
import command.addComment.AddCommentCommand;
import command.viewMilestones.ViewMilestonesCommand;
import command.viewTickets.ViewTicketsCommand;
import enums.ApplicationPhase;
import enums.Role;
import exceptions.InvalidPhaseException;
import exceptions.UserNoPermissionException;
import fileio.CommandInput;
import fileio.OutputFormatter;
import lombok.Getter;
import lombok.Setter;
import milestone.Milestone;
import milestone.MilestoneDatabase;
import ticket.TicketDatabase;
import user.User;
import user.UserDatabase;
import exceptions.UserNotFoundException;
import utils.DateManager;

import java.time.LocalDate;
import java.util.List;

@Getter
public final class BugTrackerSystem {
    private UserDatabase userDatabase = new UserDatabase();
    private TicketDatabase ticketDatabase = new TicketDatabase();
    private MilestoneDatabase milestoneDatabase = new MilestoneDatabase();
    private DateManager dateManager = new DateManager();

    @Setter
    private boolean activeStatus = true;

    /**
     * Method that calls the execute method() from the command pattern
     * After validating the username and permissions
     * @param commandInputs
     * @param outputs
     */
    public void executeCommands(final List<CommandInput> commandInputs,
                                final List<ObjectNode> outputs) {
        for (CommandInput commandInput : commandInputs) {
            if (!activeStatus) {
                return;
            }

            dateManager.updatePhase(commandInput.getTimestamp());

            User activeUser = validateUsername(commandInput, outputs);
            if (activeUser == null) {
                continue;
            }

            Command command = getCommandFromInput(commandInput, activeUser);
            boolean valid = validateUserPermission(commandInput, command, activeUser, outputs);
            if (!valid) {
                continue;
            }

            if (!validatePhasePermission(commandInput, command, outputs)) {
                continue;
            }

            LocalDate currentDate = LocalDate.parse(commandInput.getTimestamp());
            for (Milestone milestone : milestoneDatabase.getMilestoneList()) {
                milestone.updateMilestone(currentDate, ticketDatabase, milestoneDatabase);
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
    private User validateUsername(final CommandInput input,
                                  final List<ObjectNode> outputs) {
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
                                           final User user, final List<ObjectNode> outputs) {
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

    private boolean validatePhasePermission(final CommandInput input, final Command command,
                                            final List<ObjectNode> outputs) {
        ApplicationPhase requiredPhase = command.getRequiredPhase();
        if (requiredPhase == null) {
            return true;
        }

        ApplicationPhase currentPhase = dateManager.getCurrentPhase();

        if (requiredPhase.equals(currentPhase)) {
            return true;
        }

        InvalidPhaseException e = new InvalidPhaseException(input.getCommand(), currentPhase);

        outputs.add(OutputFormatter.createError(
                input.getCommand(),
                input.getUsername(),
                input.getTimestamp(),
                e.getMessage()
        ));
        return false;
    }
    private Command getCommandFromInput(final CommandInput input, final User user) {
        return switch (input.getCommand()) {
            case "reportTicket" -> new ReportTicketCommand(input, user);
            case "viewTickets" -> new ViewTicketsCommand(input, user);
            case "createMilestone" -> new CreateMilestoneCommand(input, user);
            case "viewMilestones" -> new ViewMilestonesCommand(input, user);
            case "assignTicket" -> new AssignTicketCommand(input, user);
            case "undoAssignTicket" -> new UndoAssignTicketCommand(input, user);
            case "viewAssignedTickets" -> new ViewAssignedTicketsCommand(input, user);
            case "addComment" -> new AddCommentCommand(input, user);
            case "undoAddComment" -> new UndoAddCommentCommand(input, user);

            case "lostInvestors" -> new LostInvestorsCommand(input, user);
            default -> throw new IllegalArgumentException("Unknown command " + input.getCommand());
        };
    }

}
