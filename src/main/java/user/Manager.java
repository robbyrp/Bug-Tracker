package user;

import fileio.UserInput;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public final class Manager extends User {
    private String hireDate;
    private String[] subordinates;

    public Manager(UserInput input) {
        super(input);
        this.hireDate = input.getHireDate();
        this.subordinates = input.getSubordinates();
    }
}
