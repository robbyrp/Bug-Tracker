package ticket.enums;

public enum BusinessPriority {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    CRITICAL("CRITICAL")
    ;
    public final String text;
    /**
     * @param text
     */
    BusinessPriority(final String text) {
        this.text = text;
    }

}
