package command.viewTickets;

import main.BugTrackerSystem;
import ticket.Ticket;
import user.User;

import java.util.List;

public interface ViewTicketsStrategy {

    /**
     * Returns the list of tickets specific to the role
     * @param system
     * @param user
     * @return
     */
    List<Ticket> getTickets(BugTrackerSystem system, User user);
}
