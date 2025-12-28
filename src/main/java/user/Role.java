package user;

public enum Role {
    REPORTER("REPORTER"),
    DEVELOPER("DEVELOPER"),
    MANAGER("MANAGER"),
    UNKNOWN("UNKNOWN")
    ;
    public final String text;

    Role(final String text) {
        this.text = text;
    }

    public static Role fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("Role from UserInput does not match Enum Identifier");
        }
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(text)) {
                return r;
            }
        }
        return UNKNOWN;
    }
}
