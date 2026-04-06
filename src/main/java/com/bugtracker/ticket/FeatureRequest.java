package com.bugtracker.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.bugtracker.enums.BusinessValue;
import com.bugtracker.enums.CustomerDemand;
import lombok.Getter;

@Getter
public final class FeatureRequest extends Ticket {
    @JsonIgnore
    private BusinessValue businessValue;
    @JsonIgnore
    private CustomerDemand customerDemand;

    private FeatureRequest(final FeatureRequestBuilder builder) {
        super(builder);
        this.businessValue = builder.businessValue;
        this.customerDemand = builder.customerDemand;
    }

    public static final class FeatureRequestBuilder extends Ticket.Builder<FeatureRequestBuilder> {
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

        /**
         *
         * @param businessValues
         * @return
         */
        public FeatureRequestBuilder businessValue(final BusinessValue businessValues) {
            this.businessValue = businessValues;
            return self();
        }

        /**
         *
         * @param customerDemands
         * @return
         */
        public FeatureRequestBuilder customerDemand(final CustomerDemand customerDemands) {
            this.customerDemand = customerDemands;
            return self();
        }


    }

}
