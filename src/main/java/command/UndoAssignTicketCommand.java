package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.Role;
import enums.Status;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import ticket.Ticket;
import user.User;

import java.util.List;

public final class UndoAssignTicketCommand extends  Command {

    public UndoAssignTicketCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.DEVELOPER);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }

    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        Integer ticketId = getCommandInput().getTicketID();
        Ticket ticket = system.getTicketDatabase().getTicketById(ticketId);

        if (ticket == null) {
            return;
        }

        if (!checkTicketStatus(system, outputs, ticket)) {
            return;
        }

        ticket.setStatus(Status.OPEN);
        ticket.setAssignedTo("");
        ticket.setAssignedAt(null);

        ticket.addHistoryAssign("DE-ASSIGNED",
                getUser().getUsername(),
                getCommandInput().getTimestamp()
                );

    }

    private boolean checkTicketStatus(final BugTrackerSystem system,
                                      final List<ObjectNode> outputs,
                                      final Ticket ticket) {
        if (ticket.getStatus() != Status.IN_PROGRESS) {
            outputs.add(OutputFormatter.createError(
                    getCommandInput().getCommand(),
                    getCommandInput().getUsername(),
                    getCommandInput().getTimestamp(),
                    "Only IN_PROGRESS tickets can be unassigned."
            ));
            return false;
        }
        return true;
    }
}
