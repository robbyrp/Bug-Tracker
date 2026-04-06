package com.bugtracker.fileio;

import com.bugtracker.enums.Status;
import lombok.Getter;
import com.bugtracker.ticket.Comment;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.ticket.TicketAction;

import java.util.List;

@Getter
public final class TicketHistoryDTO {
    private final Integer id;
    private final String title;
    private final Status status;
    private final List<TicketAction> actions;
    private final List<Comment> comments;

    public TicketHistoryDTO(final Ticket t) {
        this.id = t.getId();
        this.title = t.getTitle();
        this.status = t.getStatus();
        this.actions = t.getHistory();
        this.comments = t.getComments();
    }
}
