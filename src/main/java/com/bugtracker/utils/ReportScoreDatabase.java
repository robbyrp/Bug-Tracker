package com.bugtracker.utils;

import com.bugtracker.enums.*;

public class ReportScoreDatabase {
    private ReportScoreDatabase() {}

    /**
     * Converts enum values to scores
     * @param frequency
     * @return
     */
    public static int getFrequencyScore(final Frequency frequency) {
        if (frequency == null) {
            return 0;
        }
        return switch (frequency) {

            case RARE -> 1;
            case OCCASIONAL -> 2;
            case FREQUENT -> 3;
            case ALWAYS -> 4;
            default -> 0;
        };
    }

    /**
     * Converts enum values to scores
     * @param priority
     * @return
     */
    public static int getPriorityScore(final BusinessPriority priority) {
        if (priority == null) {
            return 0;
        }
        return switch (priority) {

            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
            case CRITICAL -> 4;
            default -> 0;
        };
    }

    /**
     *      * Converts enum values to scores
     * @param severity
     * @return
     */
    public static int getSeverityScore(final Severity severity) {
        if (severity == null) {
            return 0;
        }
        return switch(severity) {

            case MINOR -> 1;
            case MODERATE -> 2;
            case SEVERE -> 3;
            default -> 0;
        };
    }

    /**
     *      * Converts enum values to scores
     * @param businessValue
     * @return
     */
    public static int getBusinessValueScore(final BusinessValue businessValue) {
        if (businessValue == null) {
            return 0;
        }
        return switch(businessValue) {
            case S -> 1;
            case M -> 3;
            case L -> 6;
            case XL -> 10;
            default -> 0;
        };
    }

    /**
     *      * Converts enum values to scores
     * @param customerDemand
     * @return
     */
    public static int getCustomerDemandScore(final CustomerDemand customerDemand) {
        if (customerDemand == null) {
            return 0;
        }
        return switch(customerDemand) {
            case LOW -> 1;
            case MEDIUM -> 3;
            case HIGH -> 6;
            case VERY_HIGH -> 10;
            default -> 0;
        };
    }

}
