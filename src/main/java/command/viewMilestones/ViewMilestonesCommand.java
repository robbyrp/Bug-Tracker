package command.viewMilestones;

import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Command;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import milestone.Milestone;
import user.User;

import java.util.Comparator;
import java.util.List;

public class ViewMilestonesCommand extends Command {
    ViewMilestonesStrategy strategy;

    public ViewMilestonesCommand(final CommandInput input, final User user) {
        super(input, user);

        switch(user.getRole()) {
            case REPORTER:
                break;

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

    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        List<Milestone> milestones = strategy.getMilestones(system, getUser());

        milestones.sort(new Comparator<Milestone>() {
            @Override
            public int compare(Milestone o1, Milestone o2) {
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
                )

        );
    }
}
