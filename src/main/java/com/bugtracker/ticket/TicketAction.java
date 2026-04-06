package com.bugtracker.ticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public final class TicketAction {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String milestone;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String from;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String to;

    private String by;
    private String timestamp;
    private String action;


    /**
     * Constructs an entry in the history list for Assign operations
     * @param action
     * @param by
     * @param timestamp
     */
    public TicketAction(final String action, final String by, final String timestamp) {
        this.action = action;
        this.by = by;
        this.timestamp = timestamp;
    }


    /**
     * Constructs an entry in the history list for Status Change operations
     * @param action
     * @param from
     * @param to
     * @param by
     * @param timestamp
     */
    public TicketAction(final String action, final String from, final String to,
                        final String by, final String timestamp) {
        this.action = action;
        this.from = from;
        this.to = to;
        this.by = by;
        this.timestamp = timestamp;
    }

    /**
     * Constructs an entry in the history list for Added to Milestone operations
     * @param action
     * @param milestone
     * @param by
     * @param timestamp
     */
    public TicketAction(final String action, final String milestone,
                        final String by, final String timestamp) {
        this.action = action;
        this.milestone = milestone;
        this.by = by;
        this.timestamp = timestamp;
    }
}
