package ticket.enums;

public enum BusinessPriority {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    CRITICAL("CRITICAL"),
    UNKNOWN("UNKNOWN")
    ;
    public final String text;
    BusinessPriority(final String text) {
        this.text = text;
    }

    public static BusinessPriority fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("BusinessPriority from TicketInput "+
                    "does not match Enum Identifier");
        }
        for (BusinessPriority bp : BusinessPriority.values()) {
            if (bp.name().equalsIgnoreCase(text)) {
                return bp;
            }
        }
        return UNKNOWN;
    }

}
