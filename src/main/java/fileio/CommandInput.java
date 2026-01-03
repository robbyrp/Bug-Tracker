package fileio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

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
    private ArrayList<String> blockingFor;
    private ArrayList<Integer> tickets;
    private ArrayList<String> assignedDevs;

    // assignTicket, addComment, changeStatus
    private Integer ticketID;

    //addComment
    private String comment;


}
