package milestone;

import enums.BusinessPriority;
import enums.MilestoneStatus;
import enums.Status;
import ticket.Ticket;
import ticket.TicketDatabase;
import user.User;
import user.UserDatabase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

public final class MilestoneManager {
    private static final int DAYS_BETWEEN_UPDATES = 3;
    private static final double MAX_COMPL_PERCENTAGE = 1.0;
    private static final double ROUNDING_VALUE = 100.0;

    /**
     * Main method, called from BugTrackerSystem
     * @param milestoneDatabase
     * @param ticketDatabase
     * @param userDatabase
     * @param currentDate
     */
    public void updateAllMilestones(final MilestoneDatabase milestoneDatabase,
                                    final TicketDatabase ticketDatabase,
                                    final UserDatabase userDatabase,
                                    final LocalDate currentDate) {

        for (Milestone m : milestoneDatabase.getMilestoneList()) {
            processMilestone(m, milestoneDatabase, ticketDatabase, userDatabase, currentDate);
        }
    }

    /**
     * Calls helper methods to process and update the Milestone given
     * as parameter
     * @param m
     * @param milestoneDatabase
     * @param ticketDatabase
     * @param userDatabase
     * @param currentDate
     */
    private void processMilestone(final Milestone m,
                                  final MilestoneDatabase milestoneDatabase,
                                  final TicketDatabase ticketDatabase,
                                  final UserDatabase userDatabase,
                                  final LocalDate currentDate) {

        updateProgressAndStatus(m, ticketDatabase);

        updateTimeMetrics(m, ticketDatabase, currentDate);

        processUnblocking(m, milestoneDatabase, ticketDatabase, userDatabase, currentDate);

        if (m.getStatus() == MilestoneStatus.ACTIVE && !m.isBlocked()) {
            processActiveMilestoneRules(m, ticketDatabase, userDatabase, currentDate);
        }
    }

    /**
     * Helper method for processMilestone
     * Updates the closedTickets and openTickets member lists
     * Sets the milestone status to completed if necessary
     * Calculates and sets the completionPercentage
     * Actualizes the repartition and sorts it
     * @param m
     * @param ticketDatabase
     */
    private void updateProgressAndStatus(final Milestone m,
                                         final TicketDatabase ticketDatabase) {
        m.getOpenTickets().clear();
        m.getClosedTickets().clear();
        for (Repartition r : m.getRepartition()) {
            r.clear();
        }

        for (Integer ticketId : m.getTickets()) {
            Ticket t = ticketDatabase.getTicketById(ticketId);
            if (t == null) continue;

            if (t.getStatus() == Status.CLOSED) {
                m.getClosedTickets().add(ticketId);
            } else {
                m.getOpenTickets().add(ticketId);
            }

            String dev = t.getAssignedTo();
            if (dev != null && !dev.isEmpty()) {
                for (Repartition r : m.getRepartition()) {
                    if (r.getDeveloper().equals(dev)) {
                        r.addTicketId(ticketId);
                        break;
                    }
                }
            }
        }

        if (!m.getTickets().isEmpty() && m.getOpenTickets().isEmpty()) {
            m.setStatus(MilestoneStatus.COMPLETED);
        }

        if (m.getTickets().isEmpty()) {
            m.setCompletionPercentage(MAX_COMPL_PERCENTAGE);
        } else {
            double ratio = (double) m.getClosedTickets().size() / m.getTickets().size();
            double completionPercentage = Math.floor(ratio * ROUNDING_VALUE) / ROUNDING_VALUE;
            m.setCompletionPercentage(completionPercentage);
        }

        Collections.sort(m.getRepartition());

    }

    /**
     * Helper method for processMilestone
     * Updates the time metrics. If the milestone is completed,
     * it uses the completion date of the last ticket
     * Sets the daysUntilDue and overdueBy members
     * @param m
     * @param ticketDatabase
     * @param currentDate
     */
    private void updateTimeMetrics(final Milestone m, final TicketDatabase ticketDatabase,
                                   final LocalDate currentDate) {
        LocalDate calculationDate = currentDate;

        if (m.getStatus() == MilestoneStatus.COMPLETED) {
            Ticket lastTicket = findLastClosedTicket(m, ticketDatabase);
            if (lastTicket != null) {
                calculationDate = lastTicket.getSolvedAt();
            }
        }

        int daysBetween = (int) ChronoUnit.DAYS.between(calculationDate, m.getDueDate());
        if (daysBetween < 0) {
            m.setDaysUntilDue(0);
            m.setOverdueBy(Math.abs(daysBetween) + 1);
        } else {
            m.setDaysUntilDue(daysBetween + 1);
            m.setOverdueBy(0);
        }
    }

