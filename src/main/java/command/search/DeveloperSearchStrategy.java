package command.search;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.*;
import fileio.CommandInput;
import fileio.FilterInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import milestone.Milestone;
import ticket.Ticket;
import user.Developer;
import user.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class DeveloperSearchStrategy implements SearchStrategy {

    /**
     * Search method that applies filters by filtering the stream of tickets
     * with predicates
     * @param system
     * @param user
     * @param input
     * @return
     */
    @Override
    public void executeSearch(final BugTrackerSystem system, final List<ObjectNode> outputs,
                              final User user, final CommandInput input) {

        FilterInput filter = input.getFilters();
        List<Ticket> allTickets = system.getTicketDatabase().getTickets();
        Stream<Ticket> ticketStream = allTickets.stream();

        ticketStream = ticketStream.filter(ticket -> ticket.getStatus().equals(Status.OPEN))
                                    .filter(ticket -> isTicketInMilestoneAssignedToDev(system, ticket, user));

        if (filter.getBusinessPriority() != null) {
            ticketStream = ticketStream.filter(ticket ->
                    ticket.getBusinessPriority().name().equals(filter.getBusinessPriority()));
        }

        if (filter.getType() != null) {
            ticketStream = ticketStream.filter(ticket -> ticket.getType().name().equals(filter.getType()));
        }

        if (filter.getCreatedAt() != null) {
            LocalDate filterCreatedAt = LocalDate.parse(filter.getCreatedAt());
            ticketStream = ticketStream.filter(ticket -> ticket.getReportedTimestamp().equals(filterCreatedAt));
        }

        if (filter.getCreatedBefore() != null) {
            LocalDate filterCreatedBefore = LocalDate.parse(filter.getCreatedBefore());
            ticketStream = ticketStream.filter(ticket -> ticket.getReportedTimestamp().isBefore(filterCreatedBefore));
        }

        if (filter.getCreatedAfter() != null) {
            LocalDate filterCreatedAfter = LocalDate.parse(filter.getCreatedAfter());
            ticketStream = ticketStream.filter(ticket -> ticket.getReportedTimestamp().isAfter(filterCreatedAfter));
        }

        if (filter.isAvailableForAssignment()) {
            ticketStream = ticketStream.filter(ticket -> isTicketAvailForAssignment(system, ticket, user));
        }

        List<Ticket> results = ticketStream
                .sorted(Comparator.comparing(Ticket::getReportedTimestamp).thenComparing(Ticket::getId))
                .toList();

        outputs.add(OutputFormatter.createListResponse(
                input.getCommand(),
                user.getUsername(),
                input.getTimestamp(),
                "results",
                results
        ));

    }

    /**
     * Returns true if user is assigned to the ticket's milestone and false otherwise
     * @param system
     * @param ticket
     * @param user
     * @return
     */
    private boolean isTicketInMilestoneAssignedToDev(final BugTrackerSystem system, final Ticket ticket, final User user) {
        for (Milestone milestone : system.getMilestoneDatabase().getMilestoneList()) {
            if (milestone.getTickets().contains(ticket.getId())) {
                if (milestone.getAssignedDevs().contains(user.getUsername())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if ticket is avail for assignment(checks the 5 rules at assignTicket)
     * and false otherwise
     * @param system
     * @param ticket
     * @param user
     * @return
     */
    private boolean isTicketAvailForAssignment(final BugTrackerSystem system, final Ticket ticket, final User user) {
        Developer developer = (Developer) user;

        // Seniority check
        Seniority seniority = developer.getSeniority();
        BusinessPriority businessPriority = ticket.getBusinessPriority();
        TicketType ticketType = ticket.getType();

        if (!seniority.hasAccessToPriority(businessPriority)) {
            return false;
        }
        if (!seniority.hasAccessToTicketType(ticketType)) {
            return false;
        }

        // Expertise check
        ExpertiseArea devExpertise = developer.getExpertiseArea();
        String ticketZone = ticket.getExpertiseArea().name();
        if (!devExpertise.hasAccessToZone(ticketZone)) {
            return false;
        }

        // Ticket status check
        if (ticket.getStatus() != Status.OPEN) {
            return false;
        }

        // Milestone did not assign developer
        Milestone ownerMilestone = system.getMilestoneDatabase().getMilestoneByTicketId(ticket.getId());
        if (ownerMilestone == null || ownerMilestone.getAssignedDevs() == null ){
            return false;
        }
        if (!ownerMilestone.getAssignedDevs().contains(developer.getUsername())) {
            return false;
        }
        // Milestone is blocked
        if (ownerMilestone.isBlocked()) {
            return false;
        }

        return true;
    }
}
