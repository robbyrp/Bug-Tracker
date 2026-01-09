package fileio;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public final class FilterInput {
    private String searchType;
    // Dev filters
    private String businessPriority;
    private String type;
    private String createdAt;
    private String createdBefore;
    private String createdAfter;
    private boolean availableForAssignment;

    // Manager filters: all of the above +
    private String[] keywords; // este case-insensitive și caută potrivirea parțială sau completă a cel puțin un cuvânt în description sau title.
    private String expertiseArea;
    private String seniority;
    private Integer performanceScoreAbove;
    private Integer performanceScoreBelow;
}
