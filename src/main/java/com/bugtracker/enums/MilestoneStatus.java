package com.bugtracker.enums;

public enum MilestoneStatus {
    ACTIVE("ACTIVE"),
    COMPLETED("COMPLETED"),
    UNKNOWN("UNKNOWN");

    private final String text;

    MilestoneStatus(final String text) {
        this.text = text;
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static MilestoneStatus fromString(final String text) {
        if (text == null) {
            return null;
        }
        for (MilestoneStatus ms : MilestoneStatus.values()) {
            if (ms.name().equalsIgnoreCase(text)) {
                return ms;
            }
        }
        return UNKNOWN;
    }
}
