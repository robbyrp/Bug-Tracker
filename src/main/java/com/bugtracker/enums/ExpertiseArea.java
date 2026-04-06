package com.bugtracker.enums;

import lombok.Getter;

import java.util.Set;

public enum ExpertiseArea {
    FRONTEND(Set.of("FRONTEND", "DESIGN")),
    BACKEND(Set.of("BACKEND", "DB")),
    FULLSTACK(Set.of("FRONTEND", "BACKEND", "DEVOPS", "DESIGN", "DB")),
    DEVOPS(Set.of("DEVOPS")),
    DESIGN(Set.of("DESIGN", "FRONTEND")),
    DB(Set.of("DB")),
    UNKNOWN(Set.of("UNKNOWN"));

    @Getter
    private final Set<String> accessibleZones;

    ExpertiseArea(final Set<String> accessibleZones) {
        this.accessibleZones = accessibleZones;
    }

    /**
     *
     * @param zone
     * @return Accesible zones for devs
     */
    public boolean hasAccessToZone(final String zone) {
        return accessibleZones.contains(zone);
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static ExpertiseArea fromString(final String text) {
        if (text == null) {
            return null;
        }

        for (ExpertiseArea ea : ExpertiseArea.values()) {
            if (ea.name().equalsIgnoreCase(text)) {
                return ea;
            }
        }
        throw new IllegalArgumentException("Role from UserInput does"
                + " not match Enum Identifier");
    }
}
