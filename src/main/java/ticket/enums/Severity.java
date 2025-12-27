package ticket.enums;

public enum Severity {
    MINOR("MINOR"),
    MODERATE("MODERATE"),
    SEVERE("SEVERE")
    ;
    public String text;
    Severity(final String text) {
        this.text = text;
    }
}
