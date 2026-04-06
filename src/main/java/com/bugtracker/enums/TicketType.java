package com.bugtracker.enums;

public enum TicketType {
    BUG("BUG"),
    UI_FEEDBACK("UI_FEEDBACK"),
    FEATURE_REQUEST("FEATURE_REQUEST"),
    UNKNOWN("UNKNOWN");

    private final String text;

    TicketType(final String text) {
        this.text = text;
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static TicketType fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            return null;
        }
        for (TicketType t : TicketType.values()) {
            if (t.name().equalsIgnoreCase(text)) {
                return t;
            }
        }
        throw new IllegalArgumentException("TicketType from TicketInput "
                + "does not match Enum Identifier");
    }
}
