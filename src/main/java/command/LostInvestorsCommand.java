package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.Role;
import fileio.CommandInput;
import main.BugTrackerSystem;
import user.User;

import java.util.List;

public final class LostInvestorsCommand extends Command {

    public LostInvestorsCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.MANAGER);
    }

    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        system.setActiveStatus(false);
    }
}
