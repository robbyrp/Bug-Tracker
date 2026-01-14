package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.MilestoneStatus;
import enums.Role;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import milestone.Milestone;
import user.User;

import java.time.LocalDate;
import java.util.List;

public final class StartTestingPhaseCommand extends Command {
    public StartTestingPhaseCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.MANAGER);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return null;
    }

    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {

        LocalDate currentDate = LocalDate.parse(getCommandInput().getTimestamp());

        system.getMilestoneManager().updateAllMilestones(
                system.getMilestoneDatabase(),
                system.getTicketDatabase(),
                system.getUserDatabase(),
                currentDate
        );

        for (Milestone milestone : system.getMilestoneDatabase().getMilestoneList()) {
            if (milestone.getStatus().equals(MilestoneStatus.ACTIVE)) {
                String error = "Cannot start a new testing phase.";
                outputs.add(OutputFormatter.createError(
                        getCommandInput().getCommand(),
                        getCommandInput().getUsername(),
                        getCommandInput().getTimestamp(),
                        error
                ));
                return;
            }
        }

        String timestamp = getCommandInput().getTimestamp();
        system.getDateManager().startNewTestingPhase(timestamp);

    }
}
