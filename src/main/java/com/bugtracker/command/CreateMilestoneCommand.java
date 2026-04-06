package com.bugtracker.command;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.enums.ApplicationPhase;
import com.bugtracker.enums.Role;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.OutputFormatter;
import com.bugtracker.BugTrackerSystem;
import com.bugtracker.milestone.Milestone;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

import java.util.List;

public final class CreateMilestoneCommand extends Command {

    public CreateMilestoneCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    /**
     * Execute method that creates new Milestone, adds it to the database,
     * adds the event in history and sends notification to assigned devs
     * @param system The engine of the program
     * @param outputs output mapper
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        List<Integer> requestedTickets = getCommandInput().getTickets();

        for (Integer ticketId : requestedTickets) {
            Milestone ticketOwner = system.getMilestoneDatabase().getMilestoneByTicketId(ticketId);

            if (ticketOwner == null) {
                continue;
            }

            String error = "Tickets " + ticketId
                    + " already assigned to milestone " + ticketOwner.getName() + ".";
            outputs.add(OutputFormatter.createError(
                    getCommandInput().getCommand(),
                    getCommandInput().getUsername(),
                    getCommandInput().getTimestamp(),
                    error
            ));
            return;
        }

        Milestone newMilestone = new Milestone(getCommandInput(), system.getMilestoneDatabase());

        for (Integer ticketId : newMilestone.getTickets()) {
            Ticket ticket = system.getTicketDatabase().getTicketById(ticketId);
            if (ticket == null) {
                throw new IllegalArgumentException("Ticket id " + ticketId + " is invalid.");
            }
            ticket.addHistoryMilestone("ADDED_TO_MILESTONE",
                    newMilestone.getName(),
                    newMilestone.getCreatedBy(),
                    getCommandInput().getTimestamp()
            );
        }
        system.getMilestoneDatabase().addMilestone(newMilestone);

        String notification = "New milestone " + newMilestone.getName()
                + " has been created with due date " + newMilestone.getDueDate() + ".";

        for (String devUsername : newMilestone.getAssignedDevs()) {
            User dev = system.getUserDatabase().getUsers().get(devUsername);
            if (dev != null) {
                dev.update(notification);
            }
        }

    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }
    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.MANAGER);
    }
}
