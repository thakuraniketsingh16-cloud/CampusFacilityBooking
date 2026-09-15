package campus.exceptions;

/**
 * Thrown when a requested facility is already reserved during the requested interval.
 */
public class SlotUnavailableException extends BookingException {
    private String facilityId;
    private String conflictedBookingId;

    public SlotUnavailableException(String message) {
        super(message);
    }

    public SlotUnavailableException(String facilityId, String conflictedBookingId, String message) {
        super(message);
        this.facilityId = facilityId;
        this.conflictedBookingId = conflictedBookingId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getConflictedBookingId() {
        return conflictedBookingId;
    }
}
