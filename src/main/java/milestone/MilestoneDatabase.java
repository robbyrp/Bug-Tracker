package milestone;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MilestoneDatabase {
    @Getter
    private List<Milestone> milestoneList = new ArrayList<>();

    /**
     * Adds a milestone to the list
     * @param milestone
     */
    public void addMilestone(final Milestone milestone) {
        milestoneList.add(milestone);
    }

    /**
     * Removes a milestone from the list
     * @param milestone
     */
    public void removeMilestone(final Milestone milestone) {
        milestoneList.remove(milestone);
    }

    /**
     * Returns the milestone whose name is given as a parameter.
     * Returns null if the parameter name does not match
     * @param name
     * @return
     */
    public Milestone getMilestoneByName(final String name) {
        for (Milestone m : milestoneList) {
            if (Objects.equals(m.getName(), name)) {
                return m;
            }
        }
        return null;
    }




}
