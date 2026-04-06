package com.bugtracker.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.bugtracker.fileio.UserInput;
import lombok.Getter;
import lombok.Setter;
import com.bugtracker.enums.Role;
import com.bugtracker.notification.Subscriber;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public abstract class User implements Subscriber {
    private String username;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private Role role;

    @JsonIgnore
    private List<String> notifications = new ArrayList<>();

    /**
     * Smart constructor for parent class' fields only
     * @param input
     */
    protected User(final UserInput input) {
        this.username = input.getUsername();
        this.email = input.getEmail();
        this.role = Role.fromString(input.getRole());
    }

    /**
     * Adds notification String to the user's list
     * @param notification
     */
    @Override
    public void update(final String notification) {
        this.notifications.add(notification);
    }

}
