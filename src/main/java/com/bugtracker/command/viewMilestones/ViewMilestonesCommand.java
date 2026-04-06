package com.bugtracker.command.viewMilestones;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugtracker.command.Command;
import com.bugtracker.fileio.CommandInput;
import com.bugtracker.fileio.OutputFormatter;
import com.bugtracker.BugTrackerSystem;
import com.bugtracker.milestone.Milestone;
import com.bugtracker.user.User;

import java.util.Comparator;
import java.util.List;

public final class ViewMilestonesCommand extends Command {
    private ViewMilestonesStrategy strategy;

    /**
     * Constructor that sets the context strategy with a switch case
     * @param input
     * @param user
     */
    public ViewMilestonesCommand(final CommandInput input, final User user) {
        super(input, user);

        switch (user.getRole()) {

            case DEVELOPER:
                this.strategy = new ViewMilestonesDeveloper();
                break;

            case MANAGER:
                this.strategy = new ViewMilestonesManager();
                break;

            default:
                break;
        }
    }

    /**
     * Execute method from command pattern that sorts the milestone list
     * @param system The engine of the program
     * @param outputs output mapper
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        List<Milestone> milestones = strategy.getMilestones(system, getUser());

        milestones.sort(new Comparator<Milestone>() {
            @Override
            public int compare(final Milestone o1, final Milestone o2) {
                int compareByDate = o1.getDueDate().compareTo(o2.getDueDate());

                if (compareByDate != 0) {
                    return compareByDate;
                }

                return o1.getName().compareTo(o2.getName());
            }
        });

        outputs.add(OutputFormatter.createListResponse(
                getCommandInput().getCommand(),
                getUser().getUsername(),
                getCommandInput().getTimestamp(),
                "milestones",
                milestones
                ));
    }
}
