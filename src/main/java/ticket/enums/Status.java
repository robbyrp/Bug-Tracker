package ticket.enums;

public enum Status {
    OPEN("OPEN"),
    IN_PROGRESS("IN_PROGRESS"),
    RESOLVED("RESOLVED"),
    CLOSED("CLOSED")
    ;
    public final String text;

    Status(final String text) {
        this.text = text;
    }
}
