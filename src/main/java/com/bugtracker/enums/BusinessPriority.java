package com.bugtracker.enums;

import lombok.Getter;

public enum BusinessPriority {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    CRITICAL("CRITICAL"),
    UNKNOWN("UNKNOWN");
    @Getter
    private final String text;
    BusinessPriority(final String text) {
        this.text = text;
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static BusinessPriority fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            return null;
        }
        for (BusinessPriority bp : BusinessPriority.values()) {
            if (bp.name().equalsIgnoreCase(text)) {
                return bp;
            }
        }
        throw new IllegalArgumentException("BusinessPriority from TicketInput "
                + "does not match Enum Identifier");
    }

}
