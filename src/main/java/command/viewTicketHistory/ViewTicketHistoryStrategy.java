package command.viewTicketHistory;

import main.BugTrackerSystem;
import ticket.Ticket;
import user.User;

import java.util.List;

public interface ViewTicketHistoryStrategy {
    /**
     * Returns the tickets specific to the role
     * @param system
     * @param user
     * @return
     */
    List<Ticket> getTickets(BugTrackerSystem system, User user);

}
