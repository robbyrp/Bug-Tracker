package ticket;

import enums.BusinessPriority;
import enums.ExpertiseArea;
import enums.Status;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
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

    protected Ticket(final Builder b) {
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
        protected abstract T self();

        /**
         *
         * @param ids
         * @return
         */
        public T id(final int ids) {
            this.id = ids;
            return self();
        }

        /**
         *
         * @param types
         * @return
         */
        public T type(final String types) {
            this.type = types;
            return self();
        }

        /**
         *
         * @param titles
         * @return
         */
        public T title(final String titles) {
            this.title = titles;
            return self();
        }

        /**
         *
         * @param bps
         * @return
         */
        public T businessPriority(final BusinessPriority bps) {
            this.businessPriority = bps;
            return self();
        }

        /**
         *
         * @param statuss
         * @return
         */
        public T status(final Status statuss) {
            this.status = statuss;
            return self();
        }

        /**
         *
         * @param expertiseAreas
         * @return
         */
        public T expertiseArea(final ExpertiseArea expertiseAreas) {
            this.expertiseArea = expertiseAreas;
            return self();
        }

        /**
         *
         * @param reportedBys
         * @return
         */
        public T reportedBy(final String reportedBys) {
            this.reportedBy = reportedBys;
            return self();
        }

        /**
         *
         * @param descriptions
         * @return
         */
        public T description(final String descriptions) {
            this.description = descriptions;
            return self();
        }

        /**
         * builds the ticket's child object
         * @return
         */
        public abstract Ticket build() ;

        public abstract boolean canBeAnonymous();


    }

}
