package command.ReportCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Command;
import enums.ApplicationPhase;
import enums.Role;
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

    private static final double MAX_BUG_RISK = 12.0;
    private static final double MAX_FEATURE_RISK = 20.0;
    private static final double MAX_UI_RISK = 100.0;

    public GenerateTicketRiskReportCommand(CommandInput input, User user) {
        super(input, user);
    }

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


    private double calculateBugRisk(final Bug bug) {
        int freq = ReportScoreDatabase.getFrequencyScore(bug.getFrequency());
        int sev = ReportScoreDatabase.getSeverityScore(bug.getSeverity());

        double rawScore = (double) freq * sev;
        return normalize(rawScore, MAX_BUG_RISK);
    }

    private double calculateFeatureRisk(final FeatureRequest fr) {
        int val = ReportScoreDatabase.getBusinessValueScore(fr.getBusinessValue());
        int demand = ReportScoreDatabase.getCustomerDemandScore(fr.getCustomerDemand());

        double rawScore = (double) val + demand;
        return normalize(rawScore, MAX_FEATURE_RISK);
    }

    private double calculateUiRisk(final UiRequest uiRequest) {
        int val = ReportScoreDatabase.getBusinessValueScore(uiRequest.getBusinessValue());
        int usability = uiRequest.getUsabilityScore();

        double rawScore = (11.0 - usability) * val;
        return normalize(rawScore, MAX_UI_RISK);
    }

    private String getRiskLabel(final double score) {
        if (score < 25.0) {
            return "NEGLIGIBLE";
        } else if (score < 50.0) {
            return "MODERATE";
        } else if (score < 75.0) {
            return "SIGNIFICANT";
        } else {
            return "MAJOR";
        }
    }

}
