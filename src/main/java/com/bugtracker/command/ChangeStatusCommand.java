package com.bugtracker.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.enums.ApplicationPhase;
import com.bugtracker.enums.Role;
import com.bugtracker.enums.Status;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.OutputFormatter;
import com.bugtracker.BugTrackerSystem;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

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
        switch (ticket.getStatus()) {

            case OPEN -> ticket.setStatus(Status.IN_PROGRESS);
            case IN_PROGRESS -> ticket.setStatus(Status.RESOLVED);
            case RESOLVED, CLOSED -> ticket.setStatus(Status.CLOSED);
            default -> {
                return;
            }
        }

        String newStatus = ticket.getStatus().name();

        LocalDate currentDate = LocalDate.parse(getCommandInput().getTimestamp());

        if (ticket.getStatus().equals(Status.RESOLVED)
                || ticket.getStatus().equals(Status.CLOSED)) {
            ticket.setSolvedAt(currentDate);
        } else if (ticket.getStatus().equals(Status.IN_PROGRESS)
                || ticket.getStatus().equals(Status.OPEN)) {
            ticket.setSolvedAt(null);
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
