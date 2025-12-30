package main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Command;
import command.ReportTicketCommand;
import fileio.CommandInput;
import fileio.OutputFormatter;
import lombok.Getter;
import ticket.TicketDatabase;
import user.User;
import user.UserDatabase;
import exceptions.UserNotFoundException;

import java.util.List;

public final class BugTrackerSystem {
    @Getter
    private UserDatabase userDatabase = new UserDatabase();
    @Getter
    private TicketDatabase ticketDatabase = new TicketDatabase();

    private OutputFormatter outputFormatter = new OutputFormatter();

    /**
     * Method that validates the command and calls the execute method() from the command pattern
     * @param inputs
     * @param outputs
     */
    public void executeCommands(final List<CommandInput> inputs, final List<ObjectNode> outputs) {
        // First, validate username
        for (CommandInput input : inputs) {
            try {
                if (!userDatabase.getUsers().containsKey(input.getUsername())) {
                    throw new UserNotFoundException("The user" + input.getUsername());
                }

                User activeUser = userDatabase.getUsers().get(input.getUsername());

                Command command = getCommandFromInput(input, activeUser);

                //TODO: ADD USER NO PERMISSION EXCEPTION HANDLERS BEFORE EXECUTE
                command.execute(this, outputs);

            } catch (UserNotFoundException e) {
                String errorMessage = e.getMessage();
                outputs.add(OutputFormatter.createError(input.getCommand(), input.getUsername(),
                        input.getTimestamp(), errorMessage));
            }
        }
    }

    private Command getCommandFromInput(final CommandInput input, final User user) {
        switch (input.getCommand()) {
            case "reportTicket":
                return new ReportTicketCommand(input, user);
//            case "viewTickets":
//                return new ViewTicketsCommand(input, user);

//            case "lostInvestors":
//                return new LostInvestorsCommand(input, user);

            default:
                throw new IllegalArgumentException("Unknown command " + input.getCommand());
        }
    }

}
