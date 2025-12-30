package enums;

import lombok.Getter;

public enum Role {
    REPORTER("REPORTER"),
    DEVELOPER("DEVELOPER"),
    MANAGER("MANAGER"),
    UNKNOWN("UNKNOWN");

    @Getter
    private final String text;

    Role(final String text) {
        this.text = text;
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static Role fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("Role from UserInput "
                    + "does not match Enum Identifier");
        }
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(text)) {
                return r;
            }
        }
        return UNKNOWN;
    }
}
