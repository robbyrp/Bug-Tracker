package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.Role;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import ticket.Comment;
import ticket.Ticket;
import user.User;

import java.util.List;
import java.util.ListIterator;

public final class UndoAddCommentCommand extends Command {
    public UndoAddCommentCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }
    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.REPORTER, Role.DEVELOPER);
    }

    /**
     * Execute method that iterates the list of comments from end to start.
     * Since the list is implement as an ArrayList, the insertion order is kept.
     * The last comments in the list are the most recent, and that's where
     * the deletion takes place
     * @param system The engine of the program
     * @param outputs output mapper
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        if (!checkBeforeDeleting(system, outputs)) {
            return;
        }

        Integer ticketId =  getCommandInput().getTicketID();
        Ticket ticket = system.getTicketDatabase().getTicketById(ticketId);

        List<Comment> comments = ticket.getComments();
        ListIterator<Comment> iterator = comments.listIterator(comments.size());

        while (iterator.hasPrevious()) {
            Comment currentComment = iterator.previous();
            if (currentComment.getAuthor().equals(getUser().getUsername())
                    && !currentComment.getContent().isEmpty()) {
                iterator.remove();
                break;
            }
        }
    }


    /**
     * Helper method that checks for a valid ticket. The ticket
     * must have a comment from the user calling the method and
     * the ticket can not be anonymous.
     * @param system
     * @param outputs
     * @return
     */
    private boolean checkBeforeDeleting(final BugTrackerSystem system,
                                        List<ObjectNode> outputs) {
        Integer ticketId =  getCommandInput().getTicketID();
        Ticket ticket = system.getTicketDatabase().getTicketById(ticketId);

        if (ticket == null) {
            return false;
        }

        if (ticket.getReportedBy().isEmpty()) {
            String error = "Comments are not allowed on anonymous tickets.";
            outputs.add(OutputFormatter.createError(
                    getCommandInput().getCommand(),
                    getCommandInput().getUsername(),
                    getCommandInput().getTimestamp(),
                    error
            ));
            return false;
        }

        boolean hasComment = false;
        for (Comment comment : ticket.getComments()) {
            if (comment.getAuthor().equals(getUser().getUsername())) {
                hasComment = true;
                break;
            }
        }

        if (!hasComment) {
            return false;
        }


        return true;

    }
}
