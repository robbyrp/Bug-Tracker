package enums;

public enum Severity {
    MINOR("MINOR"),
    MODERATE("MODERATE"),
    SEVERE("SEVERE"),
    UNKNOWN("UNKNOWN");

    private final String text;
    Severity(final String text) {
        this.text = text;
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static Severity fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("Severity from TicketInput "
                    + "does not match Enum Identifier");
        }
        for (Severity s : Severity.values()) {
            if (s.name().equalsIgnoreCase(text)) {
                return s;
            }
        }
        return UNKNOWN;
    }
}
