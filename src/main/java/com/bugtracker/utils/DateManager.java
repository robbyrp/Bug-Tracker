package com.bugtracker.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.bugtracker.enums.ApplicationPhase;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class DateManager {
    @JsonIgnore
    private static DateManager instance;
    @Getter
    private ApplicationPhase currentPhase = ApplicationPhase.TESTING;
    @JsonIgnore
    private LocalDate lastTestingStartDate = null;
    @JsonIgnore
    private static final int TESTING_PHASE_DURATION = 12;


    private DateManager() { }

    /**
     * Singleton getInstance method
     * @return
     */
    public static DateManager getInstance() {
        if (instance == null) {
            return new DateManager();
        }
        return instance;
    }

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
     * Resets the DateManager to its initial state.
     * Useful for clearing memory between batch executions.
     */
    public void reset() {
        this.currentPhase = ApplicationPhase.TESTING;
        this.lastTestingStartDate = null;
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
