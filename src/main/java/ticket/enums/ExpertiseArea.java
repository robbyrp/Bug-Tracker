package ticket.enums;

public enum ExpertiseArea {
    FRONTEND("FRONTEND"),
    BACKEND("BACKEND"),
    DEVOPS("DEVOPS"),
    DESIGN("DESIGN"),
    DB("DB")
    ;

    public final String text;

    ExpertiseArea(final String text) {
        this.text = text;
    }
}
