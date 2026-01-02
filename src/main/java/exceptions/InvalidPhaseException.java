package exceptions;

import enums.ApplicationPhase;

import java.util.Objects;

public class InvalidPhaseException extends RuntimeException {

    public InvalidPhaseException(final String commandName,
                                 final ApplicationPhase currentPhase) {
        super(generateMessage(commandName));
    }

    private static String generateMessage(String commandName) {
        if (Objects.equals(commandName, "reportTicket")) {
            return "Tickets can only be reported during testing phases.";
        }
        return "Command " + commandName + " not allowed in the current phase";
    }
}
