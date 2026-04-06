package com.bugtracker.command.addComment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.OutputFormatter;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

import java.util.List;

public class AddCommentDeveloper implements AddCommentStrategy {

    /**
     * Concrete strategy for developer: checks if the ticket is assigned to the dev
     * that is commenting
     * @param ticket
     * @param user
     * @param commandInput
     * @param outputs
     * @return
     */
    public boolean validate(final Ticket ticket, final User user,
                            final CommandInput commandInput, final List<ObjectNode> outputs) {
        if (!ticket.getAssignedTo().contains(user.getUsername())) {
            String error = "Ticket " + ticket.getId() + " is not assigned to the developer "
                    + user.getUsername() + ".";
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
