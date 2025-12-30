package user;

import fileio.UserInput;
import lombok.Getter;
import lombok.Setter;
import enums.ExpertiseArea;
import enums.Seniority;

@Getter @Setter
public final class Developer extends User {
    private String hireDate;
    private ExpertiseArea expertiseArea;
    private Seniority seniority;

    public Developer(UserInput input) {
        super(input);
        this.hireDate = input.getHireDate();
        this.expertiseArea = ExpertiseArea.fromString(input.getExpertiseArea());
        this.seniority = Seniority.fromString(input.getSeniority());
    }

}
