package ticket;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class TicketDatabase {
    @Getter
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * adds a ticket to the list
     * @param ticket
     */
    public void addTicket(final Ticket ticket) {
        tickets.add(ticket);
    }

    /**
     * Removes a ticket from the list
     * @param ticket
     */
    public void removeTicket(final Ticket ticket) {
        tickets.remove(ticket);
    }

    /**
     * returns a ticket by id
     * @param id
     * @return
     */
    public Ticket getTicketById(final int id) {
        if (id < 0 || id >= tickets.size()) {
            return null;
        }
        return tickets.get(id);
    }

    /**
     * Returns the next ticket id available
     * @return
     */
    public int getNextId() {
        return tickets.size();
    }
}
