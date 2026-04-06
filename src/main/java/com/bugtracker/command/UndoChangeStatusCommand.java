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

import java.util.List;

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
     * Execute method that undoes the last changeStatus.
     * If ticket goes from RESOLVED -> IN_PROGRESS, solvedAt is reset to null
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
        if (ticket.getStatus().equals(Status.IN_PROGRESS)
                || ticket.getStatus().equals(Status.OPEN)) {
            return;
        }

        Status currentStatus = ticket.getStatus();
        Status prevStatus = null;

        switch (currentStatus) {

            case CLOSED -> prevStatus = Status.RESOLVED;
            case RESOLVED -> prevStatus = Status.IN_PROGRESS;
            default -> {
                return;
            }
        }

        ticket.setStatus(prevStatus);
        if (prevStatus.equals(Status.IN_PROGRESS)) {
            ticket.setSolvedAt(null);
        }


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
