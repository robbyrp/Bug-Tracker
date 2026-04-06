package com.bugtracker.milestone;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MilestoneDatabase {
    private static MilestoneDatabase instance;
    @Getter
    private List<Milestone> milestoneList = new ArrayList<>();

    /**
     * Singleton getInstance method
     * @return
     */
    public static MilestoneDatabase getInstance() {
        if (instance == null) {
            return new MilestoneDatabase();
        }
        return instance;
    }
    private MilestoneDatabase() { }

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

    /**
     * Returns the milestone that contains the ticket with ticketId;
     * Returns null if the ticket with ticketId is not in any milestone
     * @param ticketId
     * @return
     */
    public Milestone getMilestoneByTicketId(final Integer ticketId) {
        for (Milestone m : milestoneList) {
            ArrayList<Integer> ticketsInMilestone = m.getTickets();
            if (ticketsInMilestone.contains(ticketId)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Returns the list of milestones with the names in the list parameters
     * @param names
     * @return
     */
    public List<Milestone> getMilestonesByNames(final List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }

        List<Milestone> milestones = new ArrayList<>();

        for (String name : names) {
            Milestone milestone = getMilestoneByName(name);
            if (milestone != null) {
                milestones.add(milestone);
            }
        }
        return milestones;
    }

}
