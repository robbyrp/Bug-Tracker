package ticket.enums;

import java.util.Set;

public enum ExpertiseArea {
    FRONTEND(Set.of("FRONTEND", "DESIGN")),
    BACKEND(Set.of("BACKEND", "DB")),
    FULLSTACK(Set.of("FRONTEND", "BACKEND", "DEVOPS", "DESIGN", "DB")),
    DEVOPS(Set.of("DEVOPS")),
    DESIGN(Set.of("DESIGN", "FRONTEND")),
    DB(Set.of("DB")),
    UNKNOWN(Set.of("UNKNOWN"))
    ;

    public final Set<String> accessibleZones;

    ExpertiseArea(final Set<String> accessibleZones) {
        this.accessibleZones = accessibleZones;
    }

    public boolean hasAccessToZone(final String zone) {
        return accessibleZones.contains(zone);
    }

    public static ExpertiseArea fromString(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Role from UserInput does not match Enum Identifier");
        }

        for (ExpertiseArea ea : ExpertiseArea.values()) {
            if (ea.name().equalsIgnoreCase(text)) {
                return ea;
            }
        }
        return UNKNOWN;
    }
}
