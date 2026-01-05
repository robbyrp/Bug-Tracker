package command.viewMilestones;

import main.BugTrackerSystem;
import milestone.Milestone;
import user.User;

import java.util.List;

public interface ViewMilestonesStrategy {
    /**
     * Returns the list of milestones specific to the role calling
     * @param system
     * @param user
     * @return
     */
    List<Milestone> getMilestones(BugTrackerSystem system, User user);
}
