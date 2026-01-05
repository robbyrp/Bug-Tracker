package command.viewTickets;

import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Command;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import ticket.Ticket;
import user.User;

import java.util.Comparator;
import java.util.List;

public class ViewTicketsCommand extends Command {

    private ViewTicketsStrategy strategy;

    public ViewTicketsCommand(final CommandInput input, final User user) {
        super(input, user);

        switch (user.getRole()) {

            case REPORTER:
                this.strategy = new ViewTicketsReporter();
                break;

            case DEVELOPER:
                this.strategy = new ViewTicketsDeveloper();
                break;

            case MANAGER:
                this.strategy = new ViewTicketsManager();
                break;

            default:
                break;
        }
    }

    /**
     * Execute method that uses an anonymous class to sort the list
     * @param system
     * @param outputs
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        List<Ticket> roleSpecificTickets = strategy.getTickets(system, getUser());

         roleSpecificTickets.sort(new Comparator<Ticket>() {
             @Override
             public int compare(final Ticket o1, final Ticket o2) {

                 int compareByDate =
                         o1.getReportedTimestamp().compareTo(o2.getReportedTimestamp());

                 if (compareByDate != 0) {
                     return compareByDate;
                 }
                 return Integer.compare(o1.getId(), o2.getId());
             }
         });

         outputs.add(OutputFormatter.createListResponse(
                 getCommandInput().getCommand(),
                 getUser().getUsername(),
                 getCommandInput().getTimestamp(),
                 "tickets",
                 roleSpecificTickets
         ));
    }
}
