package user;

import ticket.enums.BusinessPriority;
import ticket.enums.ExpertiseArea;
import ticket.enums.TicketType;


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
    )

    ;
    private final Set<BusinessPriority> allowedPriorities;
    private final Set<TicketType> allowedTypes;

    Seniority(final Set<BusinessPriority> allowedPriorities,
              final Set<TicketType> allowedTypes) {
        this.allowedPriorities = allowedPriorities;
        this.allowedTypes = allowedTypes;
    }

    public boolean hasAccessToPriority(final BusinessPriority businessPriority) {
        return allowedPriorities.contains(businessPriority);
    }

    public boolean hasAccessToTicketType(final TicketType ticketType) {
        return allowedTypes.contains(ticketType);
    }

    public static Seniority fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("Seniority from UserInput does not match Enum Identifier");
        }
        for (Seniority s : Seniority.values()) {
            if (s.name().equalsIgnoreCase(text)) {
                return s;
            }
        }
        return UNKNOWN;
    }
}
