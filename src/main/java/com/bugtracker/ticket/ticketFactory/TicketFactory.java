package com.bugtracker.ticket.ticketFactory;

import com.bugtracker.enums.BusinessValue;
import com.bugtracker.enums.CustomerDemand;
import com.bugtracker.enums.Frequency;
import com.bugtracker.enums.TicketType;
import com.bugtracker.enums.Status;
import com.bugtracker.enums.BusinessPriority;
import com.bugtracker.enums.ExpertiseArea;
import com.bugtracker.enums.Severity;
import com.bugtracker.fileio.TicketInput;
import com.bugtracker.ticket.Bug;
import com.bugtracker.ticket.FeatureRequest;
import com.bugtracker.ticket.Ticket;
import com.bugtracker.ticket.UiRequest;


public final class TicketFactory {
    private TicketFactory() { }

    /**
     * Builds the ticket from a TicketInput
     * @param input
     * @return
     * @throws IllegalArgumentException
     */
    public static Ticket createTicket(final TicketInput input)
        throws IllegalArgumentException {

        Ticket.Builder<?> builder = null;


        switch (input.getType()) {
            case "BUG":
                Bug.BugBuilder bugBuilder = new Bug.BugBuilder();
                bugBuilder.expectedBehavior(input.getExpectedBehavior())
                        .actualBehavior(input.getActualBehavior())
                        .frequency(Frequency.fromString(input.getFrequency()))
                        .severity(Severity.fromString(input.getSeverity()))
                        .environment(input.getEnvironment())
                        .errorCode(input.getErrorCode());
                builder = bugBuilder;
                break;

            case "FEATURE_REQUEST":
                FeatureRequest.FeatureRequestBuilder featureRequestBuilder =
                        new FeatureRequest.FeatureRequestBuilder();
                featureRequestBuilder.
                        businessValue(BusinessValue.fromString(input.getBusinessValue()))
                        .customerDemand(CustomerDemand.fromString(input.getCustomerDemand()));
                builder = featureRequestBuilder;
                break;

            case "UI_FEEDBACK":
                UiRequest.UiRequestBuilder uiRequestBuilder =
                        new UiRequest.UiRequestBuilder();
                uiRequestBuilder.businessValue(BusinessValue.fromString(input.getBusinessValue()))
                        .usabilityScore(input.getUsabilityScore())
                        .uiElementId(input.getUiElementId())
                        .screenshotUrl(input.getScreenshotUrl())
                        .suggestedFix(input.getSuggestedFix());
                builder = uiRequestBuilder;
                break;

            default:
                throw new IllegalArgumentException("Unknown ticket type" + input.getType());

        }

        populateTicketFields(builder, input);
        return builder.build();
    }

    /**
     * Populates the fields from the Ticket parent class
     * @param builder Accepts any builder that extends Ticket.Builder
     * @param input
     */
    private static void populateTicketFields(final Ticket.Builder<?> builder,
                                             final TicketInput input) {
        builder.type(TicketType.fromString(input.getType()))
                .title(input.getTitle())
                .businessPriority(BusinessPriority.fromString(input.getBusinessPriority()))
                .status(Status.OPEN)
                .expertiseArea(ExpertiseArea.fromString(input.getExpertiseArea()))
                .reportedBy(input.getReportedBy())
                .description(input.getDescription())
                .build();
    }
}
