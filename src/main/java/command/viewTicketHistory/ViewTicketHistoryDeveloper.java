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
     * in the past or in the current moment. The list stops when the
     * dev is de-assigned
     * @param system
     * @param user
     * @return
     */
    @Override
    public List<Ticket> getTickets(final BugTrackerSystem system, final User user) {
        List<Ticket> visibleTickets = new ArrayList<>();
        String currentUser = user.getUsername();

        for (Ticket ticket : system.getTicketDatabase().getTickets()) {

            if (ticket.getAssignedTo().equals(currentUser)) {
                visibleTickets.add(new Ticket(ticket));
            } else {
                List<TicketAction> history = ticket.getHistory();
                int lastDeassignedIndex = -1;

                for (int i = 0; i < history.size(); i++) {
                    TicketAction action = history.get(i);

                    if (action.getAction().equals("DE-ASSIGNED")
                            && action.getBy().equals(currentUser)) {
                        lastDeassignedIndex = i;
                    }
                }

                if (lastDeassignedIndex != -1) {
                    Ticket ticketCopy = new Ticket(ticket);
                    List<TicketAction> truncatedHistory = new ArrayList<>();
                    for (int k = 0; k <= lastDeassignedIndex; k++) {
                        truncatedHistory.add(history.get(k));
                    }
                    ticketCopy.setHistory(truncatedHistory);
                    visibleTickets.add(ticketCopy);
                }

            }
        }
        return visibleTickets;
    }

}
