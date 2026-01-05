package ticket.ticketFactory;

import enums.*;
import fileio.TicketInput;
import ticket.Bug;
import ticket.FeatureRequest;
import ticket.Ticket;
import ticket.UiRequest;


public final class TicketFactory {
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
                featureRequestBuilder.businessValue(BusinessValue.fromString(input.getBusinessValue()))
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
    private static void populateTicketFields(final Ticket.Builder<?> builder, final TicketInput input) {
        builder.type(TicketType.fromString(input.getType()))
                .title(input.getTitle())
                .businessPriority(BusinessPriority.fromString(input.getBusinessPriority()))
                .status(Status.fromString(input.getStatus()))
                .expertiseArea(ExpertiseArea.fromString(input.getExpertiseArea()))
                .reportedBy(input.getReportedBy())
                .description(input.getDescription())
                .build();
        //TODO: ADD A WAY FOR TICKET BUILDER TO BUILD  REPORTEDTIMESTAMP MEMBER
    }
}
