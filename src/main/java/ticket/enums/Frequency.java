package ticket.enums;

public enum Frequency {
    RARE("RARE"),
    OCCASIONAL("OCCASIONAL"),
    FREQUENT("FREQUENT"),
    ALWAYS("ALWAYS"),
    UNKNOWN("UNKNOWN")
    ;
    public final String text;

    Frequency(final String text) {
        this.text = text;
    }
    public static Frequency fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("Frequency from TicketInput "+
                    "does not match Enum Identifier");
        }
        for (Frequency f : Frequency.values()) {
            if (f.name().equalsIgnoreCase(text)) {
                return f;
            }
        }
        return UNKNOWN;
    }
}
