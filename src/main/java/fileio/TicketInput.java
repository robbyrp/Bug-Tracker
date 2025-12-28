package fileio;

import lombok.Getter;
import lombok.Setter;
import ticket.enums.*;

@Setter @Getter
public class TicketInput {
    private int id;
    private  String type;
    private String title;
    private String businessPriority;
    private String status;
    private String expertiseArea;
    private String reportedBy;
    private String description;

    private String expectedBehavior;
    private String actualBehavior;
    private String frequency;
    private String severity;
    private String environment;
    private Integer errorCode;

    private String businessValue;
    private int usabilityScore;
    private String uiElementId;
    private String screenshotUrl;
    private String suggestedFix;

    private String customerDemand;

}
