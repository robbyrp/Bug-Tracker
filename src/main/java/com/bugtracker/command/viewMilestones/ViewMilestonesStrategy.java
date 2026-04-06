package com.bugtracker.command.viewMilestones;

import com.bugtracker.BugTrackerSystem;
import com.bugtracker.milestone.Milestone;
import com.bugtracker.user.User;

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
