package com.bugtracker.command.viewTicketHistory;

import com.bugtracker.BugTrackerSystem;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

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
