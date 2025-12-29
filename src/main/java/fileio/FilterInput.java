package fileio;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public final class FilterInput {
    private String businessPriority;
    private String type;
    private String createdAt;
    private String createdBefore;
    private String createdAfter;
    private boolean availableForAssignment;

    private String[] keywords; //TODO: Fa-l case insensitive si mai verifica cerinta
    private String expertiseArea;
    private String Seniority;
    private Integer performanceScoreAbove;
    private Integer performanceScoreBelow;
}
