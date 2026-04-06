package com.bugtracker.command.addComment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

import java.util.List;

public interface AddCommentStrategy {
    /**
     * Boolean abstract method that is to be implemented in the concrete strategies
     * @param ticket
     * @param user
     * @param commandInput
     * @param outputs
     * @return
     */
    boolean validate(Ticket ticket, User user,
                     CommandInput commandInput, List<ObjectNode> outputs);
}
