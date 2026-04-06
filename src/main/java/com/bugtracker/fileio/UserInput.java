package com.bugtracker.fileio;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public final class UserInput {
    private String username;
    private String email;
    private String role;

    private String hireDate;
    private String expertiseArea;
    private String seniority;

    private ArrayList<String> subordinates;

}
