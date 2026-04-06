package com.bugtracker;
import com.bugtracker.fileio.UserInput;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.command.Command;
import com.bugtracker.command.factory.CommandFactory;
import com.bugtracker.enums.ApplicationPhase;
import com.bugtracker.enums.Role;
import com.bugtracker.exceptions.InvalidPhaseException;
import com.bugtracker.exceptions.UserNoPermissionException;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.OutputFormatter;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import com.bugtracker.milestone.MilestoneDatabase;
import com.bugtracker.milestone.MilestoneManager;
import com.bugtracker.ticket.TicketDatabase;
import com.bugtracker.user.User;
import com.bugtracker.user.UserDatabase;
import com.bugtracker.exceptions.UserNotFoundException;
import com.bugtracker.utils.DateManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter @Getter
@Service
public final class BugTrackerSystem {
    private final UserDatabase userDatabase = UserDatabase.getInstance();
    private final TicketDatabase ticketDatabase = TicketDatabase.getInstance();
    private final MilestoneDatabase milestoneDatabase = MilestoneDatabase.getInstance();
    private final MilestoneManager milestoneManager = MilestoneManager.getInstance();
    private final DateManager dateManager = DateManager.getInstance();

    private boolean activeStatus = true;

    /**
     * Method that executes a single Command. Used for testing REST API
     * @param commandInput
     * @return
     */
    public ObjectNode executeSingleCommand(CommandInput commandInput) {
        List<ObjectNode> outputs = new ArrayList<>();

        if (!activeStatus) {
            return null;
        }

        dateManager.updatePhase(commandInput.getTimestamp());

        User activeUser = validateUsername(commandInput, outputs);
        if (activeUser == null) {
            return outputs.get(0);
        }

        Command command = CommandFactory.getCommand(commandInput, activeUser);

        boolean valid = validateUserPermission(commandInput, command, activeUser, outputs);
        if (!valid) {
            return outputs.get(0);
        }

        if (!validatePhasePermission(commandInput, command, outputs)) {
            return outputs.get(0);
        }

        LocalDate currentDate = LocalDate.parse(commandInput.getTimestamp());

        milestoneManager.updateAllMilestones(milestoneDatabase,
                ticketDatabase,
                userDatabase,
                currentDate
        );

        command.execute(this, outputs);

        return outputs.isEmpty() ? null : outputs.get(0);
    }

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

            Command command = CommandFactory.getCommand(commandInput, activeUser);

            boolean valid = validateUserPermission(commandInput, command, activeUser, outputs);
            if (!valid) {
                continue;
            }

            if (!validatePhasePermission(commandInput, command, outputs)) {
                continue;
            }

            LocalDate currentDate = LocalDate.parse(commandInput.getTimestamp());

            milestoneManager.updateAllMilestones(milestoneDatabase,
                    ticketDatabase,
                    userDatabase,
                    currentDate
            );

            command.execute(this, outputs);

        }
    }

    @PostConstruct
    private void initializeUsers() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("users.json").getInputStream();
            ArrayList<UserInput> userInputs = mapper.readValue(
                    inputStream,
                    new TypeReference<ArrayList<UserInput>>() { }
            );
            userDatabase.initialize(userInputs);
            System.out.println("Loaded " + userInputs.size() + " users in the system");

        } catch (Exception e) {
            System.err.println("Error loading users " + e.getMessage());
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

    /**
     * Private method that validates the Phase of execution
     * @param input
     * @param command
     * @param outputs
     * @return
     */
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

}
