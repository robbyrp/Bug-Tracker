package com.bugtracker.command.factory;

import com.bugtracker.command.*;
import com.bugtracker.command.reportCommands.*;
import com.bugtracker.command.*;
import com.bugtracker.command.addComment.AddCommentCommand;
import com.bugtracker.command.search.SearchCommand;
import com.bugtracker.command.viewMilestones.ViewMilestonesCommand;
import com.bugtracker.command.viewTicketHistory.ViewTicketHistoryCommand;
import com.bugtracker.command.viewTickets.ViewTicketsCommand;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.user.User;

public final class CommandFactory {
    private CommandFactory() { }

    /**
     * Factory method that returns the correct Command class
     * @param input
     * @param user
     * @return
     */
    public static Command getCommand(final CommandInput input, final User user) {
        return switch (input.getCommand()) {
            case "reportTicket" -> new ReportTicketCommand(input, user);
            case "viewTickets" -> new ViewTicketsCommand(input, user);
            case "createMilestone" -> new CreateMilestoneCommand(input, user);
            case "viewMilestones" -> new ViewMilestonesCommand(input, user);
            case "assignTicket" -> new AssignTicketCommand(input, user);
            case "undoAssignTicket" -> new UndoAssignTicketCommand(input, user);
            case "viewAssignedTickets" -> new ViewAssignedTicketsCommand(input, user);
            case "addComment" -> new AddCommentCommand(input, user);
            case "undoAddComment" -> new UndoAddCommentCommand(input, user);
            case "changeStatus" -> new ChangeStatusCommand(input, user);
            case "undoChangeStatus" -> new UndoChangeStatusCommand(input, user);
            case "viewTicketHistory" -> new ViewTicketHistoryCommand(input, user);
            case "viewNotifications" -> new ViewNotificationsCommand(input, user);
            case "lostInvestors" -> new LostInvestorsCommand(input, user);
            case "generateCustomerImpactReport" -> new CustomerImpactReportCommand(input, user);
            case "generateTicketRiskReport" -> new GenerateTicketRiskReportCommand(input, user);
            case "generateResolutionEfficiencyReport" ->
                    new ResolutionEfficiencyReportCommand(input, user);
            case "appStabilityReport" -> new AppStabilityReportCommand(input, user);
            case "generatePerformanceReport" -> new PerformanceReportCommand(input, user);
            case "search" -> new SearchCommand(input, user);
            case "startTestingPhase" -> new StartTestingPhaseCommand(input, user);
            default -> throw new IllegalArgumentException("Unknown command " + input.getCommand());
        };
    }
}
