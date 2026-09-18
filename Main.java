import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SMART CAMPUS FACILITY & LAB BOOKING SYSTEM
 * Course: Programming in Java
 * Author: Aniket Singh
 * 
 * Demonstrates Object-Oriented Programming (OOP) in Java:
 * - Abstraction & Interfaces (Facility, User)
 * - Inheritance (Lab, Hall, SportsCourt extend Facility; Student, FacultyMember extend User)
 * - Polymorphism (Dynamic method dispatch on getMaxBookingHours, getSpecificDetails)
 * - Encapsulation (Validated getters/setters, interval overlap protection)
 * - Exception Handling (Custom exceptions for double-booking conflicts and quota limits)
 * - File I/O & Persistence (Single-file flat text database campus_data.txt)
 */

// =========================================================================
// CUSTOM EXCEPTION CLASSES
// =========================================================================
class CampusBookingException extends Exception {
    public CampusBookingException(String message) { super(message); }
}

class SlotUnavailableException extends CampusBookingException {
    public SlotUnavailableException(String message) { super(message); }
}

class InvalidInputException extends CampusBookingException {
    public InvalidInputException(String message) { super(message); }
}

// =========================================================================
// ABSTRACT BASE CLASS: Facility (Abstraction & Encapsulation)
// =========================================================================
abstract class Facility {
    private String id;
    private String name;
    private String type;
    private int capacity;
    private String location;
    private boolean underMaintenance;

    public Facility(String id, String name, String type, int capacity, String location) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.location = location;
        this.underMaintenance = false;
    }

    public abstract String getSpecificDetails();
    public abstract double getHourlyRate();

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getCapacity() { return capacity; }
    public String getLocation() { return location; }
    public boolean isUnderMaintenance() { return underMaintenance; }
    public void setUnderMaintenance(boolean underMaintenance) { this.underMaintenance = underMaintenance; }

    @Override
    public String toString() {
        return String.format("[%s] %-30s | Type: %-11s | Cap: %3d | Loc: %s",
                id, name, type, capacity, location);
    }
}

// =========================================================================
// CONCRETE SUBCLASSES: Lab, Hall, SportsCourt (Inheritance & Polymorphism)
// =========================================================================
class Lab extends Facility {
    private int workstations;
    private String operatingSystem;
    private boolean hasGpu;

    public Lab(String id, String name, int capacity, String location, int workstations, String os, boolean hasGpu) {
        super(id, name, "Lab", capacity, location);
        this.workstations = workstations;
        this.operatingSystem = os;
        this.hasGpu = hasGpu;
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Workstations: %d, OS: %s, Dedicated GPU: %s", 
                workstations, operatingSystem, (hasGpu ? "Yes (NVIDIA RTX)" : "Integrated"));
    }

    @Override
    public double getHourlyRate() { return 0.0; } // Free academic lab usage
}

class Hall extends Facility {
    private boolean hasProjector;
    private boolean hasSoundSystem;
    private boolean isAirConditioned;

    public Hall(String id, String name, int capacity, String location, boolean projector, boolean sound, boolean ac) {
        super(id, name, "Hall", capacity, location);
        this.hasProjector = projector;
        this.hasSoundSystem = sound;
        this.isAirConditioned = ac;
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Projector: %s, Audio System: %s, AC: %s",
                (hasProjector ? "Yes" : "No"), (hasSoundSystem ? "Equipped" : "No"), (isAirConditioned ? "Yes" : "Non-AC"));
    }

    @Override
    public double getHourlyRate() { return 250.0; } // Nominal utility rate for events
}

class SportsCourt extends Facility {
    private String surfaceType;
    private boolean isIndoor;
    private boolean hasLighting;

    public SportsCourt(String id, String name, int capacity, String location, String surface, boolean indoor, boolean lights) {
        super(id, name, "SportsCourt", capacity, location);
        this.surfaceType = surface;
        this.isIndoor = indoor;
        this.hasLighting = lights;
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Type: %s, Surface: %s, Floodlights: %s",
                (isIndoor ? "Indoor Court" : "Outdoor Ground"), surfaceType, (hasLighting ? "Yes (Night Play OK)" : "Daylight Only"));
    }

    @Override
    public double getHourlyRate() { return 50.0; }
}

