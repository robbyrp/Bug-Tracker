package ticket.enums;

public enum Frequency {
    RARE("RARE"),
    OCCASIONAL("OCCASIONAL"),
    FREQUENT("FREQUENT"),
    ALWAYS("ALWAYS")
    ;
    public final String text;

    Frequency(final String text) {
        this.text = text;
    }
}
