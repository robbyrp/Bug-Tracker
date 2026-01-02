package command.viewTickets;

import main.BugTrackerSystem;
import ticket.Ticket;
import user.User;

import java.util.List;

public class ViewTicketsManager implements ViewTicketsStrategy {

    /**
     * A manager can view all the tickets from the system
     * @param system
     * @param user
     * @return
     */
    @Override
    public List<Ticket> getTickets(final BugTrackerSystem system, final User user) {
        return system.getTicketDatabase().getTickets();
    }
}