abstract class User {
    protected String userId;
    protected String name;
    protected String role;
    protected String email;
    protected String department;

    public User(String userId, String name, String role, String email, String department) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.email = email;
        this.department = department;
    }

    public abstract int getMaxBookingHours();
    public abstract boolean canBookAuditoriumDirectly();

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Role: %-8s | Dept: %-15s", userId, name, role, department);
    }
}

class Student extends User {
    private String regNo;

    public Student(String userId, String name, String email, String department, String regNo) {
        super(userId, name, "Student", email, department);
        this.regNo = regNo;
    }

    public String getRegNo() { return regNo; }

    @Override
    public int getMaxBookingHours() { return 2; } // Max 2 hours for students

    @Override
    public boolean canBookAuditoriumDirectly() { return false; } // Student requires faculty endorsement

    @Override
    public String toString() {
        return super.toString() + " | RegNo: " + regNo;
    }
}

class FacultyMember extends User {
    private String employeeId;
    private String designation;

    public FacultyMember(String userId, String name, String email, String department, String empId, String designation) {
        super(userId, name, "Faculty", email, department);
        this.employeeId = empId;
        this.designation = designation;
    }

    public String getEmployeeId() { return employeeId; }
    public String getDesignation() { return designation; }

    @Override
    public int getMaxBookingHours() { return 6; } // Faculty can book up to 6 hours

    @Override
    public boolean canBookAuditoriumDirectly() { return true; }

    @Override
    public String toString() {
        return super.toString() + " | " + designation + " (" + employeeId + ")";
    }
}

class Booking {
    private String bookingId;
    private String userId;
    private String facilityId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status; // CONFIRMED, CANCELLED
    private String purpose;

    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public Booking(String bookingId, String userId, String facilityId, LocalDate date, 
                   LocalTime startTime, LocalTime endTime, String status, String purpose) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.facilityId = facilityId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.purpose = purpose;
    }

    public boolean overlapsWith(LocalDate reqDate, LocalTime reqStart, LocalTime reqEnd) {
        if ("CANCELLED".equalsIgnoreCase(this.status)) return false;
        if (!this.date.equals(reqDate)) return false;
        return this.startTime.isBefore(reqEnd) && this.endTime.isAfter(reqStart);
    }

    public String toFileString() {
        return String.format("BKG|%s|%s|%s|%s|%s|%s|%s|%s",
                bookingId, userId, facilityId, date, 
                startTime.format(TIME_FMT), endTime.format(TIME_FMT), status, purpose);
    }

    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getFacilityId() { return facilityId; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPurpose() { return purpose; }

    @Override
    public String toString() {
        return String.format("[%s] User: %-6s | Facility: %-5s | Date: %s | Time: %s-%s | Status: %-9s | %s",
                bookingId, userId, facilityId, date, startTime.format(TIME_FMT), endTime.format(TIME_FMT), status, purpose);
    }
}

class CampusDatabase {
    private static final String DATA_FILE = "campus_data.txt";
    private Map<String, Facility> facilities = new LinkedHashMap<>();
    private Map<String, User> users = new LinkedHashMap<>();
    private List<Booking> bookings = new ArrayList<>();

    public CampusDatabase() { loadData(); }

    public void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            initDefaultSeedData();
            saveData();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            facilities.clear();
            users.clear();
            bookings.clear();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|", -1);
                String tag = p[0].trim().toUpperCase();

