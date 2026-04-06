package com.bugtracker.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.enums.ApplicationPhase;
import com.bugtracker.enums.Role;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.OutputFormatter;
import com.bugtracker.BugTrackerSystem;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;
import com.bugtracker.fileio.AssignedTicketDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ViewAssignedTicketsCommand extends Command {

    public ViewAssignedTicketsCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.DEVELOPER);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }

    /**
     * Sorts the list of tickets assigned to the developer and
     * converts the list to a TicketListDTO for output formatting reasoning
     * @param system
     * @param outputs
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        List<Ticket> assignedToDev = new ArrayList<>();
        for (Ticket ticket : system.getTicketDatabase().getTickets()) {
            if (ticket.getAssignedTo().equals(getUser().getUsername())) {
                assignedToDev.add(ticket);
            }
        }

        assignedToDev.sort(new Comparator<Ticket>() {
            @Override
            public int compare(final Ticket o1, final Ticket o2) {
                int compareByPriority =
                        o2.getBusinessPriority().compareTo(o1.getBusinessPriority());
                if (compareByPriority != 0) {
                    return compareByPriority;
                }

                int compareByDate =
                        o1.getReportedTimestamp().compareTo(o2.getReportedTimestamp());
                if (compareByDate != 0) {
                    return compareByDate;
                }

                return Integer.compare(o1.getId(), o2.getId());

            }
        });

        List<AssignedTicketDTO> dto = new ArrayList<>();
        for (Ticket t : assignedToDev) {
            dto.add(new AssignedTicketDTO(t));
        }

        outputs.add(OutputFormatter.createListResponse(
                getCommandInput().getCommand(),
                getUser().getUsername(),
                getCommandInput().getTimestamp(),
                "assignedTickets",
                dto
        ));
    }
}
