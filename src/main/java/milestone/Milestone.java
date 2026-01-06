package milestone;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import enums.BusinessPriority;
import enums.MilestoneStatus;
import enums.Status;
import fileio.CommandInput;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ticket.Ticket;
import ticket.TicketDatabase;
import user.User;
import user.UserDatabase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@JsonPropertyOrder({
        "name", "blockingFor", "dueDate", "createdAt", "tickets", "assignedDevs",
        "createdBy", "status", "isBlocked", "daysUntilDue", "overdueBy",
        "openTickets", "closedTickets", "completionPercentage", "repartition"
})
@Getter @Setter
public final class Milestone {
    // Input fields
    private String name;
    private LocalDate dueDate;
    private ArrayList<String> blockingFor;
    private ArrayList<Integer> tickets;
    private ArrayList<String> assignedDevs;
    private String createdBy;

    // Output fields
    private LocalDate createdAt;
    private MilestoneStatus status;
    @Getter(AccessLevel.NONE)
    private boolean isBlocked;
    private Integer daysUntilDue;
    private Integer overdueBy;
    private ArrayList<Integer> openTickets;
    private ArrayList<Integer> closedTickets;
    private Double completionPercentage;
    private ArrayList<Repartition> repartition;

    @JsonIgnore
    private LocalDate lastPriorityUpdateDate;
    @JsonIgnore
    private static final double MAX_PERCENTAGE = 100.0;
    @JsonIgnore
    private static final int DAYS_BETWEEN_UPDATES= 3;

    @JsonProperty("isBlocked")
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     * Constructs fields from input and safely initializes
     * the other fields. Sets the isBlocked status for milestones in
     * blockedFor field
     * @param commandInput
     */
    public Milestone(final CommandInput commandInput, final MilestoneDatabase milestoneDatabase) {
        this.name = commandInput.getName();
        this.dueDate = LocalDate.parse(commandInput.getDueDate());

        //TODO: Verifica sa vezi daca mai ai nevoie sa modifici isBlocked pe this
        this.blockingFor = commandInput.getBlockingFor();
        for (String milestoneName : blockingFor) {
            Milestone milestone = milestoneDatabase.getMilestoneByName(milestoneName);
            if (milestone == null) {
                continue;
            }
            milestone.isBlocked = true;
        }

        this.tickets = commandInput.getTickets();
        this.assignedDevs = commandInput.getAssignedDevs();
        this.createdBy = commandInput.getUsername();
        this.createdAt = LocalDate.parse(commandInput.getTimestamp());
        this.lastPriorityUpdateDate = this.createdAt;

        this.status = MilestoneStatus.ACTIVE;
        this.openTickets = new ArrayList<>(tickets);
        this.closedTickets = new ArrayList<>();
        this.completionPercentage = 0.0;
        this.isBlocked = false;
        this.repartition = new ArrayList<>();
        for (String developerUsername : assignedDevs) {
            this.repartition.add(new Repartition(developerUsername));
        }
    }

    /**
     * Helper method that sends notifications to all devs
     * assigned to the calling milestone
     * @param message
     * @param userDatabase
     */
    private void notifyAssignedDevs(String message, UserDatabase userDatabase) {
        for (String username : this.assignedDevs) {
            User user = userDatabase.getUsers().get(username);
            if (user != null) {
                user.update(message);
            }
        }
    }

    /**
     * Public method that calls helper methods to update Milestone fields.
     * Updates the Milestone calling the method
     * @param currentDate
     * @param ticketDatabase
     * @param milestoneDatabase
     */
    public void updateMilestone(final LocalDate currentDate,
                                final TicketDatabase ticketDatabase,
                                final MilestoneDatabase milestoneDatabase,
                                final UserDatabase userDatabase) {

        boolean milestoneWasCompleted = this.status.equals(MilestoneStatus.COMPLETED);

        updateTicketProgress(ticketDatabase);

        updateTimeMetrics(currentDate, ticketDatabase);

        unblockMilestones(milestoneDatabase, ticketDatabase, userDatabase);

        if (this.status.equals(MilestoneStatus.ACTIVE)) {
            updateTicketStatus(currentDate, ticketDatabase, userDatabase);
        }

    }


    /**
     * Helper method that returns the last solved(closed) ticket from
     * the calling milestone
     * @param ticketDatabase
     * @return
     */
    private Ticket lastClosedTicket(final TicketDatabase ticketDatabase) {
        Ticket lastTicket = null;
        for (Integer ticketId : this.tickets) {
            Ticket currentTicket = ticketDatabase.getTicketById(ticketId);
            if (currentTicket != null && currentTicket.getSolvedAt() != null) {
                if (lastTicket == null || currentTicket.getSolvedAt().isAfter(lastTicket.getSolvedAt())) {
                    lastTicket = currentTicket;
                }
            }
        }
        return lastTicket;
    }

    /**
     * Helper method for updateTicketStatus
     * Updates the time metrics. If the milestone is completed,
     * it uses the completion date of the last ticket
     * Sets the dausUntilDue and overdueBy members
     * @param currentDate
     */
    private void updateTimeMetrics(final LocalDate currentDate, final TicketDatabase ticketDatabase) {
        LocalDate calculationDate = currentDate;

        if (this.status.equals(MilestoneStatus.COMPLETED)) {
            Ticket lastTicket = lastClosedTicket(ticketDatabase);
            if (lastTicket != null) {
                calculationDate = lastTicket.getSolvedAt();
            }
        }

        int daysBetween = (int) ChronoUnit.DAYS.between(calculationDate, dueDate);

        if (daysBetween < 0) {
            this.daysUntilDue = 0;
            this.overdueBy = Math.abs(daysBetween) + 1;
        } else {
            this.daysUntilDue = daysBetween + 1;
            this.overdueBy = 0;
        }
    }