                if ("FAC".equals(tag) && p.length >= 6) {
                    String id = p[1].trim();
                    String name = p[2].trim();
                    String type = p[3].trim();
                    int cap = Integer.parseInt(p[4].trim());
                    String loc = p[5].trim();
                    String extra = p.length > 6 ? p[6].trim() : "";

                    if ("Lab".equalsIgnoreCase(type)) {
                        facilities.put(id, new Lab(id, name, cap, loc, cap, "Ubuntu/Windows", extra.toLowerCase().contains("gpu")));
                    } else if ("SportsCourt".equalsIgnoreCase(type) || "Sports".equalsIgnoreCase(type)) {
                        facilities.put(id, new SportsCourt(id, name, cap, loc, "Synthetic/Wood", extra.toLowerCase().contains("indoor"), true));
                    } else {
                        facilities.put(id, new Hall(id, name, cap, loc, true, true, cap > 100));
                    }
                } else if ("USR".equals(tag) && p.length >= 6) {
                    String id = p[1].trim();
                    String name = p[2].trim();
                    String role = p[3].trim();
                    String email = p[4].trim();
                    String dept = p[5].trim();
                    String extra = p.length > 6 ? p[6].trim() : "";

                    if ("Faculty".equalsIgnoreCase(role)) {
                        users.put(id, new FacultyMember(id, name, email, dept, "EMP-" + id, extra));
                    } else {
                        users.put(id, new Student(id, name, email, dept, extra.contains(":") ? extra.split(":")[1].trim() : "REG-" + id));
                    }
                } else if ("BKG".equals(tag) && p.length >= 9) {
                    bookings.add(new Booking(p[1].trim(), p[2].trim(), p[3].trim(), 
                            LocalDate.parse(p[4].trim()), LocalTime.parse(p[5].trim()), LocalTime.parse(p[6].trim()), 
                            p[7].trim(), p[8].trim()));
                }
            }
        } catch (Exception e) {
            initDefaultSeedData();
        }
    }

    public void saveData() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(DATA_FILE), StandardCharsets.UTF_8))) {
            pw.println("# SMART CAMPUS FACILITY & LAB BOOKING DATABASE");
            for (Facility f : facilities.values()) {
                pw.printf("FAC|%s|%s|%s|%d|%s|%s\n", f.getId(), f.getName(), f.getType(), f.getCapacity(), f.getLocation(), f.getSpecificDetails());
            }
            for (User u : users.values()) {
                String extra = (u instanceof Student) ? ("RegNo: " + ((Student)u).getRegNo()) : ((FacultyMember)u).getDesignation();
                pw.printf("USR|%s|%s|%s|%s|%s|%s\n", u.getUserId(), u.getName(), u.getRole(), u.getEmail(), u.getDepartment(), extra);
            }
            for (Booking b : bookings) {
                pw.println(b.toFileString());
            }
        } catch (Exception ignored) {}
    }

    private void initDefaultSeedData() {
        facilities.clear();
        users.clear();
        bookings.clear();

        facilities.put("F101", new Lab("F101", "Alan Turing Computer Lab", 45, "Tech Block 2nd Floor", 45, "Linux Workstations", true));
        facilities.put("F102", new Lab("F102", "Embedded Systems & IoT Lab", 30, "Electronics Block 1st Floor", 30, "Hardware Kits", true));
        facilities.put("F201", new Hall("F201", "Aryabhata Auditorium", 250, "Main Academic Block Ground Floor", true, true, true));
        facilities.put("F202", new Hall("F202", "APJ Abdul Kalam Seminar Hall", 120, "Science Block 3rd Floor", true, true, true));
        facilities.put("F301", new SportsCourt("F301", "Indoor Badminton Court A", 4, "Sports Complex", "Synthetic Mat", true, true));
        facilities.put("F302", new SportsCourt("F302", "Outdoor Tennis Court", 2, "Sports Ground", "Clay Surface", false, true));

        users.put("U1001", new Student("U1001", "Aarav Sharma", "aarav@campus.edu", "Computer Science", "24BCE1042"));
        users.put("U1002", new Student("U1002", "Sneha Patel", "sneha@campus.edu", "Electronics", "24BEE1089"));
        users.put("U1003", new Student("U1003", "Rohan Verma", "rohan@campus.edu", "Mechanical", "24BME1015"));
        users.put("F2001", new FacultyMember("F2001", "Dr. K. Ramanathan", "ramanathan@campus.edu", "Computer Science", "FAC8042", "Senior Professor"));
        users.put("F2002", new FacultyMember("F2002", "Dr. Ananya Sen", "ananya@campus.edu", "Physics", "FAC7719", "Associate Professor"));

        bookings.add(new Booking("B101", "U1001", "F101", LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(12, 0), "CONFIRMED", "AI Project Lab Work"));
        bookings.add(new Booking("B102", "F2001", "F201", LocalDate.now().plusDays(3), LocalTime.of(14, 0), LocalTime.of(16, 0), "CONFIRMED", "Guest Lecture on Cloud Systems"));
        bookings.add(new Booking("B103", "U1002", "F301", LocalDate.now().plusDays(2), LocalTime.of(17, 0), LocalTime.of(18, 0), "CONFIRMED", "Badminton Match Practice"));
    }

    public Map<String, Facility> getFacilities() { return facilities; }
    public Map<String, User> getUsers() { return users; }
    public List<Booking> getBookings() { return bookings; }
    public Facility getFacility(String id) { return facilities.get(id.toUpperCase().trim()); }
    public User getUser(String id) { return users.get(id.toUpperCase().trim()); }

    public synchronized Booking createBooking(String userId, String facilityId, LocalDate date, LocalTime start, LocalTime end, String purpose) throws CampusBookingException {
        User user = getUser(userId);
        if (user == null) throw new InvalidInputException("User ID not registered: " + userId);

        Facility fac = getFacility(facilityId);
        if (fac == null) throw new InvalidInputException("Facility ID not recognized: " + facilityId);

        if (fac.isUnderMaintenance()) throw new CampusBookingException("Facility is currently under maintenance.");

        if (start.isAfter(end) || start.equals(end)) {
            throw new InvalidInputException("Start time must be strictly before end time.");
        }

        double duration = java.time.Duration.between(start, end).toMinutes() / 60.0;
        if (duration > user.getMaxBookingHours()) {
            throw new InvalidInputException(String.format("Requested duration (%.1f hrs) exceeds allowed limit of %d hrs for %s.", duration, user.getMaxBookingHours(), user.getRole()));
        }

        if (fac instanceof Hall && fac.getCapacity() > 150 && !user.canBookAuditoriumDirectly()) {
            throw new CampusBookingException("Students cannot directly book grand auditoriums. Faculty advisor endorsement required.");
        }

        for (Booking b : bookings) {
            if (b.getFacilityId().equalsIgnoreCase(facilityId) && b.overlapsWith(date, start, end)) {
                throw new SlotUnavailableException(String.format("Slot %s-%s on %s is already reserved (Booking %s: %s-%s).",
                        start, end, date, b.getBookingId(), b.getStartTime(), b.getEndTime()));
            }
        }

        String nextId = "B" + (100 + bookings.size() + 1);
        Booking newB = new Booking(nextId, user.getUserId(), fac.getId(), date, start, end, "CONFIRMED", purpose);
        bookings.add(newB);
        saveData();
        return newB;
    }

    public synchronized boolean cancelBooking(String bookingId, String requestingUserId) throws CampusBookingException {
        Booking target = null;
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(bookingId)) { target = b; break; }
        }
        if (target == null) throw new CampusBookingException("Booking ID '" + bookingId + "' not found.");
        if ("CANCELLED".equalsIgnoreCase(target.getStatus())) throw new CampusBookingException("Booking is already cancelled.");

        User req = getUser(requestingUserId);
        boolean isOwner = target.getUserId().equalsIgnoreCase(requestingUserId);
        boolean isFaculty = req != null && req instanceof FacultyMember;

        if (!isOwner && !isFaculty) throw new CampusBookingException("Permission denied. Only booking owner or faculty can cancel.");

        target.setStatus("CANCELLED");
        saveData();
        return true;
    }
}

