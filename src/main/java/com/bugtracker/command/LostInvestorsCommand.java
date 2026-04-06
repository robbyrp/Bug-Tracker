package com.bugtracker.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.enums.Role;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.BugTrackerSystem;
import com.bugtracker.user.User;

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
