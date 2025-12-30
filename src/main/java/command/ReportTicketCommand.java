package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.BusinessPriority;
import enums.Status;
import enums.TicketType;
import fileio.CommandInput;
import fileio.OutputFormatter;
import fileio.TicketInput;
import main.BugTrackerSystem;
import ticket.Ticket;
import ticket.ticketFactory.TicketFactory;
import user.User;

import java.util.List;
import java.util.Objects;

public final class ReportTicketCommand implements Command {
    private CommandInput input;
    private User user;

    public ReportTicketCommand(final CommandInput input, final User user) {
        this.input = input;
        this.user = user;
    }

    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        //TODO: CHECK PENTRU DEPASIRE PERIOADA DE TESTARE SI RAPORTARE ANONIMA
        TicketInput ticketInputParams = input.getParams();

        if (Objects.equals(ticketInputParams.getReportedBy(), "")) {
            if (!Objects.equals(ticketInputParams.getType(), "BUG")) {
                String errorMessage = "Anonymous reports are only allowed for tickets of type BUG.";
                outputs.add(OutputFormatter.createError(input.getCommand(), input.getUsername(),
                        input.getTimestamp(), errorMessage));
                return;
            }
        }

        Ticket newTicket = TicketFactory.createTicket(ticketInputParams);
        int newId = system.getTicketDatabase().getNextId();
        newTicket.setId(newId);
        newTicket.setStatus(Status.OPEN);

        if (Objects.equals(ticketInputParams.getReportedBy(), "")) {
            newTicket.setBusinessPriority(BusinessPriority.LOW);
        }
    }
}
