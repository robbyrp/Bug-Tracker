package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.Role;
import fileio.CommandInput;
import lombok.Getter;
import main.BugTrackerSystem;
import user.User;

import java.util.List;

@Getter
public abstract class Command {

    private final CommandInput commandInput;
    private final User user;

    public Command(final CommandInput input, final User user) {
        this.commandInput = input;
        this.user = user;
    }

    /**
     * Execute method for the command pattern
     * @param system The engine of the program
     * @param outputs output mapper
     */
    public abstract void execute(BugTrackerSystem system, List<ObjectNode> outputs);

    /**
     * Defines the list of roles required for an action's execution
     * @return Returns List of roles for commands that require specific roles
     * or null when all roles can execute it
     */
    public List<Role> getRequiredRoles() {
        return null;
    }

    /**
     * Defines the phase in which a command can be executed
     * @return Returns the Phase, or null if the command can be executed
     * in any phase
     */
    public ApplicationPhase getRequiredPhase() {
        return null;
    }
}
