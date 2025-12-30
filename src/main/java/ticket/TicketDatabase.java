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
     * returns a ticket by id
     * @param id
     * @return
     */
    public Ticket getTickedById(final int id) {
        if (id < 0 || id >= tickets.size()) {
            return null;
        }
        return tickets.get(id);
    }

    public int getNextId() {
        return tickets.size();
    }
}
