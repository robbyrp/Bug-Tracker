package com.bugtracker.command.viewTickets;

import com.bugtracker.BugTrackerSystem;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

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
