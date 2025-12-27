package ticket;

import ticket.enums.Frequency;
import ticket.enums.Severity;

public final class Bug extends Ticket{

    private String expectedBehavior;
    private String actualBehavior;
    private Frequency frequency;
    private Severity severity;

    // Optional fields
    private String environment;
    private int errorCode;


    private Bug(BugBuilder builder) {
        super(builder);
        this.expectedBehavior = builder.expectedBehavior;
        this.actualBehavior = builder.actualBehavior;
        this.frequency = builder.frequency;
        this.severity = builder.severity;
    }

    public static class BugBuilder extends Ticket.Builder<BugBuilder> {
        private String expectedBehavior;
        private String actualBehavior;
        private Frequency frequency;
        private Severity severity;

        // Optional fields
        private String environment;
        private int errorCode;

        @Override
        protected BugBuilder self() {
            return this;
        }

        @Override
        public Bug build() {
            return new Bug(this);
        }

        public BugBuilder expectedBehavior(final String expectedBehavior) {
            this.expectedBehavior = expectedBehavior;
            return self();
        }

        public BugBuilder actualBehavior(final String actualBehavior) {
            this.actualBehavior = actualBehavior;
            return self();
        }

        public BugBuilder frequency(final Frequency frequency) {
            this.frequency = frequency;
            return self();
        }

        public BugBuilder severity(final Severity severity) {
            this.severity = severity;
            return self();
        }

        public BugBuilder environment(final String environment) {
            this.environment = environment;
            return self();
        }

        public BugBuilder errorCode(final int errorCode) {
            this.errorCode = errorCode;
            return self();
        }

    }

}
