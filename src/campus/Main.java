package campus;

import campus.exceptions.BookingException;
import campus.models.*;
import campus.service.BookingService;
import campus.service.TestRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Main application entry point for the Campus Facility Booking System.
 * Supports interactive menu mode and non-interactive CLI flags (--demo, --test).
 */
public class Main {

    private static final String DATA_FILE = "data/campus_data.txt";
    private static BookingService service;
    private static User currentUser;
    private static Scanner scanner;

    public static void main(String[] args) {
        // Handle command line flags for automated evaluation
        if (args.length > 0) {
            String flag = args[0].toLowerCase();
            if (flag.equals("--test")) {
                boolean allPassed = TestRunner.runAllTests(DATA_FILE);
                System.exit(allPassed ? 0 : 1);
            } else if (flag.equals("--demo")) {
                runAutomatedDemo();
                System.exit(0);
            } else if (flag.equals("--help") || flag.equals("-h")) {
                printHelp();
                System.exit(0);
            }
        }

        // Initialize interactive terminal interface
        service = new BookingService(DATA_FILE);
        scanner = new Scanner(System.in);

        // Default login to first student for easy demonstration
        currentUser = service.getUser("U1001");

        printWelcomeBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Select an option [0-8]: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleListFacilities();
                    break;
                case "2":
                    handleCheckSchedule();
                    break;
                case "3":
                    handleMakeBooking();
                    break;
                case "4":
                    handleViewMyBookings();
                    break;
                case "5":
                    handleCancelBooking();
                    break;
                case "6":
                    service.printAnalyticsReport();
                    break;
                case "7":
                    handleSwitchUser();
                    break;
                case "8":
                    TestRunner.runAllTests(DATA_FILE);
                    break;
                case "0":
                    System.out.println("\nSaving database state to " + DATA_FILE + "...");
                    service.saveAllData();
                    System.out.println("Thank you for using Campus Smart Booking System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please choose an option between 0 and 8.\n");
            }
        }

        scanner.close();
    }

    private static void printWelcomeBanner() {
        System.out.println("==================================================================");
        System.out.println("           SMART CAMPUS FACILITY & LAB BOOKING ENGINE             ");
        System.out.println("           Object-Oriented Programming in Java Project            ");
        System.out.println("==================================================================");
        if (currentUser != null) {
            System.out.println("Active Session: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        }
        System.out.println("------------------------------------------------------------------");
    }

    private static void printMainMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("  1. Browse All Campus Facilities");
        System.out.println("  2. Check Facility Availability Schedule");
        System.out.println("  3. Make a New Reservation");
        System.out.println("  4. View My Current Bookings");
        System.out.println("  5. Cancel a Reservation");
        System.out.println("  6. View Campus Analytics & Usage Report");
        System.out.println("  7. Switch Active User Profile");
        System.out.println("  8. Run System Self-Diagnostics (Test Suite)");
        System.out.println("  0. Save and Exit");
        System.out.println("---------------------------------------------");
    }

    private static void handleListFacilities() {
        System.out.println("\n--- CAMPUS FACILITIES CATALOG ---");
        for (Facility f : service.getAllFacilities()) {
            System.out.println(f.toString());
            System.out.println("    Specs: " + f.getSpecificDetails());
        }
    }

    private static void handleCheckSchedule() {
        System.out.println("\n--- CHECK FACILITY SCHEDULE ---");
        System.out.print("Enter Facility ID (e.g. F101, F201, F301): ");
        String facId = scanner.nextLine().trim();
        Facility f = service.getFacility(facId);
        if (f == null) {
            System.out.println("Facility with ID " + facId + " does not exist.");
            return;
        }

        System.out.print("Enter Date (YYYY-MM-DD) [or press Enter for today]: ");
        String dateStr = scanner.nextLine().trim();
        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);

        List<Booking> list = service.getFacilitySchedule(facId, date);
        System.out.println("\nConfirmed Bookings for " + f.getName() + " on " + date + ":");
        if (list.isEmpty()) {
            System.out.println("  * No bookings found. The facility is fully available!");
        } else {
            for (Booking b : list) {
                System.out.println("  * " + b.getStartTime() + " to " + b.getEndTime() + 
                                   " | Purpose: " + b.getPurpose() + " (Reserved by " + b.getUserId() + ")");
            }
        }
    }

    private static void handleMakeBooking() {
        System.out.println("\n--- CREATE NEW FACILITY RESERVATION ---");
        if (currentUser == null) {
            System.out.println("Please login / switch user first.");
            return;
        }

        System.out.println("Current User: " + currentUser.getName() + " | Max Allowed Duration: " 
                + currentUser.getMaxBookingHours() + " hour(s)");

        System.out.print("Enter Facility ID: ");
        String facId = scanner.nextLine().trim();

        System.out.print("Enter Date (YYYY-MM-DD, e.g. 2026-09-25): ");
        String dateStr = scanner.nextLine().trim();
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Expected YYYY-MM-DD.");
            return;
        }

        System.out.print("Enter Start Time (HH:mm in 24h format, e.g. 10:00): ");
        String startStr = scanner.nextLine().trim();
        LocalTime start;
        try {
            start = LocalTime.parse(startStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid start time format. Expected HH:mm.");
            return;
        }

        System.out.print("Enter End Time (HH:mm in 24h format, e.g. 12:00): ");
        String endStr = scanner.nextLine().trim();
        LocalTime end;
        try {
            end = LocalTime.parse(endStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid end time format. Expected HH:mm.");
            return;
        }

        System.out.print("Enter Purpose of Reservation: ");
        String purpose = scanner.nextLine().trim();

        try {
            Booking b = service.createBooking(currentUser.getUserId(), facId, date, start, end, purpose);
            System.out.println("\nSUCCESS: Reservation confirmed!");
            System.out.println("Booking Reference : " + b.getBookingId());
            System.out.println("Facility          : " + service.getFacility(facId).getName());
            System.out.println("Reserved Slot     : " + date + " from " + start + " to " + end);
            System.out.println("Status            : " + b.getStatus());
        } catch (BookingException e) {
            System.out.println("\nBOOKING REJECTED: " + e.getMessage());
        }
    }

    private static void handleViewMyBookings() {
        System.out.println("\n--- RESERVATIONS FOR " + currentUser.getName().toUpperCase() + " ---");
        List<Booking> list = service.getBookingsForUser(currentUser.getUserId());
        if (list.isEmpty()) {
            System.out.println("You have no recorded bookings.");
        } else {
            for (Booking b : list) {
                System.out.println(b);
            }
        }
    }

    private static void handleCancelBooking() {
        System.out.println("\n--- CANCEL A RESERVATION ---");
        System.out.print("Enter Booking ID to cancel (e.g. B101): ");
        String bId = scanner.nextLine().trim();

        try {
            service.cancelBooking(bId, currentUser.getUserId());
            System.out.println("SUCCESS: Reservation " + bId + " has been cancelled.");
        } catch (BookingException e) {
            System.out.println("CANCELLATION FAILED: " + e.getMessage());
        }
    }

    private static void handleSwitchUser() {
        System.out.println("\n--- AVAILABLE USERS ---");
        for (User u : service.getAllUsers()) {
            System.out.println("  " + u.toString());
        }
        System.out.print("Enter User ID to switch to: ");
        String uid = scanner.nextLine().trim();
        User target = service.getUser(uid);
        if (target != null) {
            currentUser = target;
            System.out.println("Switched session to: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        } else {
            System.out.println("User not found with ID: " + uid);
        }
    }

    private static void runAutomatedDemo() {
        System.out.println("==================================================================");
        System.out.println("       RUNNING NON-INTERACTIVE HEADLESS DEMONSTRATION MODE        ");
        System.out.println("==================================================================");

        BookingService demoService = new BookingService(DATA_FILE);

        System.out.println("\n[1] Current Facilities in System:");
        for (Facility f : demoService.getAllFacilities()) {
            System.out.println("  * " + f);
        }

        System.out.println("\n[2] Attempting a Sample Booking for Faculty F2001 on Badminton Court...");
        try {
            Booking demoBooking = demoService.createBooking(
                    "F2001", "F301", LocalDate.now().plusDays(7),
                    LocalTime.of(18, 0), LocalTime.of(19, 30), "Faculty Evening Fitness");
            System.out.println("  Successfully generated booking: " + demoBooking);
        } catch (Exception e) {
            System.out.println("  Booking notice: " + e.getMessage());
        }

        System.out.println("\n[3] Generating Analytics Summary:");
        demoService.printAnalyticsReport();

        System.out.println("Demonstration run completed successfully.");
    }

    private static void printHelp() {
        System.out.println("Campus Facility Booking System - CLI Options:");
        System.out.println("  (no args)       : Launch interactive terminal menu");
        System.out.println("  --test          : Execute automated unit and conflict test suite");
        System.out.println("  --demo          : Run non-interactive headless demonstration");
        System.out.println("  --help, -h      : Print this help message");
    }
}
