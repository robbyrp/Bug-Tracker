package com.bugtracker.milestone;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.bugtracker.enums.MilestoneStatus;
import com.bugtracker.fileio.CommandInput;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;

@JsonPropertyOrder({
        "name", "blockingFor", "dueDate", "createdAt", "tickets", "assignedDevs",
        "createdBy", "status", "isBlocked", "daysUntilDue", "overdueBy",
        "openTickets", "closedTickets", "completionPercentage", "repartition"
})
@Getter @Setter
public final class Milestone {
    private String name;
    private LocalDate dueDate;
    private ArrayList<String> blockingFor;
    private ArrayList<Integer> tickets;
    private ArrayList<String> assignedDevs;
    private String createdBy;

    private LocalDate createdAt;
    private MilestoneStatus status;
    @Getter(AccessLevel.NONE)
    private boolean isBlocked;
    private Integer daysUntilDue;
    private Integer overdueBy;
    private ArrayList<Integer> openTickets;
    private ArrayList<Integer> closedTickets;
    private Double completionPercentage;
    private ArrayList<Repartition> repartition;

    @JsonIgnore
    private LocalDate lastPriorityUpdateDate;

    @JsonIgnore
    private static final double MAX_PERCENTAGE = 100.0;
    @JsonIgnore
    private static final int DAYS_BETWEEN_UPDATES = 3;

    @JsonProperty("isBlocked")
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     * Constructs fields from input and safely initializes
     * the other fields. Sets the isBlocked status for milestones in
     * blockedFor field
     * @param commandInput
     */
    public Milestone(final CommandInput commandInput, final MilestoneDatabase milestoneDatabase) {
        this.name = commandInput.getName();
        this.dueDate = LocalDate.parse(commandInput.getDueDate());

        this.blockingFor = commandInput.getBlockingFor();

        this.tickets = commandInput.getTickets();
        this.assignedDevs = commandInput.getAssignedDevs();
        this.createdBy = commandInput.getUsername();
        this.createdAt = LocalDate.parse(commandInput.getTimestamp());
        this.lastPriorityUpdateDate = this.createdAt;

        this.status = MilestoneStatus.ACTIVE;
        this.openTickets = new ArrayList<>(tickets);
        this.closedTickets = new ArrayList<>();
        this.completionPercentage = 0.0;
        this.isBlocked = false;

        this.repartition = new ArrayList<>();
        for (String developerUsername : assignedDevs) {
            this.repartition.add(new Repartition(developerUsername));
        }

        if (this.blockingFor != null && !this.blockingFor.isEmpty()) {
            for (String blockedMilestoneName : this.blockingFor) {
                Milestone blocked = milestoneDatabase.getMilestoneByName(blockedMilestoneName);
                if (blocked != null) {
                    blocked.setBlocked(true);
                }
            }
        }
    }

}
