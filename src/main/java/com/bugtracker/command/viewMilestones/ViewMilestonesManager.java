package com.bugtracker.command.viewMilestones;

import com.bugtracker.BugTrackerSystem;
import com.bugtracker.milestone.Milestone;
import com.bugtracker.user.User;

import java.util.List;
import java.util.stream.Collectors;

public final class ViewMilestonesManager implements ViewMilestonesStrategy {
    /**
     * A manager can only see the milestones he created
     */
    @Override
    public List<Milestone> getMilestones(final BugTrackerSystem system, final User user) {
        List<Milestone> milestones =  system.getMilestoneDatabase().getMilestoneList();

        return milestones.stream()
                .filter(m -> m.getCreatedBy().equals(user.getUsername()))
                .collect(Collectors.toList());
    }
}
