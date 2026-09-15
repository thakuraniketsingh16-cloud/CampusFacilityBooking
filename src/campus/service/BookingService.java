package campus.service;

import campus.exceptions.*;
import campus.models.*;
import campus.storage.DataFileManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class handling all business logic for facility reservations,
 * conflict resolution, quota checks, and statistics.
 */
public class BookingService {

    private Map<String, Facility> facilities;
    private Map<String, User> users;
    private List<Booking> bookings;
    private DataFileManager fileManager;

    public BookingService(String dataFilePath) {
        this.facilities = new LinkedHashMap<>();
        this.users = new LinkedHashMap<>();
        this.bookings = new ArrayList<>();
        this.fileManager = new DataFileManager(dataFilePath);

        // Load initial records
        loadAllData();
    }

    public void loadAllData() {
        facilities.clear();
        users.clear();
        bookings.clear();
        fileManager.loadData(facilities, users, bookings);
    }

    public void saveAllData() {
        fileManager.saveData(facilities.values(), users.values(), bookings);
    }

    public Collection<Facility> getAllFacilities() {
        return facilities.values();
    }

    public Facility getFacility(String id) {
        if (id == null) return null;
        return facilities.get(id.toUpperCase().trim());
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUser(String id) {
        if (id == null) return null;
        return users.get(id.toUpperCase().trim());
    }

    public List<Booking> getAllBookings() {
        return Collections.unmodifiableList(bookings);
    }

    /**
     * Retrieve all bookings for a particular user.
     */
    public List<Booking> getBookingsForUser(String userId) {
        List<Booking> list = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getUserId().equalsIgnoreCase(userId)) {
                list.add(b);
            }
        }
        return list;
    }

    /**
     * Retrieve confirmed schedule for a facility on a specific date.
     */
    public List<Booking> getFacilitySchedule(String facilityId, LocalDate date) {
        List<Booking> schedule = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getFacilityId().equalsIgnoreCase(facilityId) 
                    && b.getDate().equals(date) 
                    && "CONFIRMED".equalsIgnoreCase(b.getStatus())) {
                schedule.add(b);
            }
        }
        // Sort chronologically by start time
        schedule.sort(Comparator.comparing(Booking::getStartTime));
        return schedule;
    }

    /**
     * Primary booking method with validation and conflict detection.
     */
    public synchronized Booking createBooking(String userId, String facilityId, 
                                             LocalDate date, LocalTime start, LocalTime end, 
                                             String purpose) throws BookingException {

        // 1. Verify User
        User user = getUser(userId);
        if (user == null) {
            throw new InvalidInputException("User ID not registered: " + userId);
        }

        // 2. Verify Facility
        Facility facility = getFacility(facilityId);
        if (facility == null) {
            throw new InvalidInputException("Facility ID not recognized: " + facilityId);
        }

        if (facility.isUnderMaintenance()) {
            throw new BookingException("Facility " + facility.getName() + " is currently under scheduled maintenance.");
        }

        // 3. Time sanity checks
        if (start.isAfter(end) || start.equals(end)) {
            throw new InvalidInputException("Start time (" + start + ") must be strictly before end time (" + end + ").");
        }

        // Campus operating hours: 07:00 to 22:00
        LocalTime opening = LocalTime.of(7, 0);
        LocalTime closing = LocalTime.of(22, 0);
        if (start.isBefore(opening) || end.isAfter(closing)) {
            throw new InvalidInputException("Booking time must be within campus operating hours (07:00 to 22:00).");
        }

        // 4. Booking duration limits (Polymorphism)
        long durationMinutes = java.time.Duration.between(start, end).toMinutes();
        double durationHours = durationMinutes / 60.0;
        int maxHours = user.getMaxBookingHours();

        if (durationHours > maxHours) {
            throw new InvalidInputException(String.format(
                    "Selected duration (%.1f hrs) exceeds allowed limit of %d hrs for %s.", 
                    durationHours, maxHours, user.getRole()));
        }

        // 5. Special permission: Student booking large hall/auditorium
        if (facility instanceof Hall && facility.getCapacity() > 150) {
            if (!user.canBookAuditoriumDirectly()) {
                throw new BookingException("Students cannot directly book grand auditoriums. Faculty advisor endorsement required.");
            }
        }

        // 6. Conflict detection: check if slot is avaliable
        // System.out.println("DEBUG: checking conflicts for " + facilityId + " on " + date);
        for (Booking existing : bookings) {
            if (existing.getFacilityId().equalsIgnoreCase(facilityId)) {
                if (existing.overlapsWith(date, start, end)) {
                    throw new SlotUnavailableException(
                            facilityId, 
                            existing.getBookingId(),
                            String.format("Slot %s to %s on %s is already reserved by booking %s (%s to %s).",
                                    start, end, date, existing.getBookingId(), 
                                    existing.getStartTime(), existing.getEndTime()));
                }
            }
        }

        // 7. Generate next ID
        String newBookingId = generateNextBookingId();
        Booking newBooking = new Booking(newBookingId, user.getUserId(), facility.getId(), 
                                         date, start, end, "CONFIRMED", purpose);
        bookings.add(newBooking);

        // Auto-save
        saveAllData();

        return newBooking;
    }

    /**
     * Cancel an existing reservation.
     */
    public synchronized boolean cancelBooking(String bookingId, String requestingUserId) throws BookingException {
        Booking target = null;
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(bookingId)) {
                target = b;
                break;
            }
        }

        if (target == null) {
            throw new BookingException("Booking with ID '" + bookingId + "' was not found.");
        }

        if ("CANCELLED".equalsIgnoreCase(target.getStatus())) {
            throw new BookingException("Booking " + bookingId + " is already marked as CANCELLED.");
        }

        User requester = getUser(requestingUserId);
        boolean isOwner = target.getUserId().equalsIgnoreCase(requestingUserId);
        boolean isFaculty = requester != null && requester instanceof Faculty;

        if (!isOwner && !isFaculty) {
            throw new BookingException("Permission denied. Only the booking owner or a faculty administrator can cancel this reservation.");
        }

        target.setStatus("CANCELLED");
        saveAllData();
        return true;
    }

    private String generateNextBookingId() {
        int maxNum = 100;
        for (Booking b : bookings) {
            try {
                String id = b.getBookingId().replace("B", "").trim();
                int num = Integer.parseInt(id);
                if (num > maxNum) maxNum = num;
            } catch (Exception ignored) {
            }
        }
        return "B" + (maxNum + 1);
    }

    /**
     * Prints statistical analytics across campus usage.
     */
    public void printAnalyticsReport() {
        System.out.println("\n========================================================");
        System.out.println("          CAMPUS FACILITY ANALYTICS & USAGE REPORT       ");
        System.out.println("========================================================");
        System.out.println("Total Facilities Registered : " + facilities.size());
        System.out.println("Total Registered Users      : " + users.size());
        System.out.println("Total Reservations Logged   : " + bookings.size());

        long confirmedCount = bookings.stream().filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus())).count();
        long cancelledCount = bookings.stream().filter(b -> "CANCELLED".equalsIgnoreCase(b.getStatus())).count();
        System.out.println("Active Confirmed Bookings   : " + confirmedCount);
        System.out.println("Cancelled Bookings          : " + cancelledCount);

        // Facility booking frequency
        Map<String, Long> facilityCountMap = bookings.stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.groupingBy(Booking::getFacilityId, Collectors.counting()));

        System.out.println("\n--- Booking Count by Facility ---");
        for (Facility f : facilities.values()) {
            long count = facilityCountMap.getOrDefault(f.getId(), 0L);
            System.out.printf("  * [%s] %-30s: %2d bookings\n", f.getId(), f.getName(), count);
        }

        // Bookings by Department
        System.out.println("\n--- Activity by Department ---");
        Map<String, Long> deptMap = new HashMap<>();
        for (Booking b : bookings) {
            User u = getUser(b.getUserId());
            if (u != null) {
                deptMap.put(u.getDepartment(), deptMap.getOrDefault(u.getDepartment(), 0L) + 1);
            }
        }
        for (Map.Entry<String, Long> entry : deptMap.entrySet()) {
            System.out.printf("  * %-20s: %d bookings\n", entry.getKey(), entry.getValue());
        }
        System.out.println("========================================================\n");
    }
}
