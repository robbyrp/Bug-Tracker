package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.Role;
import enums.Status;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import ticket.Ticket;
import ticket.TicketAction;
import user.User;

import java.util.List;
import java.util.ListIterator;

public final class UndoChangeStatusCommand extends Command {

    public UndoChangeStatusCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }
    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.DEVELOPER);
    }

    /**
     * Execute method that iterates the list of history list from end to start.
     * Since the list is implemented as an ArrayList, the insertion order is kept.
     * The last entries in the list are the most recent, and that's where
     * the deletion takes place
     * @param system The engine of the program
     * @param outputs output mapper
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        Integer ticketId =  getCommandInput().getTicketID();
        Ticket ticket = system.getTicketDatabase().getTicketById(ticketId);
        if (ticket == null) {
            return;
        }
        if (!checkTicket(system, outputs, ticket, getUser())) {
            return;
        }
        if (ticket.getStatus().equals(Status.IN_PROGRESS)) {
            return;
        }

        Status currentStatus = ticket.getStatus();
        Status prevStatus = null;

        switch(currentStatus) {
            case CLOSED -> prevStatus = Status.RESOLVED;
            case RESOLVED -> prevStatus = Status.IN_PROGRESS;
            default -> {
                return;
            }
        }

        ticket.setStatus(prevStatus);

        ticket.addHistoryStatus(
                "STATUS_CHANGED",
                currentStatus.name(),
                prevStatus.name(),
                getUser().getUsername(),
                getCommandInput().getTimestamp()
        );
    }

    /**
     * Helper method that checks if ticket is assigned to
     * the developer calling the method
     *
     * @param system
     * @param outputs
     * @param ticket
     * @param user
     * @return
     */
    public boolean checkTicket(final BugTrackerSystem system, final List<ObjectNode> outputs,
                               final Ticket ticket, final User user) {
        if (ticket.getAssignedTo() == null
                || !ticket.getAssignedTo().equals(user.getUsername())) {
            String error = "Ticket " + ticket.getId() + " is not assigned to developer "
                    + user.getUsername() + ".";
            outputs.add(OutputFormatter.createError(
                    getCommandInput().getCommand(),
                    getCommandInput().getUsername(),
                    getCommandInput().getTimestamp(),
                    error
            ));
            return false;
        }
        return true;
    }
}
