package campus.exceptions;

/**
 * Thrown when inputs (dates, times, user ids, hours) are invalid or out of permissible bounds.
 */
public class InvalidInputException extends BookingException {
    public InvalidInputException(String message) {
        super(message);
    }
}
