package ticket;

import ticket.enums.BusinessPriority;
import ticket.enums.ExpertiseArea;
import ticket.enums.Status;

public abstract class Ticket {

    protected int id;
    protected String type;
    protected String title;
    protected BusinessPriority businessPriority;
    protected Status status;
    protected ExpertiseArea expertiseArea;
    protected String reportedBy;

    // Optional fields
    protected String description;

    protected Ticket (Builder b) {
        this.id = b.id;
        this.type = b.type;
        this.title = b.title;
        this.businessPriority = b.businessPriority;
        this.status = b.status;
        this.expertiseArea = b.expertiseArea;
        this.reportedBy = b.reportedBy;
    }

    // T represents the future builder(inner class) of Ticket's children, for example Bug.BugBuilder
    public abstract static class Builder<T extends Builder<T>> {
        private int id;
        private  String type;
        private String title;
        private BusinessPriority businessPriority;
        private Status status;
        private ExpertiseArea expertiseArea;
        private String reportedBy;

        // Optional fields
        private String description;

        /**
         * Making self abstract in the parent class avoids
         * the unchecked cast of (T) this;
         * @return the child's inner builder class, for example Bug.BugBuilder
         */
        protected abstract T self() ;

        public T id(final int id){
            this.id = id;
            return self();
        }

        public T type(final String type) {
            this.type = type;
            return self();
        }

        public T title(final String title) {
            this.title = title;
            return self();
        }

        public T businessPriority(final BusinessPriority bp) {
            this.businessPriority = bp;
            return self();
        }

        public T status(final Status status) {
            this.status = status;
            return self();
        }

        public T expertiseArea(final ExpertiseArea expertiseArea) {
            this.expertiseArea = expertiseArea;
            return self();
        }

        public T reportedBy(final String reportedBy) {
            this.reportedBy = reportedBy;
            return self();
        }

        public T description(final String description) {
            this.description = description;
            return self();
        }

        public abstract Ticket build() ;


    }

}
