package enums;

import lombok.Getter;

public enum BusinessValue {
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    UNKNOWN("UNKNOWN");
    @Getter
    private final String text;

    BusinessValue(final String text) {
        this.text = text;
    }

    /**
     * Matches parameter to enum identifier
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public static BusinessValue fromString(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("BusinessValue from TicketInput "
                    + "does not match Enum Identifier");
        }
        for (BusinessValue bv : BusinessValue.values()) {
            if (bv.name().equalsIgnoreCase(text)) {
                return bv;
            }
        }
        return UNKNOWN;
    }
}
