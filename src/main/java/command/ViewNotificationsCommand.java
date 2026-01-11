package command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.ApplicationPhase;
import enums.Role;
import fileio.CommandInput;
import fileio.OutputFormatter;
import main.BugTrackerSystem;
import user.User;

import java.util.ArrayList;
import java.util.List;

public final class ViewNotificationsCommand extends Command {

    public ViewNotificationsCommand(final CommandInput input, final User user) {
        super(input, user);
    }

    @Override
    public ApplicationPhase getRequiredPhase() {
        return ApplicationPhase.DEVELOPMENT;
    }

    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.DEVELOPER);
    }

    /**
     * Execute method that displays notifications by using a copy and deleting it
     * @param system The engine of the program
     * @param outputs output mapper
     */
    @Override
    public void execute(final BugTrackerSystem system, final List<ObjectNode> outputs) {
        User user = getUser();

        List<String> notificationsCopy = new ArrayList<>(user.getNotifications());

        user.getNotifications().clear();

        outputs.add(OutputFormatter.createListResponse(
                getCommandInput().getCommand(),
                getCommandInput().getUsername(),
                getCommandInput().getTimestamp(),
                "notifications",
                notificationsCopy
        ));
    }
}
