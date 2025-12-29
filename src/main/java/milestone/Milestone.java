package milestone;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public final class Milestone {
    private String name;
    private String[] blockingFor;
    private LocalDate dueDate;
    private int[] tickets;
    private String[] assignedDevs;

}
