package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.BugTrackerSystem;

import java.util.List;

public interface Command {
    void execute(BugTrackerSystem system, List<ObjectNode> outputs);
}
