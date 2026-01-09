package command.search;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CommandInput;
import fileio.FilterInput;
import main.BugTrackerSystem;
import user.User;

import java.util.List;

public interface SearchStrategy {
    void executeSearch(final BugTrackerSystem system, final List<ObjectNode> outputs,
                       final User user, final CommandInput input);
}
