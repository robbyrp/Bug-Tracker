package fileio;

import enums.Status;
import lombok.Getter;
import ticket.Comment;
import ticket.Ticket;
import ticket.TicketAction;

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
