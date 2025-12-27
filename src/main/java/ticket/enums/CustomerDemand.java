package ticket.enums;

public enum CustomerDemand {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    VERY_HIGH("VERY_HIGH")
    ;
    public final String text;

    CustomerDemand(String text) {
        this.text = text;
    }
}
