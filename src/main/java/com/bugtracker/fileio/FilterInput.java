package com.bugtracker.fileio;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

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

    // Manager filters: all of the above and
    private ArrayList<String> keywords;
    private String expertiseArea;
    private String seniority;
    private Integer performanceScoreAbove;
    private Integer performanceScoreBelow;
}
