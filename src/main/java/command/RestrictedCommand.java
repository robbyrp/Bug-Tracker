package command;

import enums.Role;

import java.util.List;

/**
 * This interface is to be implemented by createMilestone, viewTicketHistory and viewAssignedTickets
 * because only those 3 can be called by someone without permission
 */
public interface RestrictedCommand {
    /**
     *
     * @return a list of the roles that are required to perform the action
     */
    List<Role> getRequiredRoles();
}
