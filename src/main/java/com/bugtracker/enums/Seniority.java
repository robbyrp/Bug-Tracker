package com.bugtracker.enums;

import java.util.Set;

public enum Seniority {
    JUNIOR(
            Set.of(BusinessPriority.LOW, BusinessPriority.MEDIUM),
            Set.of(TicketType.BUG, TicketType.UI_FEEDBACK)
    ),
    MID(
            Set.of(BusinessPriority.LOW, BusinessPriority.MEDIUM, BusinessPriority.HIGH),
            Set.of(TicketType.BUG, TicketType.UI_FEEDBACK, TicketType.FEATURE_REQUEST)
    ),
    SENIOR(
            Set.of(BusinessPriority.LOW, BusinessPriority.HIGH,
                    BusinessPriority.MEDIUM, BusinessPriority.CRITICAL),
            Set.of(TicketType.BUG, TicketType.UI_FEEDBACK, TicketType.FEATURE_REQUEST)
    ),
    UNKNOWN(
            Set.of(BusinessPriority.UNKNOWN),
            Set.of(TicketType.UNKNOWN)
    );
    private final Set<BusinessPriority> allowedPriorities;
    private final Set<TicketType> allowedTypes;

    Seniority(final Set<BusinessPriority> allowedPriorities,
              final Set<TicketType> allowedTypes) {
        this.allowedPriorities = allowedPriorities;
        this.allowedTypes = allowedTypes;
    }

    /**
     *
     * @param businessPriority
     * @return The allowed priorities
     */
    public boolean hasAccessToPriority(final BusinessPriority businessPriority) {
        return allowedPriorities.contains(businessPriority);
    }

    /**
     *
     * @param ticketType
     * @return returns the allowed types of tickets
     */
    public boolean hasAccessToTicketType(final TicketType ticketType) {
        return allowedTypes.contains(ticketType);
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static Seniority fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            return null;
        }
        for (Seniority s : Seniority.values()) {
            if (s.name().equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Seniority from UserInput"
                + " does not match Enum Identifier");
    }
}
