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

public class AppStabilityReportCommand extends AbstractReportCommand {

    private static final double BUG_RISK = 12.0;
    private static final double FEATURE_RISK = 20.0;
    private static final double UI_RISK = 100.0;

    private static final double BUG_IMPACT = 48.0;
    private static final double FEATURE_IMPACT = 100.0;
    private static final double UI_IMPACT = 100.0;

    private static final int NEGLIGIBLE_CAP = 25;
    private static final int MODERATE_CAP = 50;
    private static final int SIGNIFICANT_CAP = 75;

    private static final double USABILITY_PARAM = 11.0;


    public AppStabilityReportCommand(final CommandInput input, final User user) {
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
        Map<String, Integer> openTicketsByType = getInitializedTypeMap();
        Map<String, Integer> openTicketsByPriority = getInitializedPriorityMap();

        List<Double> bugRisks = new ArrayList<>();
        List<Double> featureRisks = new ArrayList<>();
        List<Double> uiRisks = new ArrayList<>();

        List<Double> bugImpacts = new ArrayList<>();
        List<Double> featureImpacts = new ArrayList<>();
        List<Double> uiImpacts = new ArrayList<>();

        int totalOpenTickets = 0;
        List<Ticket> allTickets = system.getTicketDatabase().getTickets();

        for (Ticket ticket : allTickets) {
            if (!ticket.getStatus().equals(Status.OPEN)
                    && !ticket.getStatus().equals(Status.IN_PROGRESS)) {
                continue;
            }

            totalOpenTickets++;
            updateCounters(ticket, openTicketsByType, openTicketsByPriority);

            switch (ticket.getType()) {

                case BUG:
                    Bug bug = (Bug) ticket;
                    bugRisks.add(calculateBugRisk(bug));
                    bugImpacts.add(calculateBugImpact(bug));
                    break;
                case FEATURE_REQUEST:
                    FeatureRequest fr = (FeatureRequest) ticket;
                    featureRisks.add(calculateFeatureRisk(fr));
                    featureImpacts.add(calculateFeatureImpact(fr));
                    break;
                case UI_FEEDBACK:
                    UiRequest ui = (UiRequest) ticket;
                    uiRisks.add(calculateUiRisk(ui));
                    uiImpacts.add(calculateUiImpact(ui));
                    break;

                default:
                    break;
            }
        }

        double avgBugRisk = calculateAverage(bugRisks);
        double avgFeatureRisk = calculateAverage(featureRisks);
        double avgUiRisk = calculateAverage(uiRisks);

        Map<String, String> riskByType = new LinkedHashMap<>();
        riskByType.put("BUG", getRiskLabel(avgBugRisk));
        riskByType.put("FEATURE_REQUEST", getRiskLabel(avgFeatureRisk));
        riskByType.put("UI_FEEDBACK", getRiskLabel(avgUiRisk));

        double avgBugImpact = calculateAverage(bugImpacts);
        double avgFeatureImpact = calculateAverage(featureImpacts);
        double avgUiImpact = calculateAverage(uiImpacts);

        Map<String, Double> impactByType = new LinkedHashMap<>();
        impactByType.put("BUG", avgBugImpact);
        impactByType.put("FEATURE_REQUEST", avgFeatureImpact);
        impactByType.put("UI_FEEDBACK", avgUiImpact);

        String stabilityStatus = determineStability(totalOpenTickets, riskByType, impactByType);

        Map<String, Object> reportObject = new LinkedHashMap<>();
        reportObject.put("totalOpenTickets", totalOpenTickets);
        reportObject.put("openTicketsByType", openTicketsByType);
        reportObject.put("openTicketsByPriority", openTicketsByPriority);
        reportObject.put("riskByType", riskByType);
        reportObject.put("impactByType", impactByType);
        reportObject.put("appStability", stabilityStatus);

        outputs.add(OutputFormatter.createReportResponse(
                getCommandInput().getCommand(),
                getCommandInput().getUsername(),
                getCommandInput().getTimestamp(),
                "report",
                reportObject
        ));

        if (stabilityStatus.equals("STABLE")) {
            system.setActiveStatus(false);
        }
    }

    /**
     * Determines stbility based on restrictions
     * Using functional programming
     * @param totalTickets
     * @param risks
     * @param impacts
     * @return
     */
    private String determineStability(final int totalTickets, final Map<String,
            String> risks, final Map<String, Double> impacts) {
        if (totalTickets == 0) {
            return "STABLE";
        }

        boolean allRisksNegligible = risks.values().stream()
                .allMatch(label -> label.equals("NEGLIGIBLE"));

        boolean allImpactsLow = impacts.values().stream()
                .allMatch(score -> score < MODERATE_CAP);

        boolean anyRiskSignificantOrMajor = risks.values().stream()
                .anyMatch(label -> label.equals("SIGNIFICANT") || label.equals("MAJOR"));

        if (allRisksNegligible && allImpactsLow) {
            return "STABLE";
        }

        if (anyRiskSignificantOrMajor) {
            return "UNSTABLE";
        }

        return "PARTIALLY STABLE";
    }

    /**
     * Risk score lookup table
     * @param score
     * @return
     */
    private String getRiskLabel(final double score) {
        if (score < NEGLIGIBLE_CAP) {
            return "NEGLIGIBLE";
        }
        if (score < MODERATE_CAP) {
            return "MODERATE";
        }
        if (score < SIGNIFICANT_CAP) {
            return "SIGNIFICANT";
        }
        return "MAJOR";
    }


    /**
     * Applies formula to calculate specific parameter
     * @param bug
     * @return
     */
    private double calculateBugRisk(final Bug bug) {
        int frequency = ReportScoreDatabase.getFrequencyScore(bug.getFrequency());
        int severity = ReportScoreDatabase.getSeverityScore(bug.getSeverity());
        return normalize((double) frequency * severity, BUG_RISK);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param fr
     * @return
     */
    private double calculateFeatureRisk(final FeatureRequest fr) {
        int value = ReportScoreDatabase.getBusinessValueScore(fr.getBusinessValue());
        int demand = ReportScoreDatabase.getCustomerDemandScore(fr.getCustomerDemand());
        return normalize((double) value + demand, FEATURE_RISK);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param ui
     * @return
     */
    private double calculateUiRisk(final UiRequest ui) {
        int value = ReportScoreDatabase.getBusinessValueScore(ui.getBusinessValue());
        int usability = ui.getUsabilityScore();
        return normalize((USABILITY_PARAM - usability) * value, UI_RISK);
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
        return normalize((double) frequency * priority * severity, BUG_IMPACT);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param fr
     * @return
     */
    private double calculateFeatureImpact(final FeatureRequest fr) {
        int value = ReportScoreDatabase.getBusinessValueScore(fr.getBusinessValue());
        int demand = ReportScoreDatabase.getCustomerDemandScore(fr.getCustomerDemand());
        return normalize((double) value * demand, FEATURE_IMPACT);
    }

    /**
     * Applies formula to calculate specific parameter
     * @param ui
     * @return
     */
    private double calculateUiImpact(final UiRequest ui) {
        int value = ReportScoreDatabase.getBusinessValueScore(ui.getBusinessValue());
        int usability = ui.getUsabilityScore();
        return normalize((double) value * usability, UI_IMPACT);
    }
}
