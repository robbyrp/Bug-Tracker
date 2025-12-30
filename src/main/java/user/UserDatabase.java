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

    public void initialize(ArrayList<UserInput> userInputs) {
        for (UserInput input : userInputs) {
            User user = UserFactory.createUser(input);
            users.put(user.getUsername(), user);
        }
    }
}
