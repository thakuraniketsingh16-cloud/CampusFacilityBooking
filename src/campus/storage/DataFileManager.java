package campus.storage;

import campus.models.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Handles all file I/O operations for reading and writing 
 * the single-file text database (data/campus_data.txt).
 */
public class DataFileManager {

    private String dataFilePath;

    public DataFileManager(String dataFilePath) {
        this.dataFilePath = resolveFilePath(dataFilePath);
    }

    /**
     * Resolves path whether running from project root or inside bin folder.
     */
    private String resolveFilePath(String path) {
        File direct = new File(path);
        if (direct.exists()) {
            return direct.getAbsolutePath();
        }
        File parentDir = new File(".." + File.separator + path);
        if (parentDir.exists()) {
            return parentDir.getAbsolutePath();
        }
        // Fallback to direct path
        return path;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    /**
     * Loads all facilities, users, and bookings from the single text file.
     */
    public boolean loadData(Map<String, Facility> facilityMap, 
                            Map<String, User> userMap, 
                            List<Booking> bookingList) {
        File file = new File(dataFilePath);
        if (!file.exists()) {
            System.out.println("Notice: Database file not found at " + dataFilePath + ". Initializing defaults.");
            createDefaultDatabaseFile();
            // Re-resolve
            this.dataFilePath = resolveFilePath(dataFilePath);
            file = new File(dataFilePath);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim();
                // Skip empty lines or comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] tokens = line.split("\\|", -1);
                if (tokens.length < 2) {
                    continue; // Skip malformed row
                }

                String recordType = tokens[0].trim().toUpperCase();

                try {
                    switch (recordType) {
                        case "FAC":
                            parseFacility(tokens, facilityMap);
                            break;
                        case "USR":
                            parseUser(tokens, userMap);
                            break;
                        case "BKG":
                            parseBooking(tokens, bookingList);
                            break;
                        default:
                            // Unknown record type, ignore gracefully
                            break;
                    }
                } catch (Exception ex) {
                    System.out.println("Warning: Could not parse line " + lineCount + ": " + ex.getMessage());
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error reading database file: " + e.getMessage());
            return false;
        }
    }

    private void parseFacility(String[] tokens, Map<String, Facility> facilityMap) {
        // FAC|ID|Name|Type|Capacity|Location|ExtraDetail
        if (tokens.length < 6) return;
        String id = tokens[1].trim();
        String name = tokens[2].trim();
        String type = tokens[3].trim();
        int capacity = Integer.parseInt(tokens[4].trim());
        String location = tokens[5].trim();
        String extra = tokens.length > 6 ? tokens[6].trim() : "";

        Facility f;
        if ("Lab".equalsIgnoreCase(type)) {
            f = new Lab(id, name, capacity, location, extra);
        } else if ("SportsCourt".equalsIgnoreCase(type) || "Sports".equalsIgnoreCase(type)) {
            f = new SportsCourt(id, name, capacity, location, extra);
        } else {
            f = new Hall(id, name, capacity, location, extra);
        }
        facilityMap.put(id, f);
    }

    private void parseUser(String[] tokens, Map<String, User> userMap) {
        // USR|ID|Name|Role|Email|Department|ExtraDetail
        if (tokens.length < 6) return;
        String id = tokens[1].trim();
        String name = tokens[2].trim();
        String role = tokens[3].trim();
        String email = tokens[4].trim();
        String dept = tokens[5].trim();
        String extra = tokens.length > 6 ? tokens[6].trim() : "";

        User u;
        if ("Faculty".equalsIgnoreCase(role)) {
            u = new Faculty(id, name, email, dept, extra);
        } else {
            u = new Student(id, name, email, dept, extra);
        }
        userMap.put(id, u);
    }

    private void parseBooking(String[] tokens, List<Booking> bookingList) {
        // BKG|BookingID|UserID|FacilityID|Date|StartTime|EndTime|Status|Purpose
        if (tokens.length < 9) return;
        String bId = tokens[1].trim();
        String uId = tokens[2].trim();
        String fId = tokens[3].trim();
        LocalDate date = LocalDate.parse(tokens[4].trim());
        LocalTime start = LocalTime.parse(tokens[5].trim());
        LocalTime end = LocalTime.parse(tokens[6].trim());
        String status = tokens[7].trim();
        String purpose = tokens[8].trim();

        Booking b = new Booking(bId, uId, fId, date, start, end, status, purpose);
        bookingList.add(b);
    }

    /**
     * Saves all in-memory records back into the single text database file.
     */
    public synchronized boolean saveData(Collection<Facility> facilities, 
                                        Collection<User> users, 
                                        List<Booking> bookings) {
        File file = new File(dataFilePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {

            writer.write("# ==========================================================\n");
            writer.write("# CAMPUS FACILITY AND LAB BOOKING SYSTEM - DATABASE FILE\n");
            writer.write("# Format Specifications:\n");
            writer.write("# Facility: FAC|ID|Name|Type|Capacity|Location|ExtraDetail\n");
            writer.write("# User:     USR|ID|Name|Role|Email|Department|ExtraDetail\n");
            writer.write("# Booking:  BKG|BookingID|UserID|FacilityID|Date|StartTime|EndTime|Status|Purpose\n");
            writer.write("# ==========================================================\n\n");

            // 1. Write Facilities
            writer.write("# --- FACILITIES ---\n");
            for (Facility f : facilities) {
                writer.write(String.format("FAC|%s|%s|%s|%d|%s|%s\n",
                        f.getId(), f.getName(), f.getType(), f.getCapacity(), 
                        f.getLocation(), f.getSpecificDetails()));
            }
            writer.write("\n");

            // 2. Write Users
            writer.write("# --- USERS ---\n");
            for (User u : users) {
                String extra = (u instanceof Student) ? ("RegNo: " + ((Student) u).getRegNumber())
                        : ("EmpId: " + ((Faculty) u).getEmployeeId() + ", " + ((Faculty) u).getDesignation());
                writer.write(String.format("USR|%s|%s|%s|%s|%s|%s\n",
                        u.getUserId(), u.getName(), u.getRole(), u.getEmail(), u.getDepartment(), extra));
            }
            writer.write("\n");

            // 3. Write Bookings
            writer.write("# --- BOOKINGS ---\n");
            for (Booking b : bookings) {
                writer.write(b.toFileString() + "\n");
            }

            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving database file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fallback to create seed file if missing.
     */
    private void createDefaultDatabaseFile() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        File file = new File("data" + File.separator + "campus_data.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("# --- FACILITIES ---");
            pw.println("FAC|F101|Alan Turing Computer Lab|Lab|45|Tech Block 2nd Floor|45 Linux Workstations");
            pw.println("FAC|F201|Aryabhata Auditorium|Hall|250|Main Academic Block|Dual Projectors, Sound");
            pw.println("FAC|F301|Indoor Badminton Court A|SportsCourt|4|Sports Complex|Indoor Wooden");
            pw.println("# --- USERS ---");
            pw.println("USR|U1001|Aarav Sharma|Student|aarav@campus.edu|Computer Science|RegNo: 24BCE1042");
            pw.println("USR|F2001|Dr. Ramanathan|Faculty|raman@campus.edu|Computer Science|EmpId: FAC8042");
            pw.println("# --- BOOKINGS ---");
            pw.println("BKG|B101|U1001|F101|2026-09-20|10:00|12:00|CONFIRMED|Sample Booking");
        } catch (Exception e) {
            System.err.println("Could not create default data file: " + e.getMessage());
        }
    }
}
