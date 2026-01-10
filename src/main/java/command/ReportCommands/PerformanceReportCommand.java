package command.ReportCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Command;
import enums.*;
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
import java.util.*;

public class PerformanceReportCommand extends Command {
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
     * IN_PROGRESS and RESOLVED 
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


    private double calculateJuniorScore(int closedTickets, int bugs, int features, int uis) {
        double diversity = ticketDiversityFactor(bugs, features, uis);
        double base = (0.5 * closedTickets) - diversity;
        return Math.max(0.0, base) + 5.0;
    }

    private double calculateMidScore(int closedTickets, int highPrio, double avgTime) {
        double base = (0.5 * closedTickets) + (0.7 * highPrio) - (0.3 * avgTime);
        return Math.max(0.0, base) + 15.0;
    }

    private double calculateSeniorScore(int closedTickets, int highPrio, double avgTime) {
        double base = (0.5 * closedTickets) + (1.0 * highPrio) - (0.5 * avgTime);
        return Math.max(0.0, base) + 30.0;
    }


    private double averageResolvedTicketType(int bug, int feature, int ui) {
        return (bug + feature + ui) / 3.0;
    }

    private double standardDeviation(int bug, int feature, int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        double variance = (Math.pow(bug - mean, 2)
                + Math.pow(feature - mean, 2)
                + Math.pow(ui - mean, 2)) / 3.0;
        return Math.sqrt(variance);
    }

    private double ticketDiversityFactor(int bug, int feature, int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        if (mean == 0.0) {
            return 0.0;
        }
        double std = standardDeviation(bug, feature, ui);
        return std / mean;
    }


    private long calculateDaysToResolve(Ticket t, LocalDate end) {
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
    private LocalDate getTicketLastResolvedDate(Ticket ticket) {
        if (ticket.getHistory() == null || ticket.getHistory().isEmpty()) {
            return ticket.getSolvedAt();
        }

        List<TicketAction> history = ticket.getHistory();

        for (int i = history.size() - 1; i >= 0; i--) {
            TicketAction action = history.get(i);
            if (action.getAction().equals("STATUS_CHANGED")){
                 if (action.getTo().equals("RESOLVED") && action.getFrom().equals("IN_PROGRESS")) {
                    return LocalDate.parse(action.getTimestamp());
                 }
            }
        }
        return ticket.getSolvedAt();
    }






    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
