package command.addComment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CommandInput;
import ticket.Ticket;
import user.User;

import java.util.List;

public interface AddCommentStrategy {
    boolean validate(final Ticket ticket, final User user,
                     final CommandInput commandInput, final List<ObjectNode> outputs);
}
