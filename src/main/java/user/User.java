package user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fileio.UserInput;
import lombok.Getter;
import lombok.Setter;
import enums.Role;
import notification.Subscriber;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public abstract class User implements Subscriber {
    private String username;
    private String email;
    private Role role;

    @JsonIgnore
    private List<String> notifications = new ArrayList<>();

    /**
     * Smart constructor for parent class' fields only
     * @param input
     */
    protected User(UserInput input) {
        this.username = input.getUsername();
        this.email = input.getEmail();
        this.role = Role.fromString(input.getRole());
    }

    @Override
    public void update(String notification) {
        this.notifications.add(notification);
    }

    public void clear(String notification) {
        this.notifications.clear();
    }

}
