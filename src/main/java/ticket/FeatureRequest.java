package ticket;

import ticket.enums.BusinessValue;
import ticket.enums.CustomerDemand;

public final class FeatureRequest extends Ticket {

    private BusinessValue businessValue;
    private CustomerDemand customerDemand;

    private FeatureRequest(FeatureRequestBuilder builder) {
        super(builder);
        this.businessValue = builder.businessValue;
        this.customerDemand = builder.customerDemand;
    }

    public static class FeatureRequestBuilder extends Ticket.Builder<FeatureRequestBuilder> {
        private BusinessValue businessValue;
        private CustomerDemand customerDemand;

        @Override
        protected FeatureRequestBuilder self() {
            return this;
        }

        @Override
        public FeatureRequest build() {
            return new FeatureRequest(this);
        }

        public FeatureRequestBuilder businessValue(final BusinessValue businessValue) {
            this.businessValue = businessValue;
            return self();
        }

        public FeatureRequestBuilder customerDemand(final CustomerDemand customerDemand) {
            this.customerDemand = customerDemand;
            return self();
        }

    }

}
