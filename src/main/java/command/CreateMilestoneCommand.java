package command;
import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.Role;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import milestone.Milestone;
import user.User;

import java.util.List;

public final class CreateMilestoneCommand extends Command{

    public CreateMilestoneCommand(CommandInput input, User user) {
        super(input, user);
    }

    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        List<Integer> requestedTickets = getCommandInput().getTickets();

        for (Integer ticketId : requestedTickets) {
            Milestone ticketOwner = system.getMilestoneDatabase().getMilestoneByTicketId(ticketId);

            if (ticketOwner == null) {
                continue;
            }

            String error = "Tickets " + ticketId + " already assigned to milestone " + ticketOwner.getName() + ".";
            outputs.add(OutputFormatter.createError(
                    getCommandInput().getCommand(),
                    getCommandInput().getUsername(),
                    getCommandInput().getTimestamp(),
                    error
            ));
            return;
        }

        Milestone newMilestone = new Milestone(getCommandInput(), system.getMilestoneDatabase());

        system.getMilestoneDatabase().addMilestone(newMilestone);

    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }
    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.MANAGER);
    }
}
