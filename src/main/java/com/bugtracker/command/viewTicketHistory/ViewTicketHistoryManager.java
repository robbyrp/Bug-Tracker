package com.bugtracker.command.viewTicketHistory;

import com.bugtracker.BugTrackerSystem;
import com.bugtracker.milestone.Milestone;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

import java.util.ArrayList;
import java.util.List;

public final class ViewTicketHistoryManager implements ViewTicketHistoryStrategy {
    @Override
    public List<Ticket> getTickets(final BugTrackerSystem system, final User user) {
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
        }
        return managerTickets;
    }
}
