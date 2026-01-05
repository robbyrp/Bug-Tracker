package command.viewTickets;

import main.BugTrackerSystem;
import ticket.Ticket;
import user.User;

import java.util.ArrayList;
import java.util.List;

public class ViewTicketsReporter implements ViewTicketsStrategy {

    /**
     * A reporter can only view the tickets he submitted
     * @param system
     * @param user
     * @return
     */
    @Override
    public List<Ticket> getTickets(final BugTrackerSystem system, final User user) {
        List<Ticket> tickets =  system.getTicketDatabase().getTickets();
        List<Ticket> filteredTickets = new ArrayList<>();
        for (Ticket t : tickets) {
            if (user.getUsername().equals(t.getReportedBy())) {
                filteredTickets.add(t);
            }
        }
        return filteredTickets;
    }
}
