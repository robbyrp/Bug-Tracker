package fileio;

import lombok.Data;
import lombok.NoArgsConstructor;
import ticket.enums.ExpertiseArea;
import user.Role;
import user.Seniority;

@Data
@NoArgsConstructor
public final class UserInput {
    private String username;
    private String email;
    private String role;

    private String hireDate;
    private String expertiseArea;
    private String seniority;

    private String[] subordinates;

}
