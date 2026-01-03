package command;
import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.Role;
import fileio.CommandInput;
import main.BugTrackerSystem;
import user.User;

import java.util.List;

public final class CreateMilestoneCommand extends Command{

    public CreateMilestoneCommand(CommandInput input, User user) {
        super(input, user);
    }

    @Override
    public void execute(BugTrackerSystem system, List<ObjectNode> outputs) {
        return;
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
