package com.bugtracker.command.viewMilestones;

import com.bugtracker.BugTrackerSystem;
import com.bugtracker.milestone.Milestone;
import com.bugtracker.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ViewMilestonesDeveloper implements ViewMilestonesStrategy {

    /**
     * A developer only sees the milestones which he is assigned to
     * @param system
     * @param user
     * @return
     */
    @Override
    public List<Milestone> getMilestones(final BugTrackerSystem system, final User user) {
        List<Milestone> milestones =  system.getMilestoneDatabase().getMilestoneList();

        return milestones.stream()
                .filter(m -> m.getAssignedDevs().contains(user.getUsername()))
                .collect(Collectors.toList());
    }
}
