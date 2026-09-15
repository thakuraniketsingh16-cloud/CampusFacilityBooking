package campus.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a reservation made by a user for a campus facility.
 */
public class Booking {
    private String bookingId;
    private String userId;
    private String facilityId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status; // CONFIRMED, CANCELLED, COMPLETED
    private String purpose;

    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public Booking(String bookingId, String userId, String facilityId, 
                   LocalDate date, LocalTime startTime, LocalTime endTime, 
                   String status, String purpose) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.facilityId = facilityId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.purpose = purpose;
    }

    /**
     * Checks if this booking overlaps with another requested time slot.
     * Note: Cancelled bookings do not cause conflict.
     */
    public boolean overlapsWith(LocalDate requestedDate, LocalTime requestedStart, LocalTime requestedEnd) {
        if ("CANCELLED".equalsIgnoreCase(this.status)) {
            return false;
        }
        if (!this.date.equals(requestedDate)) {
            return false;
        }
        // Standard interval overlap: [start1, end1) and [start2, end2)
        // Overlap exists if start1 < end2 and end1 > start2
        return this.startTime.isBefore(requestedEnd) && this.endTime.isAfter(requestedStart);
    }

    public double getDurationInHours() {
        if (startTime == null || endTime == null) return 0;
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return minutes / 60.0;
    }

    public String toFileString() {
        return String.format("BKG|%s|%s|%s|%s|%s|%s|%s|%s",
                bookingId, userId, facilityId, date, 
                startTime.format(TIME_FMT), endTime.format(TIME_FMT), 
                status, purpose);
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return String.format("[%s] User: %s | Facility: %s | Date: %s | Time: %s - %s | Status: %-9s | %s",
                bookingId, userId, facilityId, date, 
                startTime.format(TIME_FMT), endTime.format(TIME_FMT), 
                status, purpose);
    }
}
