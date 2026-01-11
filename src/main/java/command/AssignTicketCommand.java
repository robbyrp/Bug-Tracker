package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.ExpertiseArea;
import enums.Role;
import enums.Status;
import enums.Seniority;
import enums.BusinessPriority;
import enums.TicketType;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import milestone.Milestone;
import ticket.Ticket;
import user.Developer;
import user.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AssignTicketCommand extends Command {
    public AssignTicketCommand(final CommandInput input, final User user) {
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

    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        Integer ticketId = getCommandInput().getTicketID();
        Ticket ticket = system.getTicketDatabase().getTicketById(ticketId);
        if (ticket == null) {
            return;
        }

        if (!checkExpertiseArea(system, outputs, ticket)) {
            return;
        }
        if (!checkSeniority(system, outputs, ticket)) {
            return;
        }

        if (!checkOpenStatus(system, outputs, ticket)) {
            return;
        }

        if (!checkAssignedMilestone(system, outputs, ticket)) {
            return;
        }

        if (!checkBlockedMilestone(system, outputs, ticket)) {
            return;
        }

        Status oldStatus = ticket.getStatus();
        ticket.setStatus(Status.IN_PROGRESS);
        Status newStatus = ticket.getStatus();

        ticket.setAssignedTo(getUser().getUsername());
        LocalDate assignedAt = LocalDate.parse(getCommandInput().getTimestamp());
        ticket.setAssignedAt(assignedAt);

        ticket.addHistoryAssign("ASSIGNED",
                getUser().getUsername(),
                getCommandInput().getTimestamp()
                );
        ticket.addHistoryStatus("STATUS_CHANGED",
                oldStatus.name(),
                newStatus.name(),
                getUser().getUsername(),
                getCommandInput().getTimestamp()
                );
    }

    private boolean checkExpertiseArea(final BugTrackerSystem system,
                                       final List<ObjectNode> outputs, final Ticket ticket) {
        Developer developer = (Developer) getUser();
        ExpertiseArea devExpertise = developer.getExpertiseArea();
        String ticketZone = ticket.getExpertiseArea().name();
        if (devExpertise.hasAccessToZone(ticketZone)) {
            return true;
        }

        List<String> requiredAreas = new ArrayList<>();
        for (ExpertiseArea expertiseArea : ExpertiseArea.values()) {
            if (expertiseArea.hasAccessToZone(ticketZone)) {
                requiredAreas.add(expertiseArea.name());
            }
        }
        Collections.sort(requiredAreas);

        String requiredString = String.join(", ", requiredAreas);

        String error = "Developer " + developer.getUsername()
                + " cannot assign ticket " + ticket.getId()
                + " due to expertise area. Required: " + requiredString
                + "; Current: " + devExpertise.name() + ".";

        outputs.add(OutputFormatter.createError(
                getCommandInput().getCommand(),
                getCommandInput().getUsername(),
                getCommandInput().getTimestamp(),
                error
        ));
        return false;

    }

    private boolean checkSeniority(final BugTrackerSystem system, final List<ObjectNode> outputs,
                                   final Ticket ticket) {
        Developer developer = (Developer) getUser();
        Seniority seniority = developer.getSeniority();
        BusinessPriority businessPriority = ticket.getBusinessPriority();
        TicketType ticketType = ticket.getType();

        if (seniority.hasAccessToPriority(businessPriority)
                && seniority.hasAccessToTicketType(ticketType)) {
            return true;
        }

        List<String> requiredSeniority = new ArrayList<>();
        for (Seniority s : Seniority.values()) {
            if (s.hasAccessToTicketType(ticketType) && s.hasAccessToPriority(businessPriority)) {
                requiredSeniority.add(s.name());
            }
        }
        Collections.sort(requiredSeniority);

        String requiredString = String.join(", ", requiredSeniority);

        String error = "Developer " + developer.getUsername()
                + " cannot assign ticket " + ticket.getId()
                + " due to seniority level. Required: " + requiredString
                + "; Current: " + seniority.name() + ".";

        outputs.add(OutputFormatter.createError(
                getCommandInput().getCommand(),
                getCommandInput().getUsername(),
                getCommandInput().getTimestamp(),
                error
        ));

        return false;

    }

    private boolean checkOpenStatus(final BugTrackerSystem system, final List<ObjectNode> outputs,
                                    final Ticket ticket) {
        Developer developer = (Developer) getUser();
        if (ticket.getStatus() != Status.OPEN) {
            String error = "Only OPEN tickets can be assigned.";
            outputs.add(OutputFormatter.createError(
                    getCommandInput().getCommand(),
                    getCommandInput().getUsername(),
                    getCommandInput().getTimestamp(),
                    error
            ));
            return false;
        }
        return true;
    }

    private boolean checkAssignedMilestone(final BugTrackerSystem system,
                                           final List<ObjectNode> outputs, final Ticket ticket) {
        Developer developer = (Developer) getUser();

        Milestone ownerMilestone =
                system.getMilestoneDatabase().getMilestoneByTicketId(ticket.getId());

        if (ownerMilestone == null) {
            throw new IllegalArgumentException("Ticket "
                    + ticket.getId() + " was not in any milestone");
        }

        if (ownerMilestone.getAssignedDevs().contains(developer.getUsername())) {
            return true;
        }

        String error = "Developer " + developer.getUsername()
                + " is not assigned to milestone " + ownerMilestone.getName() + ".";

        outputs.add(OutputFormatter.createError(
                getCommandInput().getCommand(),
                getCommandInput().getUsername(),
                getCommandInput().getTimestamp(),
                error
        ));
        return false;


    }

    private boolean checkBlockedMilestone(final BugTrackerSystem system,
                                          final List<ObjectNode> outputs, final Ticket ticket) {
        Developer developer = (Developer) getUser();

        Milestone ownerMilestone =
                system.getMilestoneDatabase().getMilestoneByTicketId(ticket.getId());

        if (ownerMilestone == null) {
            throw new IllegalArgumentException("Ticket "
                    + ticket.getId() + " was not in any milestone");
        }

        if (!ownerMilestone.isBlocked()) {
            return true;
        }

        String error = "Cannot assign ticket " + ticket.getId() + " from blocked milestone "
                + ownerMilestone.getName() + ".";

        outputs.add(OutputFormatter.createError(
                getCommandInput().getCommand(),
                getCommandInput().getUsername(),
                getCommandInput().getTimestamp(),
                error
        ));
        return false;

    }
}