    /**
     * Helper method for updateTicketStatus
     * Updates the closedTickets and openTickets member lists
     * Sets the milestone status to completed if necessary
     * Calculates and sets the completionPercentage
     * Actualizes the repartition and sorts it
     * @param ticketDatabase
     */
    private void updateTicketProgress(final TicketDatabase ticketDatabase) {
        this.openTickets.clear();
        this.closedTickets.clear();

        for (Repartition r : repartition) {
            r.clear();
        }

        for (Integer ticketId : this.tickets) {
            Ticket t = ticketDatabase.getTicketById(ticketId);

            if (t == null) {
                continue;
            }

            if (t.getStatus() == Status.CLOSED) {
                this.closedTickets.add(ticketId);
            } else {
                this.openTickets.add(ticketId);
            }

            String assignedDev = t.getAssignedTo();
            if (assignedDev != null && !assignedDev.isEmpty()) {
                for (Repartition r : repartition) {
                    if (r.getDeveloper().equals(assignedDev)) {
                        r.addTicketId(ticketId);
                        break;
                    }
                }
            }
        }

        if (!tickets.isEmpty() && openTickets.isEmpty()) {
            this.status = MilestoneStatus.COMPLETED;
        }

        if (tickets.isEmpty()) {
            this.completionPercentage = 1.0;
        } else {
            double ratio = (double) closedTickets.size() / tickets.size();
            this.completionPercentage = Math.floor(ratio * 100) / 100.0;
        }

        Collections.sort(this.repartition);
    }

    /** Helper method for updateTicketStatus
     * Increments ticket priority every 3 days
     * Rule 1
     * @param currentDate
     * @param ticketDatabase
     */
    private void incrementTicketPriority(final LocalDate currentDate,
                                         final TicketDatabase ticketDatabase) {
        if (lastPriorityUpdateDate != null && !currentDate.isAfter(lastPriorityUpdateDate)) {
            return;
        }

        int daysBetween = (int) ChronoUnit.DAYS.between(this.createdAt, currentDate) + 1;

        if (daysBetween % DAYS_BETWEEN_UPDATES == 1 && daysBetween != 1) {
            for (Integer ticketId : this.tickets) {
                Ticket t = ticketDatabase.getTicketById(ticketId);
                switch (Objects.requireNonNull(t).getBusinessPriority()) {

                    case LOW -> t.setBusinessPriority(BusinessPriority.MEDIUM);
                    case MEDIUM -> t.setBusinessPriority(BusinessPriority.HIGH);
                    case HIGH -> t.setBusinessPriority(BusinessPriority.CRITICAL);
                }
            }
        }

        this.lastPriorityUpdateDate = currentDate;
    }

    /** Helper method for updateTicketStatus
     * Updates the tickets' priority to critical and notifies
     * the responsible devs. Is called in updateTicketStatus
     * only if dueDate is tomorrow
     * @param currentDate
     * @param ticketDatabase
     */
    private void updateToCritical(final LocalDate currentDate,
                                  final TicketDatabase ticketDatabase) {
        for (Integer ticketId : this.tickets) {
            Ticket t = ticketDatabase.getTicketById(ticketId);
            Status currentStatus = Objects.requireNonNull(t.getStatus());
            if (currentStatus == Status.OPEN || currentStatus == Status.IN_PROGRESS) {
                t.setBusinessPriority(BusinessPriority.CRITICAL);
                //TODO: SEND NOTIF TO ASSIGNED DEVS
                //TODO: IF UNBLOCKED AFTER DUEDATE, SEND SPECIAL NOTIFICATION
            }
        }
    }

    /**
     * Updates the milestone's tickets' statuses every three days
     * @param currentDate the date when calling the update method
     */
    public void updateTicketStatus(final LocalDate currentDate, final TicketDatabase ticketDatabase,
                                   final UserDatabase userDatabase) {
        if (this.isBlocked) {
            return;
        }

        incrementTicketPriority(currentDate, ticketDatabase);

        int daysBetweenCurrDue = (int) ChronoUnit.DAYS.between(currentDate, this.dueDate);

        if (daysBetweenCurrDue == 1) {
            String message = "Milestone " + this.name + " is due tomorrow. "
                    + "All unresolved tickets are now CRITICAL.";
            notifyAssignedDevs(message, userDatabase);

            updateToCritical(currentDate, ticketDatabase);
        } else if (daysBetweenCurrDue < 0) {
            updateToCritical(currentDate, ticketDatabase);
        }

    }

    /**
     * If all tickets in milestone are closed, unblock milestones in
     * blockingFor field
     * @param milestoneDatabase
     */
    private void unblockMilestones(final MilestoneDatabase milestoneDatabase,
                                   final TicketDatabase ticketDatabase,
                                   final UserDatabase userDatabase) {
        if (!this.openTickets.isEmpty()) {
            return;
        }

        Ticket lastClosedTicket = lastClosedTicket(ticketDatabase);
        Integer ticketId = (lastClosedTicket != null) ? lastClosedTicket.getId() : -1;

        for (String milestoneName : this.blockingFor) {
            Milestone milestone = milestoneDatabase.getMilestoneByName(milestoneName);
            if (milestone == null) {
                continue;
            }
            milestone.isBlocked = false;
        }
    }

    //TODO: Dacă un tichet este redeschis dintr-un milestone anterior blocant,
    // milestone-urile deblocate ramân deblocate.
}
