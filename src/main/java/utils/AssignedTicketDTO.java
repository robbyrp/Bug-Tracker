package utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import enums.BusinessPriority;
import enums.Status;
import enums.TicketType;
import lombok.Getter;
import ticket.Comment;
import ticket.Ticket;

import java.time.LocalDate;
import java.util.List;

@Getter
@JsonPropertyOrder({
        "id", "type", "title", "businessPriority", "status",
        "createdAt", "assignedAt", "reportedBy", "comments"
})
public final class AssignedTicketDTO {
    private final Integer id;
    private final TicketType type;
    private final String title;
    private final BusinessPriority businessPriority;
    private final Status status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate assignedAt;

    private final String reportedBy;
    private final List<Comment> comments;

    public AssignedTicketDTO(Ticket t) {
        this.id = t.getId();
        this.type = t.getType();
        this.title = t.getTitle();
        this.businessPriority = t.getBusinessPriority();
        this.status = t.getStatus();
        this.createdAt = t.getReportedTimestamp();
        this.assignedAt = t.getAssignedAt();
        this.reportedBy = t.getReportedBy();
        this.comments = t.getComments();
    }
}
