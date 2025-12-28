package ticket.enums;

import user.Role;

public enum BusinessValue {
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    UNKNOWN("UNKNOWN")
    ;
    public final String text;

    BusinessValue(final String text) {
        this.text = text;
    }
    public static BusinessValue fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("BusinessValue from TicketInput "+
                    "does not match Enum Identifier");
        }
        for (BusinessValue bv : BusinessValue.values()) {
            if (bv.name().equalsIgnoreCase(text)) {
                return bv;
            }
        }
        return UNKNOWN;
    }
}
