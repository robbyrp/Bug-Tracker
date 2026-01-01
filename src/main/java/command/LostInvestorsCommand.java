package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.Role;
import exceptions.UserNoPermissionException;
import fileio.CommandInput;
import main.BugTrackerSystem;
import user.User;

import java.util.List;

public final class LostInvestorsCommand implements Command {
    public CommandInput input;
    public User user;

    public LostInvestorsCommand(final CommandInput input, final User users) {
        this.input = input;
        this.user = users;
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
