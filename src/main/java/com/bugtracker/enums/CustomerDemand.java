package com.bugtracker.enums;

import lombok.Getter;

public enum CustomerDemand {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    VERY_HIGH("VERY_HIGH"),
    UNKNOWN("UNKNOWN");
    @Getter
    private final String text;

    CustomerDemand(final String text) {
        this.text = text;
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static CustomerDemand fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            return null;
        }
        for (CustomerDemand cd : CustomerDemand.values()) {
            if (cd.name().equalsIgnoreCase(text)) {
                return cd;
            }
        }
        throw new IllegalArgumentException("CustomerDemand from TicketInput does"
                + " not match Enum Identifier");
    }
}
