package com.bugtracker.command.viewTickets;

import com.bugtracker.BugTrackerSystem;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

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
