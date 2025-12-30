package ticket;

import enums.Frequency;
import enums.Severity;

public final class Bug extends Ticket {

    private String expectedBehavior;
    private String actualBehavior;
    private Frequency frequency;
    private Severity severity;

    // Optional fields
    private String environment;
    private Integer errorCode; // Wrapper so that i can check if bug.errorcode != null


    private Bug(final BugBuilder builder) {
        super(builder);
        this.expectedBehavior = builder.expectedBehavior;
        this.actualBehavior = builder.actualBehavior;
        this.frequency = builder.frequency;
        this.severity = builder.severity;
    }

    public final static class BugBuilder extends Ticket.Builder<BugBuilder> {
        private String expectedBehavior;
        private String actualBehavior;
        private Frequency frequency;
        private Severity severity;

        // Optional fields
        private String environment;
        private Integer errorCode;

        @Override
        protected BugBuilder self() {
            return this;
        }

        @Override
        public Bug build() {
            return new Bug(this);
        }

        /**
         *
         * @param expectedBehaviors
         * @return
         */
        public BugBuilder expectedBehavior(final String expectedBehaviors) {
            this.expectedBehavior = expectedBehaviors;
            return self();
        }

        /**
         *
         * @param actualBehaviors
         * @return
         */
        public BugBuilder actualBehavior(final String actualBehaviors) {
            this.actualBehavior = actualBehaviors;
            return self();
        }

        /**
         *
         * @param frequencys
         * @return
         */
        public BugBuilder frequency(final Frequency frequencys) {
            this.frequency = frequencys;
            return self();
        }

        /**
         *
         * @param severitys
         * @return
         */
        public BugBuilder severity(final Severity severitys) {
            this.severity = severitys;
            return self();
        }

        /**
         *
         * @param environments
         * @return
         */
        public BugBuilder environment(final String environments) {
            this.environment = environments;
            return self();
        }

        /**
         *
         * @param errorCodes
         * @return
         */
        public BugBuilder errorCode(final Integer errorCodes) {
            this.errorCode = errorCodes;
            return self();
        }

        public boolean canBeAnonymous() {
            return true;
        }

    }

}
