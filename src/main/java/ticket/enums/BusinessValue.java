package ticket.enums;

public enum BusinessValue {
    S("S"),
    M("M"),
    L("L"),
    XL("XL")
    ;
    public final String text;

    BusinessValue(String text) {
        this.text = text;
    }
}
