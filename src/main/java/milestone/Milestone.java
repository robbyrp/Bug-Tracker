package milestone;

import enums.BusinessPriority;
import enums.MilestoneStatus;
import enums.Status;
import fileio.CommandInput;
import lombok.Getter;
import lombok.Setter;
import ticket.Ticket;
import ticket.TicketDatabase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.IllformedLocaleException;
import java.util.Objects;

@Getter @Setter
public final class Milestone {
    // Input fields
    private String name;
    private LocalDate dueDate;
    private ArrayList<String> blockingFor;
    private ArrayList<Integer> tickets;
    private ArrayList<String> assignedDevs;

    // Output fields
    private LocalDate createdAt;
    private MilestoneStatus status;
    private boolean isBlocked;
    private Integer daysUntilDue;
    private Integer overdueBy;
    private ArrayList<Integer> openTickets;
    private ArrayList<Integer> closedTickets;
    private Double completionPercentage;
    private Repartition repartition;


    /**
     * Constructs fields from input and safely initializes
     * the other fields. Sets the isBlocked status for milestones in
     * blockedFor field
     * @param commandInput
     */
    Milestone(final CommandInput commandInput, final MilestoneDatabase milestoneDatabase) {
        this.name = commandInput.getName();
        this.dueDate = LocalDate.parse(commandInput.getDueDate());

        this.blockingFor = commandInput.getBlockingFor();
        for (String milestoneName : blockingFor) {
            Milestone milestone = milestoneDatabase.getMilestoneByName(milestoneName);
            if (milestone == null)
                continue;
            milestone.isBlocked = true;
        }

        this.tickets = commandInput.getTickets();
        this.assignedDevs = commandInput.getAssignedDevs();
        this.createdAt = LocalDate.parse(commandInput.getTimestamp());

        this.status = MilestoneStatus.ACTIVE;
        this.openTickets = new ArrayList<>(tickets);
        this.closedTickets = new ArrayList<>();
        this.completionPercentage = 0.0;
        this.isBlocked = false;
    }

    public void updateMilestones(final LocalDate currentDate, final TicketDatabase ticketDatabase,
                                 final MilestoneDatabase milestoneDatabase) {
        calculateTimeMetrics(currentDate);

        calculateTicketProgress(ticketDatabase);

        incrementTicketPriority(currentDate, ticketDatabase);

        updateTicketStatus(currentDate, ticketDatabase);

    }

    /**
     * Sets the dausUntilDue and overdueBy members
     * @param currentDate
     */
    private void calculateTimeMetrics(final LocalDate currentDate) {
        int daysBetween = (int) ChronoUnit.DAYS.between(currentDate, dueDate) + 1;
        if (daysBetween < 1) {
            this.daysUntilDue = 0;
            this.overdueBy = 1 - daysBetween;
        } else {
            this.daysUntilDue = daysBetween;
            this.overdueBy = 0;
        }
    }

    /**
     * Updates the closedTickets and openTickets member lists
     * Calculates and sets the completionPercentage
     * @param ticketDatabase
     */
    private void calculateTicketProgress(final TicketDatabase ticketDatabase) {
        this.openTickets.clear();
        this.closedTickets.clear();

        for (Integer ticketId : this.tickets) {
            Ticket t = ticketDatabase.getTicketById(ticketId);
            if (t != null) {
                if (t.getStatus() == Status.CLOSED || t.getStatus() == Status.RESOLVED) {
                    this.closedTickets.add(ticketId);
                } else {
                    this.openTickets.add(ticketId);
                }
            }
        }

        if (tickets.isEmpty()) {
            this.completionPercentage = 100.0;
        } else {
            this.completionPercentage = (double) closedTickets.size() / tickets.size() * 100;
        }
    }

    /** Helper method for updateTicketStatus
     * Increments ticket priority every 3 days
     * Rule 1
     * @param currentDate
     * @param ticketDatabase
     */
    private void incrementTicketPriority(final LocalDate currentDate, final TicketDatabase ticketDatabase) {
        int daysBetween = (int) ChronoUnit.DAYS.between(this.createdAt, currentDate) + 1;

        if (daysBetween % 3 == 1 && daysBetween != 1) {
            for (Integer ticketId : this.tickets) {
                Ticket t = ticketDatabase.getTicketById(ticketId);
                switch (Objects.requireNonNull(t).getBusinessPriority()) {
                    case LOW -> t.setBusinessPriority(BusinessPriority.MEDIUM);
                    case MEDIUM -> t.setBusinessPriority(BusinessPriority.HIGH);
                    case HIGH -> t.setBusinessPriority(BusinessPriority.CRITICAL);
                }
            }
        }

    }

    /** Helper method for updateTicketStatus
     * Updates the tickets' priority to critical and notifies
     * the responsible devs. Is called in updateTicketStatus
     * only if dueDate is tomorrow
     * @param currentDate
     * @param ticketDatabase
     */
    private void updateToCritical(final LocalDate currentDate, final TicketDatabase ticketDatabase) {
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
    public void updateTicketStatus(final LocalDate currentDate, final TicketDatabase ticketDatabase) {
        if (this.isBlocked)
            return;

        incrementTicketPriority(currentDate, ticketDatabase);

        int daysBetweenCurrDue = (int) ChronoUnit.DAYS.between(currentDate, this.dueDate);
        boolean dueDatePassed = daysBetweenCurrDue > 0;
        if (daysBetweenCurrDue == 1 || dueDatePassed) {
            updateToCritical(currentDate, ticketDatabase);
        }
    }

    /**
     * Atenție!
     * Un milestone este deblocat în momentul în care ultimul tichet
     * din milestone-ul blocant devine CLOSED.
     */

    /**
     * If all tickets in milestone are closed, unblock milestones in
     * blockingFor field
     * @param milestoneDatabase
     */
    private void unblockMilestones(final MilestoneDatabase milestoneDatabase) {
        if (!this.openTickets.isEmpty())
            return;

        for (String milestoneName : this.blockingFor) {
            Milestone milestone = milestoneDatabase.getMilestoneByName(milestoneName);
            if (milestone == null) {
                continue;
            }
            milestone.isBlocked = false;
        }
    }

    //TODO: bug-urile din milestone-ul blocat nu pot fi rezolvate sau preluate de un developer
}
