package command.ReportCommands;

import command.Command;
import fileio.CommandInput;
import enums.ApplicationPhase;
import enums.Role;
import ticket.Ticket;
import user.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractReportCommand extends Command {

    protected static final double NORMALIZE_FACTOR = 100.0;

    public AbstractReportCommand(final CommandInput input, final User user) {
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
     * Returns an initialized ticketType linked hashmap
     * @return
     */
    protected Map<String, Integer> getInitializedTypeMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("BUG", 0);
        map.put("FEATURE_REQUEST", 0);
        map.put("UI_FEEDBACK", 0);
        return map;
    }

    /**
     * Returns an initialized businesspriority linked hashmap
     * @return
     */
    protected Map<String, Integer> getInitializedPriorityMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("LOW", 0);
        map.put("MEDIUM", 0);
        map.put("HIGH", 0);
        map.put("CRITICAL", 0);
        return map;
    }

    /**
     * Updates the counters for typeMap and priorityMap
     * @param ticket
     * @param typeMap
     * @param priorityMap
     */
    protected void updateCounters(final Ticket ticket,
                                  final Map<String, Integer> typeMap,
                                  final Map<String, Integer> priorityMap) {
        String ticketTypeString = ticket.getType().name();
        Integer currentCountType = typeMap.get(ticketTypeString);
        typeMap.put(ticketTypeString, currentCountType + 1);

        String ticketPriorityString = ticket.getBusinessPriority().name();
        Integer currentCountPriority = priorityMap.get(ticketPriorityString);
        priorityMap.put(ticketPriorityString, currentCountPriority + 1);
    }

    /**
     * Helper method that normalizes baseScore
     * @param baseScore
     * @param maxScore
     * @return
     */
    protected double normalize(final double baseScore, final double maxScore) {
        if (maxScore == 0) {
            return 0.0;
        }

        double normalizedByMax = (baseScore * NORMALIZE_FACTOR) / maxScore;

        double normalized = Math.min(NORMALIZE_FACTOR, normalizedByMax);

        return Math.round(normalized * NORMALIZE_FACTOR) / NORMALIZE_FACTOR;
    }

    /**
     * Helper method that calculates averages of a list
     * @param scores
     * @return
     */
    protected double calculateAverage(final List<Double> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (Double score : scores) {
            sum += score;
        }
        double avg = sum / scores.size();

        return Math.round(avg * NORMALIZE_FACTOR) / NORMALIZE_FACTOR;
    }

}
