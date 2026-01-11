package ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.BusinessValue;
import lombok.Getter;

@Getter
public final class UiRequest extends Ticket {
    @JsonIgnore
    private BusinessValue businessValue;
    @JsonIgnore
    private int usabilityScore;

    // Optional fields
    @JsonIgnore
    private String uiElementId;
    @JsonIgnore
    private String screenshotUrl;
    @JsonIgnore
    private String suggestedFix;

    private UiRequest(final UiRequestBuilder builder) {
        super(builder);
        this.businessValue = builder.businessValue;
        this.usabilityScore = builder.usabilityScore;
    }

    public static final class UiRequestBuilder extends Ticket.Builder<UiRequestBuilder> {
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

        /**
         *
         * @param businessValues
         * @return
         */
        public UiRequestBuilder businessValue(final BusinessValue businessValues) {
            this.businessValue = businessValues;
            return self();
        }

        /**
         *
         * @param usabilityScores
         * @return
         */
        public UiRequestBuilder usabilityScore(final int usabilityScores) {
            this.usabilityScore = usabilityScores;
            return self();
        }

        /**
         *
         * @param uiElementIds
         * @return
         */
        public UiRequestBuilder uiElementId(final String uiElementIds) {
            this.uiElementId = uiElementIds;
            return self();
        }

        /**
         *
         * @param screenshotUrls
         * @return
         */
        public UiRequestBuilder screenshotUrl(final String screenshotUrls) {
            this.screenshotUrl = screenshotUrls;
            return self();
        }

        /**
         *
         * @param suggestedFixs
         * @return
         */
        public UiRequestBuilder suggestedFix(final String suggestedFixs) {
            this.suggestedFix = suggestedFixs;
            return self();
        }

    }
}
