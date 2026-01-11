package user;

import fileio.UserInput;
import lombok.Getter;
import user.userFactory.UserFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class UserDatabase {
    @Getter
    private Map<String, User> users = new HashMap<>();

    /**
     * Builds the users from userInput and adds them to the users database
     * which is a map with key username and value User Map<Username, User>
     * @param userInputs
     */
    public void initialize(final ArrayList<UserInput> userInputs) {
        for (UserInput input : userInputs) {
            User user = UserFactory.createUser(input);
            users.put(user.getUsername(), user);
        }
    }
}
