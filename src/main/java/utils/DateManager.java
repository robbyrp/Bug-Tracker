package utils;

import enums.ApplicationPhase;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class DateManager {

    @Getter
    private ApplicationPhase currentPhase = ApplicationPhase.TESTING;

    private LocalDate lastTestingStartDate = null;
    private static final int TESTING_PHASE_DURATION = 12;

    /**
     * Method called at every command to verify testing phase
     * @param currentTimestampString
     */
    public void updatePhase(String currentTimestampString) {
        if (currentTimestampString == null) {
            return;
        }

        LocalDate now = LocalDate.parse(currentTimestampString);

        if (lastTestingStartDate == null) {
            lastTestingStartDate = now;
        }

        if (currentPhase == ApplicationPhase.TESTING) {
            int daysElapsed = getDaysBetween(lastTestingStartDate, now);

            if (daysElapsed > TESTING_PHASE_DURATION) {
                currentPhase = ApplicationPhase.DEVELOPMENT;
            }
        }
    }

    /**
     * Method called by the command startTestingPhase
     * @param timestampString
     */
    public void startNewTestingPhase(String timestampString) {
        this.currentPhase = ApplicationPhase.TESTING;
        this.lastTestingStartDate = LocalDate.parse(timestampString);
    }


    /**
     * Helper to calculate days between
     * @param now
     * @param due
     * @return
     */
    private int getDaysBetween(LocalDate now, LocalDate due) {
        return (int) ChronoUnit.DAYS.between(now, due) + 1;
    }
}
