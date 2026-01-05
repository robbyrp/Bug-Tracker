package ticket;

import lombok.Getter;

@Getter
public final class TicketAction {
    private String action;
    private String by;
    private String timestamp;

    public TicketAction(String action, String by, String timestamp) {
        this.action = action;
        this.by = by;
        this.timestamp = timestamp;
    }
}
