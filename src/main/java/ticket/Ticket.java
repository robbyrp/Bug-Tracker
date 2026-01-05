package ticket;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import enums.BusinessPriority;
import enums.ExpertiseArea;
import enums.Status;
import enums.TicketType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@JsonPropertyOrder({
        "id",
        "type",
        "title",
        "businessPriority",
        "status",
        "createdAt",
        "assignedAt",
        "solvedAt",
        "assignedTo",
        "reportedBy",
        "comments",
        "description"
})
public abstract class Ticket {
    protected int id;
    protected TicketType type;
    protected String title;
    protected BusinessPriority businessPriority;
    protected Status status;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    protected LocalDate reportedTimestamp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(nullsUsing = EmptyStringSerializer.class)
    protected LocalDate assignedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(nullsUsing = EmptyStringSerializer.class)
    protected LocalDate solvedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(nullsUsing = EmptyStringSerializer.class)
    protected String assignedTo = "";

    protected String reportedBy;
    protected List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    protected ExpertiseArea expertiseArea;

    // Optional fields
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String description;

    @JsonIgnore
    protected List<TicketAction> history = new ArrayList<>();

    protected Ticket(final Builder b) {
        this.id = b.id;
        this.type = b.type;
        this.title = b.title;
        this.businessPriority = b.businessPriority;
        this.status = b.status;
        this.expertiseArea = b.expertiseArea;
        this.reportedBy = b.reportedBy;
        this.reportedTimestamp = b.reportedTimestamp;
    }

    // T represents the future builder(inner class) of Ticket's children, for example Bug.BugBuilder
    public abstract static class Builder<T extends Builder<T>> {
        private int id;
        private TicketType type;
        private String title;
        private BusinessPriority businessPriority;
        private Status status;
        private ExpertiseArea expertiseArea;
        private String reportedBy;
        private LocalDate reportedTimestamp;

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
        public T type(final TicketType types) {
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

        public T reportedTimestamp (final LocalDate reportedTimestamps) {
            this.reportedTimestamp = reportedTimestamps;
            return self();
        }

        /**
         * builds the ticket's child object
         * @return
         */
        public abstract Ticket build() ;

        public abstract boolean canBeAnonymous();

    }

    public void addHistory(String action, String username, String timestamp) {
        history.add(new TicketAction(action, username, timestamp));
    }

    /**
     * Internal class that transforms a null object to ""
     * For json output
     */
    public static class EmptyStringSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString("");
        }
    }
}
