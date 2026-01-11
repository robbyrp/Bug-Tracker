package command.reportCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.Status;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import ticket.Bug;
import ticket.FeatureRequest;
import ticket.Ticket;
import ticket.UiRequest;
import user.User;
import utils.ReportScoreDatabase;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResolutionEfficiencyReportCommand extends AbstractReportCommand {

    private static final double BUG_EFFICIENCY = 70.0;
    private static final double FEATURE_EFFICIENCY = 20.0;
    private static final double UI_EFFICIENCY = 20.0;

    private static final double BUG_PARAM = 10.0;

    public ResolutionEfficiencyReportCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    /**
     * Execute method that uses LinkedHashMaps for output objects
     * (ordering in JSON File) and
     * ticketsByType and byPriority (for order retention) and array lists
     * for individual scores
     * @param system The engine of the program
     * @param outputs output mapper
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        Map<String, Integer> ticketsByType = getInitializedTypeMap();
        Map<String, Integer> ticketsByPriority = getInitializedPriorityMap();

        List<Double> bugScores = new ArrayList<>();
        List<Double> featureScores = new ArrayList<>();
        List<Double> uiScores = new ArrayList<>();

        int totalTickets = 0;
        List<Ticket> allTickets = system.getTicketDatabase().getTickets();

        for (Ticket ticket : allTickets) {
            if (!ticket.getStatus().equals(Status.RESOLVED)
                    && !ticket.getStatus().equals(Status.CLOSED)) {
                continue;
            }

            totalTickets++;
            updateCounters(ticket, ticketsByType, ticketsByPriority);

            int daysToResolve = calculateDaysToResolve(ticket);

            switch (ticket.getType()) {

                case BUG :
                    Bug bug = (Bug) ticket;
                    bugScores.add(calculateBugEfficiency(bug, daysToResolve));
                    break;

                case FEATURE_REQUEST:
                    FeatureRequest fr = (FeatureRequest) ticket;
                    featureScores.add(calculateFeatureEfficiency(fr, daysToResolve));
                    break;

                case UI_FEEDBACK:
                    UiRequest ui = (UiRequest) ticket;
                    uiScores.add(calculateUiEfficiency(ui, daysToResolve));
                    break;

                default:
                    break;
            }
        }
        Map<String, Double> efficiencyByType = new LinkedHashMap<>();
        efficiencyByType.put("BUG", calculateAverage(bugScores));
        efficiencyByType.put("FEATURE_REQUEST", calculateAverage(featureScores));
        efficiencyByType.put("UI_FEEDBACK", calculateAverage(uiScores));

        Map<String, Object> reportObject = new LinkedHashMap<>();
        reportObject.put("totalTickets", totalTickets);
        reportObject.put("ticketsByType", ticketsByType);
        reportObject.put("ticketsByPriority", ticketsByPriority);
        reportObject.put("efficiencyByType", efficiencyByType);

        outputs.add(OutputFormatter.createReportResponse(
                getCommandInput().getCommand(),
                getCommandInput().getUsername(),
                getCommandInput().getTimestamp(),
                "report",
                reportObject
        ));
    }

    /**
     * Applies formula to calculate specific parameter
     * @param bug
     * @param days
     * @return
     */
    private double calculateBugEfficiency(final Bug bug, final int days) {
        int frequency = ReportScoreDatabase.getFrequencyScore(bug.getFrequency());
        int severity = ReportScoreDatabase.getSeverityScore(bug.getSeverity());

        double rawScore = (double) (frequency + severity) * BUG_PARAM / days;
        return normalize(rawScore, BUG_EFFICIENCY);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param fr
     * @param days
     * @return
     */
    private double calculateFeatureEfficiency(final FeatureRequest fr, final int days) {
        int value = ReportScoreDatabase.getBusinessValueScore(fr.getBusinessValue());
        int demand = ReportScoreDatabase.getCustomerDemandScore(fr.getCustomerDemand());

        double rawScore = (double) (value + demand) / days;
        return normalize(rawScore, FEATURE_EFFICIENCY);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param ui
     * @param days
     * @return
     */
    private double calculateUiEfficiency(final UiRequest ui, final int days) {
        int value = ReportScoreDatabase.getBusinessValueScore(ui.getBusinessValue());
        int usability = ui.getUsabilityScore();

        double rawScore = (double) (usability + value) / days;
        return normalize(rawScore, UI_EFFICIENCY);
    }

    /**
     * Calculates daysToResolve according to formula
     * @param ticket
     * @return
     */
    private int calculateDaysToResolve(final Ticket ticket) {
        if (ticket.getAssignedAt() == null || ticket.getSolvedAt() == null) {
            return 1;
        }

        int days = (int) ChronoUnit.DAYS.between(ticket.getAssignedAt(), ticket.getSolvedAt());

        return 1 + days;
    }

}
