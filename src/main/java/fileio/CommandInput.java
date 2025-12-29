package fileio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class CommandInput {
    // Common header
    private String command;
    private String username;
    private String timestamp;

    // Nested fields
    private TicketInput params;
    private FilterInput filters;

    // createMilestone
    private String name;
    private String dueDate;
    private String[] blockingFor;
    private Integer[] tickets;
    private String[] assignedDevs;

    // assignTicket, addComment, changeStatus
    //
    private Integer ticketID;

    //addComment
    private String comment;


}
