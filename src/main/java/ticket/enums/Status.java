package ticket.enums;

public enum Status {
    OPEN("OPEN"),
    IN_PROGRESS("IN_PROGRESS"),
    RESOLVED("RESOLVED"),
    CLOSED("CLOSED"),
    UNKNOWN("UNKNOWN")
    ;
    public final String text;

    Status(final String text) {
        this.text = text;
    }
    public static Status fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("Status from TicketInput "+
                    "does not match Enum Identifier");
        }
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(text)) {
                return s;
            }
        }
        return UNKNOWN;
    }
}
