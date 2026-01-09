package user;

import fileio.UserInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public final class Manager extends User {
    private String hireDate;
    private ArrayList<String> subordinates;

    public Manager(UserInput input) {
        super(input);
        this.hireDate = input.getHireDate();
        this.subordinates = input.getSubordinates();
    }

}
