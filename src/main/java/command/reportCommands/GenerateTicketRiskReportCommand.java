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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GenerateTicketRiskReportCommand extends AbstractReportCommand {

    private static final double BUG_RISK = 12.0;
    private static final double FEATURE_RISK = 20.0;
    private static final double UI_RISK = 100.0;

    private static final int NEGLIGIBLE_CAP = 25;
    private static final int MODERATE_CAP = 50;
    private static final int SIGNIFICANT_CAP = 75;

    private static final double USABILITY_PARAM = 11.0;

    public GenerateTicketRiskReportCommand(final CommandInput input, final User user) {
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

        List<Double> bugRisks = new ArrayList<>();
        List<Double> featureRisks = new ArrayList<>();
        List<Double> uiRisks = new ArrayList<>();

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

                case BUG :
                    Bug bug = (Bug) ticket;
                    bugRisks.add(calculateBugRisk(bug));
                    break;

                case FEATURE_REQUEST:
                    FeatureRequest fr = (FeatureRequest) ticket;
                    featureRisks.add(calculateFeatureRisk(fr));
                    break;

                case UI_FEEDBACK:
                    UiRequest uiRequest = (UiRequest) ticket;
                    uiRisks.add(calculateUiRisk(uiRequest));
                    break;

                default:
                    break;
            }
        }

        Map<String, String> riskByType = new LinkedHashMap<>();
        riskByType.put("BUG", getRiskLabel(calculateAverage(bugRisks)));
        riskByType.put("FEATURE_REQUEST", getRiskLabel(calculateAverage(featureRisks)));
        riskByType.put("UI_FEEDBACK", getRiskLabel(calculateAverage(uiRisks)));

        Map<String, Object> reportObject = new LinkedHashMap<>();
        reportObject.put("totalTickets", totalTickets);
        reportObject.put("ticketsByType", ticketsByType);
        reportObject.put("ticketsByPriority", ticketsByPriority);
        reportObject.put("riskByType", riskByType);

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
    private double calculateBugRisk(final Bug bug) {
        int frequency = ReportScoreDatabase.getFrequencyScore(bug.getFrequency());
        int severity = ReportScoreDatabase.getSeverityScore(bug.getSeverity());

        double rawScore = (double) frequency * severity;
        return normalize(rawScore, BUG_RISK);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param fr
     * @return
     */
    private double calculateFeatureRisk(final FeatureRequest fr) {
        int value = ReportScoreDatabase.getBusinessValueScore(fr.getBusinessValue());
        int demand = ReportScoreDatabase.getCustomerDemandScore(fr.getCustomerDemand());

        double rawScore = (double) value + demand;
        return normalize(rawScore, FEATURE_RISK);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param uiRequest
     * @return
     */
    private double calculateUiRisk(final UiRequest uiRequest) {
        int value = ReportScoreDatabase.getBusinessValueScore(uiRequest.getBusinessValue());
        int usability = uiRequest.getUsabilityScore();

        double rawScore = (USABILITY_PARAM - usability) * value;
        return normalize(rawScore, UI_RISK);
    }

    /**
     * Risk score lookup table
     * @param score
     * @return
     */
    private String getRiskLabel(final double score) {
        if (score < NEGLIGIBLE_CAP) {
            return "NEGLIGIBLE";
        } else if (score < MODERATE_CAP) {
            return "MODERATE";
        } else if (score < SIGNIFICANT_CAP) {
            return "SIGNIFICANT";
        } else {
            return "MAJOR";
        }
    }

}
