package com.bugtracker.command.addComment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.enums.Status;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.OutputFormatter;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

import java.util.List;

public class AddCommentReporter implements AddCommentStrategy {

    /**
     * Concrete strategy for reporter. Checks if the ticket status is closed
     * and if the ticket is reported by the calling user
     * @param ticket
     * @param user
     * @param commandInput
     * @param outputs
     * @return
     */
    @Override
    public boolean validate(final Ticket ticket, final User user,
                     final CommandInput commandInput, final List<ObjectNode> outputs) {
        if (ticket.getStatus() == Status.CLOSED) {
            String error = "Reporters cannot comment on CLOSED tickets.";
            outputs.add(OutputFormatter.createError(
                    commandInput.getCommand(),
                    commandInput.getUsername(),
                    commandInput.getTimestamp(),
                    error
            ));
            return false;
        }

        if (!ticket.getReportedBy().equals(user.getUsername())) {
            String error = "Reporter " + user.getUsername() + " cannot comment on ticket "
                    + ticket.getId() + ".";
            outputs.add(OutputFormatter.createError(
                    commandInput.getCommand(),
                    commandInput.getUsername(),
                    commandInput.getTimestamp(),
                    error
            ));
            return false;
        }

        return true;
    }
}
