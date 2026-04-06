package com.bugtracker.command.search;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.command.Command;
import com.bugtracker.enums.ApplicationPhase;
import com.bugtracker.enums.Role;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.FilterInput;
import com.bugtracker.BugTrackerSystem;
import com.bugtracker.user.User;

import java.util.List;

public final class SearchCommand extends Command {
    private SearchStrategy strategy;

    public SearchCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return null;
    }
    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.MANAGER, Role.DEVELOPER);
    }

    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        FilterInput filter = getCommandInput().getFilters();
        User user = getUser();

        if (user.getRole().equals(Role.MANAGER)) {
            strategy = new ManagerSearchStrategy();
        } else {
            strategy = new DeveloperSearchStrategy();
        }

        strategy.executeSearch(system, outputs, user, getCommandInput());
    }
}
