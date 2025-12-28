package user;

import fileio.UserInput;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User {
    private String username;
    private String email;
    private Role role;

    /**
     * Smart constructor for parent class' fields only
     * @param input
     */
    protected User(UserInput input) {
        this.username = input.getUsername();
        this.email = input.getEmail();
        this.role = Role.fromString(input.getRole());
    }

}