public class Main {
    private static CampusDatabase db = new CampusDatabase();
    private static User currentUser;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length > 0) {
            String flag = args[0].toLowerCase();
            if (flag.equals("--test")) { runAutomatedTests(); return; }
            else if (flag.equals("--demo")) { runAutomatedDemo(); return; }
            else if (flag.equals("--help") || flag.equals("-h")) {
                System.out.println("Smart Campus Facility & Lab Booking System");
                System.out.println("  (no args)  : Launch interactive console UI");
                System.out.println("  --test     : Run automated grading test suite");
                System.out.println("  --demo     : Run non-interactive demonstration");
                return;
            }
        }

        currentUser = db.getUser("U1001"); // Default session to Aarav Sharma
        boolean exit = false;

        System.out.println("==================================================================");
        System.out.println("           SMART CAMPUS FACILITY & LAB BOOKING ENGINE             ");
        System.out.println("           Object-Oriented Programming in Java Project            ");
        System.out.println("==================================================================");

        while (!exit) {
            System.out.println("\nActive User: " + (currentUser != null ? currentUser.getName() + " (" + currentUser.getRole() + ")" : "None"));
            System.out.println("------------------------- MAIN MENU ------------------------------");
            System.out.println("  1. Browse All Campus Facilities");
            System.out.println("  2. Check Facility Availability Schedule");
            System.out.println("  3. Make a New Reservation");
            System.out.println("  4. View My Current Bookings");
            System.out.println("  5. Cancel a Reservation");
            System.out.println("  6. View Campus Analytics & Usage Report");
            System.out.println("  7. Switch Active User Profile");
            System.out.println("  8. Run System Self-Diagnostics (Test Suite)");
            System.out.println("  0. Save and Exit");
            System.out.println("------------------------------------------------------------------");
            System.out.print("Select an option [0-8]: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1": handleListFacilities(); break;
                case "2": handleCheckSchedule(); break;
                case "3": handleMakeBooking(); break;
                case "4": handleViewMyBookings(); break;
                case "5": handleCancelBooking(); break;
                case "6": printAnalytics(); break;
                case "7": handleSwitchUser(); break;
                case "8": runAutomatedTests(); break;
                case "0":
                    db.saveData();
                    System.out.println("\nSaving data to campus_data.txt...");
                    System.out.println("Thank you for using Smart Campus Facility Booking System. Goodbye!");
                    exit = true;
                    break;
                default: System.out.println("Invalid selection. Please choose 0 to 8.");
            }
        }
    }

    private static void handleListFacilities() {
        System.out.println("\n--- CAMPUS FACILITIES CATALOG ---");
        for (Facility f : db.getFacilities().values()) {
            System.out.println(f);
            System.out.println("    Specs: " + f.getSpecificDetails());
        }
    }

    private static void handleCheckSchedule() {
        System.out.println("\n--- CHECK FACILITY SCHEDULE ---");
        System.out.print("Enter Facility ID (e.g. F101, F201, F301): ");
        String fid = scanner.nextLine().trim();
        Facility f = db.getFacility(fid);
        if (f == null) { System.out.println("Facility not found."); return; }

        System.out.print("Enter Date (YYYY-MM-DD) [Enter for today]: ");
        String ds = scanner.nextLine().trim();
        LocalDate d = ds.isEmpty() ? LocalDate.now() : LocalDate.parse(ds);

        System.out.println("\nConfirmed Bookings for " + f.getName() + " on " + d + ":");
        boolean any = false;
        for (Booking b : db.getBookings()) {
            if (b.getFacilityId().equalsIgnoreCase(fid) && b.getDate().equals(d) && "CONFIRMED".equalsIgnoreCase(b.getStatus())) {
                System.out.println("  * " + b.getStartTime() + " to " + b.getEndTime() + " | Purpose: " + b.getPurpose() + " (Reserved by " + b.getUserId() + ")");
                any = true;
            }
        }
        if (!any) System.out.println("  * No bookings found. The facility is fully available!");
    }

    private static void handleMakeBooking() {
        System.out.println("\n--- CREATE NEW FACILITY RESERVATION ---");
        if (currentUser == null) { System.out.println("Please login / switch user first."); return; }
        System.out.println("Current User: " + currentUser.getName() + " | Max Allowed Duration: " + currentUser.getMaxBookingHours() + " hour(s)");

        try {
            System.out.print("Enter Facility ID: ");
            String fid = scanner.nextLine().trim();
            System.out.print("Enter Date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Enter Start Time (HH:mm in 24h, e.g. 10:00): ");
            LocalTime start = LocalTime.parse(scanner.nextLine().trim());
            System.out.print("Enter End Time (HH:mm in 24h, e.g. 12:00): ");
            LocalTime end = LocalTime.parse(scanner.nextLine().trim());
            System.out.print("Enter Purpose of Reservation: ");
            String purpose = scanner.nextLine().trim();

            Booking b = db.createBooking(currentUser.getUserId(), fid, date, start, end, purpose);
            System.out.println("\nSUCCESS: Reservation confirmed!");
            System.out.println("Booking ID: " + b.getBookingId() + " | " + db.getFacility(fid).getName() + " | " + date + " " + start + "-" + end);
        } catch (DateTimeParseException e) {
            System.out.println("Date/Time format error. Please use YYYY-MM-DD and HH:mm.");
        } catch (CampusBookingException e) {
            System.out.println("BOOKING REJECTED: " + e.getMessage());
        }
    }

    private static void handleViewMyBookings() {
        System.out.println("\n--- RESERVATIONS FOR " + currentUser.getName().toUpperCase() + " ---");
        boolean any = false;
        for (Booking b : db.getBookings()) {
            if (b.getUserId().equalsIgnoreCase(currentUser.getUserId())) {
                System.out.println(b);
                any = true;
            }
        }
        if (!any) System.out.println("No recorded bookings for current user.");
    }

    private static void handleCancelBooking() {
        System.out.println("\n--- CANCEL A RESERVATION ---");
        System.out.print("Enter Booking ID to cancel (e.g. B101): ");
        String bid = scanner.nextLine().trim();
        try {
            db.cancelBooking(bid, currentUser.getUserId());
            System.out.println("SUCCESS: Reservation " + bid + " cancelled successfully.");
        } catch (CampusBookingException e) {
            System.out.println("CANCELLATION FAILED: " + e.getMessage());
        }
    }

    private static void handleSwitchUser() {
        System.out.println("\n--- AVAILABLE USERS ---");
        for (User u : db.getUsers().values()) System.out.println("  " + u);
        System.out.print("Enter User ID to switch to: ");
        String uid = scanner.nextLine().trim();
        User u = db.getUser(uid);
        if (u != null) {
            currentUser = u;
            System.out.println("Switched session to: " + u.getName() + " (" + u.getRole() + ")");
        } else {
            System.out.println("User not found.");
        }
    }

    private static void printAnalytics() {
        System.out.println("\n========================================================");
        System.out.println("          CAMPUS FACILITY USAGE & ANALYTICS REPORT       ");
        System.out.println("========================================================");
        System.out.println("Total Facilities Registered : " + db.getFacilities().size());
        System.out.println("Total Registered Users      : " + db.getUsers().size());
        System.out.println("Total Reservations Logged   : " + db.getBookings().size());

        long conf = db.getBookings().stream().filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus())).count();
        long canc = db.getBookings().stream().filter(b -> "CANCELLED".equalsIgnoreCase(b.getStatus())).count();
        System.out.println("Active Confirmed Bookings   : " + conf);
        System.out.println("Cancelled Bookings          : " + canc);

        System.out.println("\n--- Booking Count by Facility ---");
        Map<String, Long> countMap = db.getBookings().stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.groupingBy(Booking::getFacilityId, Collectors.counting()));
        for (Facility f : db.getFacilities().values()) {
            System.out.printf("  * [%s] %-30s: %d bookings\n", f.getId(), f.getName(), countMap.getOrDefault(f.getId(), 0L));
        }
        System.out.println("========================================================\n");
    }

    public static void runAutomatedDemo() {
        System.out.println("==================================================================");
        System.out.println("       RUNNING NON-INTERACTIVE HEADLESS DEMONSTRATION MODE        ");
        System.out.println("==================================================================");
        CampusDatabase d = new CampusDatabase();
        System.out.println("\n[1] Current Facilities:");
        for (Facility f : d.getFacilities().values()) System.out.println("  * " + f);

        System.out.println("\n[2] Performing Sample Booking for Dr. Ramanathan (F2001) in Badminton Court:");
        try {
            Booking b = d.createBooking("F2001", "F301", LocalDate.now().plusDays(5), LocalTime.of(18, 0), LocalTime.of(19, 30), "Faculty Badminton");
            System.out.println("  Booking Confirmed: " + b);
        } catch (Exception e) { System.out.println("  Notice: " + e.getMessage()); }

        System.out.println("\n[3] Generating Analytics:");
        printAnalytics();
        System.out.println("Demonstration completed successfully.");
    }

    public static void runAutomatedTests() {
        System.out.println("\n========================================================");
        System.out.println("     RUNNING AUTOMATED SYSTEM & CONFLICT TEST SUITE     ");
        System.out.println("========================================================");
        int passed = 0, total = 6;
        CampusDatabase testDb = new CampusDatabase();

        System.out.print("[TEST 1] Verifying Facility & User Catalog Loading .... ");
        if (testDb.getFacilities().size() >= 4 && testDb.getUsers().size() >= 3) {
            System.out.println("PASS (Facilities: " + testDb.getFacilities().size() + ", Users: " + testDb.getUsers().size() + ")");
            passed++;
        } else { System.out.println("FAIL"); }

        System.out.print("[TEST 2] Testing Standard Booking Creation ............. ");
        LocalDate tDate = LocalDate.now().plusDays(30);
        testDb.getBookings().removeIf(b -> !b.getDate().isBefore(tDate));
        try {
            Booking b = testDb.createBooking("U1001", "F102", tDate, LocalTime.of(9, 0), LocalTime.of(10, 30), "IoT Sensor Calibration");
            if (b != null && "CONFIRMED".equals(b.getStatus())) {
                System.out.println("PASS (Created ID: " + b.getBookingId() + ")");
                passed++;
            } else { System.out.println("FAIL"); }
        } catch (Exception e) { System.out.println("FAIL (" + e.getMessage() + ")"); }

        System.out.print("[TEST 3] Testing Slot Conflict & Overlap Prevention ... ");
        try {
            testDb.createBooking("U1002", "F102", tDate, LocalTime.of(9, 30), LocalTime.of(11, 0), "Double-booking attempt");
            System.out.println("FAIL (Double-booking allowed!)");
        } catch (SlotUnavailableException e) {
            System.out.println("PASS (Prevented collision: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) { System.out.println("FAIL (" + e.getMessage() + ")"); }

        System.out.print("[TEST 4] Testing Student Max Duration Limit (2 hrs) .... ");
        try {
            testDb.createBooking("U1003", "F301", tDate.plusDays(1), LocalTime.of(14, 0), LocalTime.of(18, 0), "Overlimit session");
            System.out.println("FAIL (Student booked 4 hours)");
        } catch (InvalidInputException e) {
            System.out.println("PASS (Quota enforced: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) { System.out.println("FAIL (" + e.getMessage() + ")"); }

        System.out.print("[TEST 5] Testing Grand Auditorium Booking Restriction . ");
        try {
            testDb.createBooking("U1001", "F201", tDate.plusDays(2), LocalTime.of(10, 0), LocalTime.of(11, 30), "Student fest");
            System.out.println("FAIL (Student booked Grand Auditorium directly)");
        } catch (CampusBookingException e) {
            System.out.println("PASS (Restriction enforced: " + e.getMessage() + ")");
            passed++;
        }

        System.out.print("[TEST 6] Testing Cancellation & Slot Freeing ........... ");
        try {
            LocalDate cDate = tDate.plusDays(3);
            Booking temp = testDb.createBooking("F2001", "F302", cDate, LocalTime.of(15, 0), LocalTime.of(17, 0), "Faculty Tennis");
            testDb.cancelBooking(temp.getBookingId(), "F2001");
            Booking rebooked = testDb.createBooking("U1002", "F302", cDate, LocalTime.of(15, 0), LocalTime.of(17, 0), "Student Tennis Club");
            if ("CONFIRMED".equals(rebooked.getStatus())) {
                System.out.println("PASS (Slot freed upon cancellation and re-booked)");
                passed++;
            } else { System.out.println("FAIL"); }
        } catch (Exception e) { System.out.println("FAIL (" + e.getMessage() + ")"); }

        System.out.println("========================================================");
        System.out.printf("Test Summary: %d / %d Tests Passed (Score: %.1f%%)\n", passed, total, (passed * 100.0 / total));
        System.out.println("========================================================\n");
    }
}
