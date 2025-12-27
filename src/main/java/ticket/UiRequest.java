package ticket;

import ticket.enums.BusinessValue;

public final class UiRequest extends Ticket {

    private BusinessValue businessValue;
    private int usabilityScore;

    // Optional fields
    private String uiElementId;
    private String screenshotUrl;
    private String suggestedFix;

    private UiRequest(UiRequestBuilder builder) {
        super(builder);
        this.businessValue = builder.businessValue;
        this.usabilityScore = builder.usabilityScore;
    }

    public static class UiRequestBuilder extends Ticket.Builder<UiRequestBuilder> {
        private BusinessValue businessValue;
        private int usabilityScore;

        // Optional fields
        private String uiElementId;
        private String screenshotUrl;
        private String suggestedFix;

        @Override
        protected UiRequestBuilder self() {
            return this;
        }

        @Override
        public UiRequest build() {
            return new UiRequest(this);
        }

        public UiRequestBuilder businessValue(final BusinessValue businessValue) {
            this.businessValue = businessValue;
            return self();
        }

        public UiRequestBuilder usabilityScore(final int usabilityScore) {
            this.usabilityScore = usabilityScore;
            return self();
        }

        public UiRequestBuilder uiElementId(final String uiElementId) {
            this.uiElementId = uiElementId;
            return self();
        }

        public UiRequestBuilder screenshotUrl(final String screenshotUrl) {
            this.screenshotUrl = screenshotUrl;
            return self();
        }

        public UiRequestBuilder suggestedFix(final String suggestedFix) {
            this.suggestedFix = suggestedFix;
            return self();
        }
    }
}
