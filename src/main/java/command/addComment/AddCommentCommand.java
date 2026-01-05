package command.addComment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Command;
import enums.ApplicationPhase;
import enums.Role;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import ticket.Comment;
import ticket.Ticket;
import user.User;

import java.time.LocalDate;
import java.util.List;

public class AddCommentCommand extends Command {
    private AddCommentStrategy strategy;

    public AddCommentCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.DEVELOPER, Role.REPORTER);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }

    /**
     * Execute method that chooses the strategy, checks all possible
     * scenarios before validating and adding the comment to the ticket's list
     * @param system The engine of the program
     * @param outputs output mapper
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        Integer ticketId = getCommandInput().getTicketID();
        User user = getUser();
        Ticket ticket = system.getTicketDatabase().getTicketById(ticketId);

        if (ticket == null) {
            return;
        }

        if (!checkAnonTicket(system, outputs, ticket)) {
            return;
        }

        if (!checkMinLength(system, outputs)) {
            return;
        }

       switch (user.getRole()) {

           case DEVELOPER:
               strategy = new AddCommentDeveloper();
               break;

           case REPORTER:
               strategy = new AddCommentReporter();
               break;

           default:
               return;
       }

        if (!strategy.validate(ticket, user, getCommandInput(), outputs)) {
            return;
        }

        LocalDate commentDate = LocalDate.parse(getCommandInput().getTimestamp());

        Comment newComment = new Comment(
                getCommandInput().getUsername(),
                getCommandInput().getComment(),
                commentDate);
        ticket.getComments().add(newComment);
    }

    /**
     * Helper method that checks if ticket is anonymous
     * @param system
     * @param outputs
     * @param ticket
     * @return
     */
    private boolean checkAnonTicket(final BugTrackerSystem system, final List<ObjectNode> outputs,
                                 final Ticket ticket) {
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
        return true;
    }

    /**
     * Helper method that checks if comment length > 10 chars
     * @param system
     * @param outputs
     * @return
     */
    private boolean checkMinLength(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        String comment = getCommandInput().getComment();
        if (comment.length() <= 10) {
            String error = "Comment must be at least 10 characters long.";
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
