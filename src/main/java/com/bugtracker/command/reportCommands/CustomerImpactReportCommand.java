package com.bugtracker.command.reportCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.enums.Status;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.OutputFormatter;
import com.bugtracker.BugTrackerSystem;
import com.bugtracker.ticket.Bug;
import com.bugtracker.ticket.FeatureRequest;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.ticket.UiRequest;
import com.bugtracker.user.User;
import com.bugtracker.utils.ReportScoreDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerImpactReportCommand extends AbstractReportCommand {

    private static final double BUG_IMPACT = 48.0;
    private static final double FEATURE_IMPACT = 100.0;
    private static final double UI_IMPACT = 100.0;

    public CustomerImpactReportCommand(final CommandInput input, final User user) {
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
            if (!ticket.getStatus().equals(Status.OPEN)
                    && !ticket.getStatus().equals(Status.IN_PROGRESS)) {
                continue;
            }
            totalTickets++;

            updateCounters(ticket, ticketsByType, ticketsByPriority);

            switch (ticket.getType()) {

                case BUG:
                    Bug bug = (Bug) ticket;
                    bugScores.add(calculateBugImpact(bug));
                    break;

                case FEATURE_REQUEST:
                    FeatureRequest fr = (FeatureRequest) ticket;
                    featureScores.add(calculateFeatureImpact(fr));
                    break;

                case UI_FEEDBACK:
                    UiRequest uiRequest = (UiRequest) ticket;
                    uiScores.add(calculateUiImpact(uiRequest));
                    break;

                default:
                    break;

            }
        }

        Map<String, Double> customerImpactByType = new LinkedHashMap<>();
        customerImpactByType.put("BUG", calculateAverage(bugScores));
        customerImpactByType.put("FEATURE_REQUEST", calculateAverage(featureScores));
        customerImpactByType.put("UI_FEEDBACK", calculateAverage(uiScores));

        Map<String, Object> reportObject = new LinkedHashMap<>();
        reportObject.put("totalTickets", totalTickets);
        reportObject.put("ticketsByType", ticketsByType);
        reportObject.put("ticketsByPriority", ticketsByPriority);
        reportObject.put("customerImpactByType", customerImpactByType);

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
     * @return
     */
    private double calculateBugImpact(final Bug bug) {
        int frequency = ReportScoreDatabase.getFrequencyScore(bug.getFrequency());
        int priority = ReportScoreDatabase.getPriorityScore(bug.getBusinessPriority());
        int severity = ReportScoreDatabase.getSeverityScore(bug.getSeverity());

        double rawScore = (double) frequency * priority * severity;
        return normalize(rawScore, BUG_IMPACT);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param fr
     * @return
     */
    private double calculateFeatureImpact(final FeatureRequest fr) {
        int val = ReportScoreDatabase.getBusinessValueScore(fr.getBusinessValue());
        int demand = ReportScoreDatabase.getCustomerDemandScore(fr.getCustomerDemand());

        double rawScore = (double) val * demand;
        return normalize(rawScore, FEATURE_IMPACT);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param uiRequest
     * @return
     */
    private double calculateUiImpact(final UiRequest uiRequest) {
        int val = ReportScoreDatabase.getBusinessValueScore(uiRequest.getBusinessValue());
        int usability = uiRequest.getUsabilityScore();

        double rawScore = (double) val * usability;
        return normalize(rawScore, UI_IMPACT);
    }

}
