package command.viewTicketHistory;

import main.BugTrackerSystem;
import milestone.Milestone;
import ticket.Ticket;
import user.User;

import java.util.ArrayList;
import java.util.List;

public class ViewTicketHistoryManager implements ViewTicketHistoryStrategy {
    @Override
    public List<Ticket> getTickets(BugTrackerSystem system, User user) {
        List<Milestone> createdByManager = new ArrayList<>();
        for (Milestone milestone : system.getMilestoneDatabase().getMilestoneList()) {
            if (milestone.getCreatedBy().equals(user.getUsername())) {
                createdByManager.add(milestone);
            }
        }

        List<Ticket> managerTickets = new ArrayList<>();

        for (Milestone milestone : createdByManager) {
            for (Integer ticketId : milestone.getTickets()) {
                Ticket ticket = system.getTicketDatabase().getTicketById(ticketId);
                if (ticket != null) {
                    managerTickets.add(ticket);
                }
            }
            //TODO: Verifica, poate e nevoie sa iterezi si prin milestone.getClosed()
        }
        return managerTickets;
    }
}
