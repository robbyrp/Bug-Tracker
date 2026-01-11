package user;

import fileio.UserInput;
import lombok.Getter;
import lombok.Setter;
import enums.ExpertiseArea;
import enums.Seniority;

@Getter @Setter
public final class Developer extends User {
    private ExpertiseArea expertiseArea;
    private Seniority seniority;
    private double performanceScore;
    private String hireDate;

    public Developer(final UserInput input) {
        super(input);
        this.hireDate = input.getHireDate();
        this.expertiseArea = ExpertiseArea.fromString(input.getExpertiseArea());
        this.seniority = Seniority.fromString(input.getSeniority());
        this.performanceScore = 0.0;
    }

}
