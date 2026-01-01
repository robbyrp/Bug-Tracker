package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.Role;
import main.BugTrackerSystem;

import java.util.List;

public interface Command {
    /**
     * Execute method for the command pattern
     * @param system
     * @param outputs
     */
    void execute(BugTrackerSystem system, List<ObjectNode> outputs);

    default void undo(BugTrackerSystem system, List<ObjectNode> outputs) {
        return;
    }

    /**
     * Returns the list of roles required for execution
     * @return List<Role> or null based on the command's permission
     */
    default List<Role> getRequiredRoles() {
        return null;
    }
}
