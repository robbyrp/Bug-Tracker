package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.Role;
import fileio.CommandInput;
import lombok.Getter;
import main.BugTrackerSystem;
import user.User;

import java.util.List;

@Getter
public abstract class Command {

    private CommandInput input;
    private User user;

    public Command(CommandInput input, User user) {
        this.input = input;
        this.user = user;
    }

    /**
     * Execute method for the command pattern
     * @param system
     * @param outputs
     */
    public abstract void execute(BugTrackerSystem system, List<ObjectNode> outputs);

    public void undo(BugTrackerSystem system, List<ObjectNode> outputs) {
        return;
    }

    /**
     * Returns the list of roles required for execution
     * @return List<Role> or null based on the command's permission
     */
    public List<Role> getRequiredRoles() {
        return null;
    }
}
