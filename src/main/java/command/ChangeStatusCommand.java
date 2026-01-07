package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.Role;
import enums.Status;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import ticket.Ticket;
import user.User;

import java.time.LocalDate;
import java.util.List;

public final class ChangeStatusCommand extends Command {

    public ChangeStatusCommand(final CommandInput input, final User user) {
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

        String oldStatus = ticket.getStatus().name();
        switch(ticket.getStatus()) {
            case Status.OPEN -> ticket.setStatus(Status.IN_PROGRESS);
            case Status.IN_PROGRESS -> ticket.setStatus(Status.RESOLVED);
            case Status.RESOLVED, Status.CLOSED -> ticket.setStatus(Status.CLOSED);
        }

        String newStatus = ticket.getStatus().name();

        LocalDate currentDate = LocalDate.parse(getCommandInput().getTimestamp());
        if (ticket.getStatus().equals(Status.CLOSED)
        || ticket.getStatus().equals(Status.RESOLVED)) {
            ticket.setSolvedAt(currentDate);
        }

        ticket.addHistoryStatus("STATUS_CHANGED",
                oldStatus,
                newStatus,
                getCommandInput().getUsername(),
                getCommandInput().getTimestamp()
        );
    }

    /**
     * Helper method that checks if ticket is assigned to
     * the developer calling the method and is not Status CLOSED
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

        return !ticket.getStatus().equals(Status.CLOSED);
    }

}
