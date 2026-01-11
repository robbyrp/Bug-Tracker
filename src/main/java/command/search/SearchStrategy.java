package command.search;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CommandInput;
import main.BugTrackerSystem;
import user.User;

import java.util.List;

public interface SearchStrategy {
    /**
     * Abstract strategy to be implemented by devs and managers
     * @param system
     * @param outputs
     * @param user
     * @param input
     */
    void executeSearch(BugTrackerSystem system, List<ObjectNode> outputs,
                       User user, CommandInput input);
}