    /** Helper method for processMilestone.
     * If all tickets in milestone are closed, unblock milestones in
     * blockingFor field and send notifications
     * @param m
     * @param milestoneDatabase
     * @param ticketDatabase
     * @param userDatabase
     * @param currentDate
     */
    private void processUnblocking(final Milestone m,
                                   final MilestoneDatabase milestoneDatabase,
                                   final TicketDatabase ticketDatabase,
                                   final UserDatabase userDatabase,
                                   final LocalDate currentDate) {

        if (!m.getOpenTickets().isEmpty()) return;

        Ticket lastTicket = findLastClosedTicket(m, ticketDatabase);
        int triggerTicketId = (lastTicket != null) ? lastTicket.getId() : -1;

        List<Milestone> blockedMilestones = milestoneDatabase.getMilestonesByNames(m.getBlockingFor());

        for (Milestone blockedMilestone : blockedMilestones) {
            if (blockedMilestone.isBlocked()) {
                blockedMilestone.setBlocked(false);

                if (currentDate.isAfter(blockedMilestone.getDueDate())) {
                    String msg = "Milestone " + blockedMilestone.getName() +
                            " was unblocked after due date. All active tickets are now CRITICAL.";
                    notifyDevs(blockedMilestone, userDatabase, msg);
                    forceCritical(blockedMilestone, ticketDatabase);
                } else {
                    String msg = "Milestone " + blockedMilestone.getName() +
                            " is now unblocked as ticket " + triggerTicketId + " has been CLOSED.";
                    notifyDevs(blockedMilestone, userDatabase, msg);
                }
            }
        }
    }

    /** Helper method for processMilestone.
     * Updates ticket priorities every 3 days, forces critical priority
     * and sends notifications is necessary
     * @param m
     * @param ticketDatabase
     * @param userDatabase
     * @param currentDate
     */
    private void processActiveMilestoneRules(final Milestone m,
                                             final TicketDatabase ticketDatabase,
                                             final UserDatabase userDatabase,
                                             final LocalDate currentDate) {
        if (m.getLastPriorityUpdateDate() == null || currentDate.isAfter(m.getLastPriorityUpdateDate())) {
            int daysSinceStart = (int) ChronoUnit.DAYS.between(m.getCreatedAt(), currentDate) + 1;

            if (daysSinceStart % DAYS_BETWEEN_UPDATES == 1 && daysSinceStart != 1) {
                incrementPriorities(m, ticketDatabase);
            }
            m.setLastPriorityUpdateDate(currentDate);
        }

        int daysUntil = (int) ChronoUnit.DAYS.between(currentDate, m.getDueDate());

        if (daysUntil == 1) {
            String msg = "Milestone " + m.getName()
                    + " is due tomorrow. All unresolved tickets are now CRITICAL.";
            notifyDevs(m, userDatabase, msg);
            forceCritical(m, ticketDatabase);
        } else if (daysUntil < 0) {
            forceCritical(m, ticketDatabase);
        }
    }


    /**
     * Helper method that calls the user.update() method.
     * Adds the message given as parameter to the dev's notifications list
     * @param m
     * @param userDatabase
     * @param message
     */
    private void notifyDevs(final Milestone m, final UserDatabase userDatabase, final String message) {
        for (String username : m.getAssignedDevs()) {
            User u = userDatabase.getUsers().get(username);
            if (u != null) u.update(message);
        }
    }


    /**
     * Helper method that sets all ticket statuses in milestone m to critical.
     * @param m
     * @param ticketDatabase
     */
    private void forceCritical(final Milestone m, final TicketDatabase ticketDatabase) {
        for(Integer id : m.getTickets()) {
            Ticket t = ticketDatabase.getTicketById(id);
            if(t != null && (t.getStatus() == Status.OPEN || t.getStatus() == Status.IN_PROGRESS)) {
                t.setBusinessPriority(BusinessPriority.CRITICAL);
            }
        }
    }

    /**
     * Helper method that increments ticket priorities in milestone m.
     * Called every 3 days (except for dueDate exceptions)
     * @param m
     * @param ticketDatabase
     */
    private void incrementPriorities(final Milestone m, final TicketDatabase ticketDatabase) {
        for(Integer id : m.getTickets()) {
            Ticket t = ticketDatabase.getTicketById(id);
            if (t == null) {
                continue;
            }
            switch(t.getBusinessPriority()) {

                case LOW -> t.setBusinessPriority(BusinessPriority.MEDIUM);
                case MEDIUM -> t.setBusinessPriority(BusinessPriority.HIGH);
                case HIGH -> t.setBusinessPriority(BusinessPriority.CRITICAL);
            }
        }
    }

    /**
     * Helper method that returns the last solved ticket in milestone given as param
     * @param m
     * @param ticketDatabase
     * @return
     */
    private Ticket findLastClosedTicket(final Milestone m, final TicketDatabase ticketDatabase) {
        Ticket last = null;
        for(Integer id : m.getTickets()) {
            Ticket t = ticketDatabase.getTicketById(id);
            if (t != null && t.getSolvedAt() != null) {
                if (last == null || t.getSolvedAt().isAfter(last.getSolvedAt())) {
                    last = t;
                }
            }
        }
        return last;
    }

    //TODO: Dacă un tichet este redeschis dintr-un milestone anterior blocant,
    // milestone-urile deblocate ramân deblocate.
}