package command.search;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.BusinessPriority;
import enums.Seniority;
import enums.Status;
import enums.TicketType;
import enums.ExpertiseArea;
import fileio.CommandInput;
import fileio.FilterInput;
import fileio.OutputFormatter;
import fileio.SearchedTicketDTO;
import main.BugTrackerSystem;
import milestone.Milestone;
import ticket.Ticket;
import user.Developer;
import user.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class DeveloperSearchStrategy implements SearchStrategy {

    /**
     * Search method that applies filterss by filtersing the stream of tickets
     * with predicates
     * @param system
     * @param user
     * @param input
     * @return
     */
    @Override
    public void executeSearch(final BugTrackerSystem system, final List<ObjectNode> outputs,
                              final User user, final CommandInput input) {

        FilterInput filters = input.getFilters();
        List<Ticket> allTickets = system.getTicketDatabase().getTickets();
        Stream<Ticket> ticketStream = allTickets.stream();

        if (filters.getBusinessPriority() != null) {
            ticketStream = ticketStream.filter(ticket ->
                    ticket.getBusinessPriority().name().equals(filters.getBusinessPriority()));
        }

        if (filters.getType() != null) {
            ticketStream = ticketStream.filter(ticket ->
                    ticket.getType().name().equals(filters.getType()));
        }

        if (filters.getCreatedAt() != null) {
            LocalDate filtersCreatedAt = LocalDate.parse(filters.getCreatedAt());
            ticketStream = ticketStream.filter(ticket ->
                    ticket.getReportedTimestamp().equals(filtersCreatedAt));
        }

        if (filters.getCreatedBefore() != null) {
            LocalDate filtersCreatedBefore = LocalDate.parse(filters.getCreatedBefore());
            ticketStream = ticketStream.filter(ticket ->
                    ticket.getReportedTimestamp().isBefore(filtersCreatedBefore));
        }

        if (filters.getCreatedAfter() != null) {
            LocalDate filtersCreatedAfter = LocalDate.parse(filters.getCreatedAfter());
            ticketStream =
                    ticketStream.filter(ticket ->
                            ticket.getReportedTimestamp().isAfter(filtersCreatedAfter));
        }

        ticketStream =
                ticketStream.filter(ticket ->
                        isTicketAvailForAssignment(system, ticket, user));

        List<Ticket> sortedTickets = ticketStream
                .sorted(Comparator.comparing(Ticket::getReportedTimestamp).
                        thenComparing(Ticket::getId))
                .toList();

        List<SearchedTicketDTO> dtos = new ArrayList<>();
        for (Ticket ticket : sortedTickets) {
            SearchedTicketDTO dto = new SearchedTicketDTO(ticket);
            dtos.add(dto);
        }


        outputs.add(OutputFormatter.createSearchResponse(
                input.getCommand(),
                user.getUsername(),
                input.getTimestamp(),
                filters.getSearchType(),
                dtos
        ));
    }

    /**
     * Returns true if ticket is avail for assignment(checks the 5 rules at assignTicket)
     * and false otherwise
     * @param system
     * @param ticket
     * @param user
     * @return
     */
    private boolean isTicketAvailForAssignment(final BugTrackerSystem system,
                                               final Ticket ticket,
                                               final User user) {
        Developer developer = (Developer) user;

        Seniority seniority = developer.getSeniority();
        BusinessPriority businessPriority = ticket.getBusinessPriority();
        TicketType ticketType = ticket.getType();

        if (!seniority.hasAccessToPriority(businessPriority)) {
            return false;
        }
        if (!seniority.hasAccessToTicketType(ticketType)) {
            return false;
        }

        ExpertiseArea devExpertise = developer.getExpertiseArea();
        String ticketZone = ticket.getExpertiseArea().name();

        if (!devExpertise.hasAccessToZone(ticketZone)) {
            return false;
        }

        if (!ticket.getStatus().equals(Status.OPEN)) {
            return false;
        }

        Milestone ownerMilestone =
                system.getMilestoneDatabase().getMilestoneByTicketId(ticket.getId());
        if (ownerMilestone == null || ownerMilestone.getAssignedDevs() == null) {
            return false;
        }
        if (!ownerMilestone.getAssignedDevs().contains(developer.getUsername())) {
            return false;
        }
        if (ownerMilestone.isBlocked()) {
            return false;
        }

        return true;
    }
}
