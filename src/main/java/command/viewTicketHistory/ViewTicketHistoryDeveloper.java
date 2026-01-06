package command.viewTicketHistory;

import main.BugTrackerSystem;
import ticket.Ticket;
import ticket.TicketAction;
import user.User;

import java.util.ArrayList;
import java.util.List;

public class ViewTicketHistoryDeveloper implements ViewTicketHistoryStrategy {

    /**
     * A developer only sees the tickets that were assigned to him
     * in the past or in the current moment
     * @param system
     * @param user
     * @return
     */
    @Override
    public List<Ticket> getTickets(BugTrackerSystem system, User user) {
        List<Ticket> visibleTickets = new ArrayList<>();
        String currentUser = user.getUsername();

        for (Ticket ticket : system.getTicketDatabase().getTickets()) {
            boolean isCurrentlyAssigned = ticket.getAssignedTo().equals(currentUser);
            boolean wasAssignedInPast = false;

            if (!isCurrentlyAssigned) {
                for (TicketAction action : ticket.getHistory()) {
                    if (action.getAction().equals("ASSIGNED")
                            && action.getBy().equals(currentUser)) {
                        wasAssignedInPast = true;
                        break;
                    }
                }
            }

            if (isCurrentlyAssigned || wasAssignedInPast) {
                visibleTickets.add(ticket);
            }
        }
        return visibleTickets;
    }

}
