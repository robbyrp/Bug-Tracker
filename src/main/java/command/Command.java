package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.BugTrackerSystem;

import java.util.List;

public interface Command {
    /**
     * Execute method for the command pattern
     * @param system
     * @param outputs
     */
    void execute(BugTrackerSystem system, List<ObjectNode> outputs);
//    void undo(BugTrackerSystem system, List<ObjectNode> outputs);
}
