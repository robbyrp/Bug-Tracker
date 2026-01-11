package command.reportCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Command;
import enums.ApplicationPhase;
import enums.Role;
import enums.Status;
import enums.TicketType;
import enums.BusinessPriority;
import enums.Seniority;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import ticket.Ticket;
import ticket.TicketAction;
import user.Developer;
import user.Manager;
import user.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.LinkedHashMap;

public final class PerformanceReportCommand extends Command {
    private static final double ROUND_VALUE = 100.0;
    private static final double CLOSED_PARAM = 0.5;
    private static final double PRIO_MID_PARAM = 0.5;
    private static final double HIGH_PRIO_MID_PARAM = 0.7;
    private static final double AVG_MID = 0.3;
    private static final double HIGH_PRIO_SENIOR_PARAM = 1.0;
    private static final double AVG_TIME_PARAM = 0.5;
    private static final double AVERAGE_FACTOR = 3.0;
    private static final double STD_DEV_FACTOR = 2.0;


    private static final double JUNIOR_BONUS = 5.0;
    private static final double MID_BONUS = 15.0;
    private static final double SENIOR_BONUS = 30.0;

    public PerformanceReportCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }

    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.MANAGER);
    }

    /**
     * Execute method that calculates the performance report metrics.
     * It checks ticket history for last status change between
     * * IN_PROGRESS and RESOLVED
     * @param system The engine of the program
     * @param outputs output mapper
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        User user = getUser();
        if (!user.getRole().equals(Role.MANAGER)) {
            return;
        }
        Manager manager = (Manager) user;

        LocalDate commandDate = LocalDate.parse(getCommandInput().getTimestamp());
        LocalDate targetMonthDate = commandDate.minusMonths(1);
        int targetMonth = targetMonthDate.getMonthValue();
        int targetYear = targetMonthDate.getYear();

        List<Developer> teamDevs = new ArrayList<>();
        for (String devName : manager.getSubordinates()) {
            User developerUser = system.getUserDatabase().getUsers().get(devName);
            Developer dev = (Developer) developerUser;
            teamDevs.add(dev);
        }

        teamDevs.sort(Comparator.comparing(Developer::getUsername));

        List<Map<String, Object>> reportList = new ArrayList<>();

        for (Developer developer : teamDevs) {
            List<Ticket> devTickets = new ArrayList<>();
            int bugCount = 0;
            int featureCount = 0;
            int uiCount = 0;
            int highPriorityCount = 0;
            double totalResolutionDays = 0.0;

            for (Ticket t : system.getTicketDatabase().getTickets()) {
                if (t.getAssignedTo() == null
                        || !t.getAssignedTo().equals(developer.getUsername())) {
                    continue;
                }
                if (t.getStatus() != Status.CLOSED || t.getSolvedAt() == null) {
                    continue;
                }

                LocalDate ticketResolutionDate = getTicketLastResolvedDate(t);
                if (ticketResolutionDate == null) {
                    continue;
                }

                if (ticketResolutionDate.getMonthValue() == targetMonth
                        && ticketResolutionDate.getYear() == targetYear) {

                    devTickets.add(t);
                    if (t.getType().equals(TicketType.BUG)) {
                        bugCount++;
                    }
                    if (t.getType().equals(TicketType.FEATURE_REQUEST)) {
                        featureCount++;
                    }
                    if (t.getType().equals(TicketType.UI_FEEDBACK)) {
                        uiCount++;
                    }

                    if (t.getBusinessPriority().equals(BusinessPriority.HIGH)
                            || t.getBusinessPriority() == BusinessPriority.CRITICAL) {
                        highPriorityCount++;
                    }
                    totalResolutionDays += calculateDaysToResolve(t, ticketResolutionDate);
                }
            }
            int closedTickets = devTickets.size();
            double avgResolutionTime = (closedTickets > 0)
                    ? totalResolutionDays / closedTickets
                    : 0.0;

            double performanceScore = 0.0;

            if (closedTickets > 0) {
                switch (developer.getSeniority()) {
                    case Seniority.JUNIOR -> performanceScore = calculateJuniorScore(
                            closedTickets, bugCount, featureCount, uiCount);

                    case Seniority.MID -> performanceScore = calculateMidScore(
                            closedTickets, highPriorityCount, avgResolutionTime);

                    case Seniority.SENIOR -> performanceScore = calculateSeniorScore(
                            closedTickets, highPriorityCount, avgResolutionTime);

                    default -> {
                        return;
                    }
                }
            }

            Map<String, Object> devReport = new LinkedHashMap<>();
            devReport.put("username", developer.getUsername());
            devReport.put("closedTickets", closedTickets);
            devReport.put("averageResolutionTime", round(avgResolutionTime));
            devReport.put("performanceScore", round(performanceScore));
            devReport.put("seniority", developer.getSeniority().name());

            reportList.add(devReport);
            developer.setPerformanceScore(round(performanceScore));
        }

        outputs.add(OutputFormatter.createListResponse(
                getCommandInput().getCommand(),
                getCommandInput().getUsername(),
                getCommandInput().getTimestamp(),
                "report",
                reportList
        ));
    }


    /**
     * Calculates junior score
     * @param closedTickets
     * @param bugs
     * @param features
     * @param uis
     * @return
     */
    private double calculateJuniorScore(final int closedTickets, final int bugs,
                                        final int features, final int uis) {
        double diversity = ticketDiversityFactor(bugs, features, uis);
        double base = (CLOSED_PARAM * closedTickets) - diversity;
        return Math.max(0.0, base) + JUNIOR_BONUS;
    }

    /**
     * Calculates mid score
     * @param closedTickets
     * @param highPrio
     * @param avgTime
     * @return
     */
    private double calculateMidScore(final int closedTickets,
                                     final int highPrio, final double avgTime) {
        double base = (CLOSED_PARAM * closedTickets) + (HIGH_PRIO_MID_PARAM * highPrio)
                - (AVG_MID * avgTime);
        return Math.max(0.0, base) + MID_BONUS;
    }

    /**
     * Calculates senior score
     * @param closedTickets
     * @param highPrio
     * @param avgTime
     * @return
     */
    private double calculateSeniorScore(final int closedTickets, final int highPrio,
                                        final double avgTime) {
        double base = (CLOSED_PARAM * closedTickets)
                + (HIGH_PRIO_SENIOR_PARAM * highPrio) - (AVG_TIME_PARAM * avgTime);
        return Math.max(0.0, base) + SENIOR_BONUS;
    }


    /**
     * Calculates average
     * @param bug
     * @param feature
     * @param ui
     * @return
     */
    private double averageResolvedTicketType(final int bug, final int feature, final int ui) {
        return (bug + feature + ui) / AVERAGE_FACTOR;
    }

    /**
     * Standard deviation
     * @param bug
     * @param feature
     * @param ui
     * @return
     */
    private double standardDeviation(final int bug, final int feature, final int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        double variance = (Math.pow(bug - mean, STD_DEV_FACTOR)
                + Math.pow(feature - mean, STD_DEV_FACTOR)
                + Math.pow(ui - mean, STD_DEV_FACTOR)) / AVERAGE_FACTOR;
        return Math.sqrt(variance);
    }

    /**
     * Calculate ticket diversity factor
     * @param bug
     * @param feature
     * @param ui
     * @return
     */
    private double ticketDiversityFactor(final int bug, final int feature, final int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        if (mean == 0.0) {
            return 0.0;
        }
        double std = standardDeviation(bug, feature, ui);
        return std / mean;
    }


    /**
     * Calculates days to resolve
     * @param t
     * @param end
     * @return
     */
    private long calculateDaysToResolve(final Ticket t, final LocalDate end) {
        if (t.getAssignedAt() == null || end == null) {
            return 0;
        }

        return ChronoUnit.DAYS.between(t.getAssignedAt(), end) + 1;
    }

    /**
     * Returns the last iteration in history where a ticket went from
     * "IN_PROGRESS" TO "SOLVED", to get the "real" solvedAt date
     * Otherwise, there was a conflict between tests 9 and 17
     * @param ticket
     * @return
     */
    private LocalDate getTicketLastResolvedDate(final Ticket ticket) {
        if (ticket.getHistory() == null || ticket.getHistory().isEmpty()) {
            return ticket.getSolvedAt();
        }

        List<TicketAction> history = ticket.getHistory();

        for (int i = history.size() - 1; i >= 0; i--) {
            TicketAction action = history.get(i);
            if (action.getAction().equals("STATUS_CHANGED")) {
                 if (action.getTo().equals("RESOLVED") && action.getFrom().equals("IN_PROGRESS")) {
                    return LocalDate.parse(action.getTimestamp());
                 }
            }
        }
        return ticket.getSolvedAt();
    }

    /**
     * Rounding method
     * @param value
     * @return
     */
    private double round(final double value) {
        return Math.round(value * ROUND_VALUE) / ROUND_VALUE;
    }
}
