package ticket.enums;

import user.Role;

public enum CustomerDemand {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    VERY_HIGH("VERY_HIGH"),
    UNKNOWN("UNKNOWN")
    ;
    public final String text;

    CustomerDemand(final String text) {
        this.text = text;
    }

    public static CustomerDemand fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("CustomerDemand from TicketInput does" +
                    " not match Enum Identifier");
        }
        for (CustomerDemand cd : CustomerDemand.values()) {
            if (cd.name().equalsIgnoreCase(text)) {
                return cd;
            }
        }
        return UNKNOWN;
    }
}
