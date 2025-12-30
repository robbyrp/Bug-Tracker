package enums;

import lombok.Getter;

public enum Frequency {
    RARE("RARE"),
    OCCASIONAL("OCCASIONAL"),
    FREQUENT("FREQUENT"),
    ALWAYS("ALWAYS"),
    UNKNOWN("UNKNOWN");

    @Getter
    private final String text;

    Frequency(final String text) {
        this.text = text;
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static Frequency fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("Frequency from TicketInput "
                    + "does not match Enum Identifier");
        }
        for (Frequency f : Frequency.values()) {
            if (f.name().equalsIgnoreCase(text)) {
                return f;
            }
        }
        return UNKNOWN;
    }
}
