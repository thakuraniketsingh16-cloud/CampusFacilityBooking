package campus.service;

import campus.exceptions.*;
import campus.models.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Built-in automated test harness for validating business rules,
 * conflict resolution, quota enforcement, and persistence.
 * Suitable for automated grading scripts and CI/CD pipelines.
 */
public class TestRunner {

    public static boolean runAllTests(String dataPath) {
        System.out.println("\n========================================================");
        System.out.println("     RUNNING AUTOMATED SYSTEM & CONFLICT TEST SUITE     ");
        System.out.println("========================================================");

        int passed = 0;
        int total = 6;

        BookingService service = new BookingService(dataPath);

        // Test 1: Data loading test
        System.out.print("[TEST 1] Verifying Data File Loading ................... ");
        if (service.getAllFacilities().size() >= 3 && service.getAllUsers().size() >= 2) {
            System.out.println("PASS (Facilities: " + service.getAllFacilities().size() + ", Users: " + service.getAllUsers().size() + ")");
            passed++;
        } else {
            System.out.println("FAIL (Loaded facilities or users were insufficient)");
        }

        // Test 2: Standard valid booking
        System.out.print("[TEST 2] Testing Standard Booking Creation ............. ");
        LocalDate testDate = LocalDate.now().plusDays(5);
        try {
            Booking b = service.createBooking("U1001", "F102", testDate, 
                    LocalTime.of(9, 0), LocalTime.of(10, 30), "IoT Sensor Calibration");
            if (b != null && "CONFIRMED".equals(b.getStatus())) {
                System.out.println("PASS (Created ID: " + b.getBookingId() + ")");
                passed++;
            } else {
                System.out.println("FAIL (Booking returned null or unconfirmed)");
            }
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
        }

        // Test 3: Conflict detection (prevent double-booking same slot)
        System.out.print("[TEST 3] Testing Slot Conflict Detection .............. ");
        try {
            // Attempt overlapping reservation on same facility F102 at 09:30 - 11:00
            service.createBooking("U1002", "F102", testDate, 
                    LocalTime.of(9, 30), LocalTime.of(11, 0), "Contested overlapping slot");
            System.out.println("FAIL (Expected SlotUnavailableException was not thrown!)");
        } catch (SlotUnavailableException e) {
            System.out.println("PASS (Correctly prevented double-booking: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) {
            System.out.println("FAIL (Unexpected exception type: " + e.getClass().getSimpleName() + ")");
        }

        // Test 4: Duration limit check for student (Max 2 hrs)
        System.out.print("[TEST 4] Testing Student Max Duration Limit ............ ");
        try {
            // Attempt 4-hour reservation by student U1003
            service.createBooking("U1003", "F301", testDate.plusDays(1), 
                    LocalTime.of(14, 0), LocalTime.of(18, 0), "Exceeding duration practice");
            System.out.println("FAIL (Student should not be permitted to book 4 continuous hours!)");
        } catch (InvalidInputException e) {
            System.out.println("PASS (Quota enforced: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) {
            System.out.println("FAIL (Unexpected exception: " + e.getMessage() + ")");
        }

        // Test 5: Permission check (Student booking Grand Auditorium directly)
        System.out.print("[TEST 5] Testing Auditorium Booking Restriction ........ ");
        try {
            // Attempt booking F201 (capacity 250) by student U1001
            service.createBooking("U1001", "F201", testDate.plusDays(2), 
                    LocalTime.of(10, 0), LocalTime.of(11, 30), "Student fest prep");
            System.out.println("FAIL (Student booked Grand Auditorium without faculty endorsement!)");
        } catch (BookingException e) {
            System.out.println("PASS (Restriction enforced: " + e.getMessage() + ")");
            passed++;
        }

        // Test 6: Cancellation & Slot Re-booking
        System.out.print("[TEST 6] Testing Cancellation & Slot Freeing ........... ");
        try {
            LocalDate cancelDate = testDate.plusDays(3);
            Booking temp = service.createBooking("F2001", "F302", cancelDate, 
                    LocalTime.of(15, 0), LocalTime.of(17, 0), "Faculty Tennis");
            service.cancelBooking(temp.getBookingId(), "F2001");
            
            // Now another user should be able to book this exact slot
            Booking rebooked = service.createBooking("U1002", "F302", cancelDate, 
                    LocalTime.of(15, 0), LocalTime.of(17, 0), "Student Tennis Club");
            if ("CONFIRMED".equals(rebooked.getStatus())) {
                System.out.println("PASS (Slot freed upon cancellation and re-booked successfully)");
                passed++;
            } else {
                System.out.println("FAIL (Re-booking after cancel was not confirmed)");
            }
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
        }

        System.out.println("========================================================");
        System.out.printf("Test Summary: %d / %d Tests Passed (Score: %.1f%%)\n", 
                passed, total, (passed * 100.0 / total));
        System.out.println("========================================================\n");

        return passed == total;
    }
}
