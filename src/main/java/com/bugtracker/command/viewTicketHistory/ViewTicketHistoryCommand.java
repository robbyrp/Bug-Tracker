package com.bugtracker.command.viewTicketHistory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.command.Command;
import com.bugtracker.enums.ApplicationPhase;
import com.bugtracker.enums.Role;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.OutputFormatter;
import com.bugtracker.fileio.TicketHistoryDTO;
import com.bugtracker.BugTrackerSystem;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ViewTicketHistoryCommand extends Command {

    public ViewTicketHistoryCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }
    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.DEVELOPER, Role.MANAGER);
    }


    /**
     * Execute command that outputs the ticket history
     * The strategy gets the tickets for the specific role
     * @param system The engine of the program
     * @param outputs output mapper
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        ViewTicketHistoryStrategy strategy;
        switch (getUser().getRole()) {

            case DEVELOPER -> strategy = new ViewTicketHistoryDeveloper();
            case MANAGER -> strategy = new ViewTicketHistoryManager();
            default -> {
                return;
            }
        }

        List<Ticket> tickets = strategy.getTickets(system, getUser());

        tickets.sort(new Comparator<Ticket>() {
            @Override
            public int compare(final  Ticket o1, final Ticket o2) {
                int compareByDate = o1.getReportedTimestamp().compareTo(o2.getReportedTimestamp());
                if (compareByDate != 0) {
                    return compareByDate;
                }
                return Integer.compare(o1.getId(), o2.getId());
            }
        });

        List<TicketHistoryDTO> dto = new ArrayList<>();
        for (Ticket t : tickets) {
            dto.add(new TicketHistoryDTO(t));
        }

        outputs.add(OutputFormatter.createListResponse(
                getCommandInput().getCommand(),
                getUser().getUsername(),
                getCommandInput().getTimestamp(),
                "ticketHistory",
                dto
        ));
    }
}
