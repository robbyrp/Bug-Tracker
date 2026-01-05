package milestone;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public final class Repartition implements Comparable<Repartition> {
    private String developer;
    private ArrayList<Integer> assignedTickets;

    public Repartition(final String developer) {
        this.developer = developer;
        this.assignedTickets = new ArrayList<>();
    }


    /**
     * Adds the ticketId in the ticketId arrayList
     * @param ticketId
     */
    public void addTicketId(final Integer ticketId) {
        this.assignedTickets.add(ticketId);
    }

    /**
     * Empties the list of assigned tickets
     */
    public void clear() {
        this.assignedTickets.clear();
    }

    /**
     * Implements the comparison criteria for repartitions
     * It is sorted ascendingly by the number of tickets
     * In case of equality, sorted alphabetically by username
     * @param other
     * @return
     */
    @Override
    public int compareTo(Repartition other) {
        int sizeComparison =
                Integer.compare(this.assignedTickets.size(), other.assignedTickets.size());

        if (sizeComparison != 0) {
            return sizeComparison;
        }

        return this.developer.compareTo(other.developer);
    }
}
