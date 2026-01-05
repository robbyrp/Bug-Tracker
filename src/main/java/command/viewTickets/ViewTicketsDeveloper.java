package command.viewTickets;

import enums.Status;
import main.BugTrackerSystem;
import milestone.Milestone;
import ticket.Ticket;
import user.User;

import java.util.ArrayList;
import java.util.List;

public final class ViewTicketsDeveloper implements ViewTicketsStrategy {
    /**
     * A developer sees the tickets with OPEN status from milestones
     * where he is assigned
     *
     * @param system
     * @param user
     * @return
     */
    @Override
    public List<Ticket> getTickets(final BugTrackerSystem system, final User user) {
        List<Ticket> devTickets = new ArrayList<>();
        for (Milestone milestone : system.getMilestoneDatabase().getMilestoneList()) {
            if (!milestone.getAssignedDevs().contains(user.getUsername())) {
                continue;
            }
            for (Integer ticketId : milestone.getTickets()) {
                Ticket ticket = system.getTicketDatabase().getTicketById(ticketId);
                if (ticket.getStatus().equals(Status.OPEN)) {
                    devTickets.add(ticket);
                }
            }
        }
        return devTickets;
    }
}
