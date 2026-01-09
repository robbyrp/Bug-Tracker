package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.BusinessPriority;
import enums.Status;
import enums.Role;
import fileio.CommandInput;
import fileio.OutputFormatter;
import fileio.TicketInput;
import main.BugTrackerSystem;
import ticket.Ticket;
import ticket.ticketFactory.TicketFactory;
import user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class ReportTicketCommand extends Command {

    public ReportTicketCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.REPORTER);
    }

    @Override
    public ApplicationPhase  getRequiredPhase() {
        return ApplicationPhase.TESTING;
    }

    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        TicketInput ticketInputParams = getCommandInput().getParams();

        if (Objects.equals(ticketInputParams.getReportedBy(), "")) {
            if (!Objects.equals(ticketInputParams.getType(), "BUG")) {
                String error = "Anonymous reports are only allowed "
                        + "for tickets of type BUG.";
                outputs.add(OutputFormatter.createError(
                        getCommandInput().getCommand(),
                        getCommandInput().getUsername(),
                        getCommandInput().getTimestamp(),
                        error
                ));
                return;
            }
        }

        Ticket newTicket = TicketFactory.createTicket(ticketInputParams);

        int newId = system.getTicketDatabase().getNextId();
        newTicket.setId(newId);

        newTicket.setStatus(Status.OPEN);

        String ticketReportedTimestampString = getCommandInput().getTimestamp();
        LocalDate reportedTimestamp = LocalDate.parse(ticketReportedTimestampString);
        newTicket.setReportedTimestamp(reportedTimestamp);

        if (Objects.equals(ticketInputParams.getReportedBy(), "")) {
            newTicket.setBusinessPriority(BusinessPriority.LOW);
        }

        system.getTicketDatabase().addTicket(newTicket);
    }
}
