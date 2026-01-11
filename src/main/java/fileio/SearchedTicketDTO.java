package fileio;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import enums.BusinessPriority;
import enums.Status;
import enums.TicketType;
import lombok.Getter;
import ticket.Ticket;

import java.time.LocalDate;

@Getter
@JsonPropertyOrder({
        "id", "type", "title", "businessPriority", "status",
        "createdAt", "solvedAt", "reportedBy"
})
public final class SearchedTicketDTO {
    private final Integer id;
    private final TicketType type;
    private final String title;
    private final BusinessPriority businessPriority;
    private final Status status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(nullsUsing = Ticket.EmptyStringSerializer.class)
    private final LocalDate solvedAt;
    private final String reportedBy;

    public SearchedTicketDTO(final Ticket t) {
        this.id = t.getId();
        this.type = t.getType();
        this.title = t.getTitle();
        this.businessPriority = t.getBusinessPriority();
        this.status = t.getStatus();
        this.createdAt = t.getReportedTimestamp();
        this.solvedAt = t.getSolvedAt();
        this.reportedBy = t.getReportedBy();
    }
}
