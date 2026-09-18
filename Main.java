import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class HostelException extends Exception {
    public HostelException(String message) { super(message); }
}

class RoomFullException extends HostelException {
    public RoomFullException(String message) { super(message); }
}

class InvalidDataException extends HostelException {
    public InvalidDataException(String message) { super(message); }
}

abstract class Room {
    private String roomNumber;
    private int capacity;
    private int occupiedBeds;
    private double monthlyBaseRent;
    private boolean underMaintenance;

    public Room(String roomNumber, int capacity, double monthlyBaseRent) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.monthlyBaseRent = monthlyBaseRent;
        this.occupiedBeds = 0;
        this.underMaintenance = false;
    }

    public abstract String getRoomType();
    public abstract String getAmenities();
    public abstract double calculateTotalFee(int months);

    public boolean hasAvailableBed() {
        return !underMaintenance && (occupiedBeds < capacity);
    }

    public void allocateBed() throws RoomFullException {
        if (!hasAvailableBed()) {
            throw new RoomFullException("Room " + roomNumber + " is already at full capacity (" + capacity + "/" + capacity + ") or under maintenance.");
        }
        occupiedBeds++;
    }

    public void vacateBed() {
        if (occupiedBeds > 0) occupiedBeds--;
    }

    public String getRoomNumber() { return roomNumber; }
    public int getCapacity() { return capacity; }
    public int getOccupiedBeds() { return occupiedBeds; }
    public void setOccupiedBeds(int occupied) { this.occupiedBeds = occupied; }
    public double getMonthlyBaseRent() { return monthlyBaseRent; }
    public boolean isUnderMaintenance() { return underMaintenance; }
    public void setUnderMaintenance(boolean underMaintenance) { this.underMaintenance = underMaintenance; }

    @Override
    public String toString() {
        return String.format("[%s] Type: %-12s | Beds: %d/%d | Rent: Rs.%.0f/mo | Status: %s",
                roomNumber, getRoomType(), occupiedBeds, capacity, monthlyBaseRent,
                (underMaintenance ? "MAINTENANCE" : (hasAvailableBed() ? "AVAILABLE" : "FULL")));
    }
}

class StandardRoom extends Room {
    public StandardRoom(String roomNumber, int capacity, double monthlyBaseRent) {
        super(roomNumber, capacity, monthlyBaseRent);
    }

    @Override
    public String getRoomType() { return "Standard"; }

    @Override
    public String getAmenities() { return "Ceiling Fan, Study Table, Shared Washroom, Standard Wi-Fi"; }

    @Override
    public double calculateTotalFee(int months) {
        double utilityCharge = 1000.0 * months;
        return (getMonthlyBaseRent() * months) + utilityCharge;
    }
}

class DeluxeACRoom extends Room {
    private boolean hasGeyser;

    public DeluxeACRoom(String roomNumber, int capacity, double monthlyBaseRent, boolean hasGeyser) {
        super(roomNumber, capacity, monthlyBaseRent);
        this.hasGeyser = hasGeyser;
    }

    @Override
    public String getRoomType() { return "Deluxe AC"; }

    @Override
    public String getAmenities() { return "Split AC, Attached Washroom, Geyser, High-Speed LAN & Wi-Fi"; }

    @Override
    public double calculateTotalFee(int months) {
        double acElectricityCharge = 3500.0 * months;
        return (getMonthlyBaseRent() * months) + acElectricityCharge;
    }
}

abstract class Person {
    protected String id;
    protected String name;
    protected String phone;
    protected String email;

    public Person(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public abstract String getRole();
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}

class Student extends Person {
    private String regNo;
    private String department;
    private String allocatedRoom;
    private double totalFee;
    private double feePaid;

    public Student(String id, String name, String phone, String email, String regNo, String department) {
        super(id, name, phone, email);
        this.regNo = regNo;
        this.department = department;
        this.allocatedRoom = "NOT_ALLOCATED";
        this.totalFee = 0.0;
        this.feePaid = 0.0;
    }

    public Student(String id, String name, String phone, String email, String regNo, String department, 
                   String allocatedRoom, double totalFee, double feePaid) {
        super(id, name, phone, email);
        this.regNo = regNo;
        this.department = department;
        this.allocatedRoom = allocatedRoom;
        this.totalFee = totalFee;
        this.feePaid = feePaid;
    }

    @Override
    public String getRole() { return "Student"; }

    public double getFeeDue() { return Math.max(0.0, totalFee - feePaid); }

    public void payFee(double amount) throws InvalidDataException {
        if (amount <= 0) throw new InvalidDataException("Payment amount must be positive.");
        if (amount > getFeeDue()) throw new InvalidDataException("Amount exceeds outstanding due of Rs. " + getFeeDue());
        this.feePaid += amount;
    }

    public String getRegNo() { return regNo; }
    public String getDepartment() { return department; }
    public String getAllocatedRoom() { return allocatedRoom; }
    public void setAllocatedRoom(String room) { this.allocatedRoom = room; }
    public double getTotalFee() { return totalFee; }
    public void setTotalFee(double fee) { this.totalFee = fee; }
    public double getFeePaid() { return feePaid; }
    public void setFeePaid(double paid) { this.feePaid = paid; }

    @Override
    public String toString() {
        return String.format("[%s] %-18s | Reg: %-10s | Dept: %-6s | Room: %-5s | Due: Rs.%-6.0f",
                id, name, regNo, department, allocatedRoom, getFeeDue());
    }
}

class FeeTransaction {
    private String txnId;
    private String regNo;
    private double amount;
    private String date;
    private String paymentMode;

    public FeeTransaction(String txnId, String regNo, double amount, String date, String paymentMode) {
        this.txnId = txnId;
        this.regNo = regNo;
        this.amount = amount;
        this.date = date;
        this.paymentMode = paymentMode;
    }

    public String getTxnId() { return txnId; }
    public String getRegNo() { return regNo; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getPaymentMode() { return paymentMode; }

    @Override
    public String toString() {
        return String.format("[%s] Student: %-10s | Amount: Rs.%-7.0f | Date: %s | Mode: %s",
                txnId, regNo, amount, date, paymentMode);
    }
}

class HostelDatabase {
    private static final String DATA_FILE = "hostel_data.txt";
    private Map<String, Room> rooms = new LinkedHashMap<>();
    private Map<String, Student> students = new LinkedHashMap<>();
    private List<FeeTransaction> transactions = new ArrayList<>();

    public HostelDatabase() { loadData(); }

    public void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            initDefaultSeedData();
            saveData();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            rooms.clear();
            students.clear();
            transactions.clear();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|", -1);
                String tag = p[0].trim().toUpperCase();
                if ("ROOM".equals(tag) && p.length >= 6) {
                    String num = p[1].trim();
                    String type = p[2].trim();
                    int cap = Integer.parseInt(p[3].trim());
                    int occ = Integer.parseInt(p[4].trim());
                    double rent = Double.parseDouble(p[5].trim());
                    Room r = type.equalsIgnoreCase("Deluxe AC") ? new DeluxeACRoom(num, cap, rent, true) : new StandardRoom(num, cap, rent);
                    r.setOccupiedBeds(occ);
                    rooms.put(num, r);
                } else if ("STUDENT".equals(tag) && p.length >= 10) {
                    Student s = new Student(p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(),
                            p[5].trim(), p[6].trim(), p[7].trim(), 
                            Double.parseDouble(p[8].trim()), Double.parseDouble(p[9].trim()));
                    students.put(s.getRegNo(), s);
                } else if ("TXN".equals(tag) && p.length >= 6) {
                    transactions.add(new FeeTransaction(p[1].trim(), p[2].trim(), Double.parseDouble(p[3].trim()), p[4].trim(), p[5].trim()));
                }
            }
        } catch (Exception e) {
            initDefaultSeedData();
        }
    }

    public void saveData() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(DATA_FILE), StandardCharsets.UTF_8))) {
            pw.println("# SMART HOSTEL MANAGEMENT SYSTEM DATA");
            for (Room r : rooms.values()) {
                pw.printf("ROOM|%s|%s|%d|%d|%.0f\n", r.getRoomNumber(), r.getRoomType(), r.getCapacity(), r.getOccupiedBeds(), r.getMonthlyBaseRent());
            }
            for (Student s : students.values()) {
                pw.printf("STUDENT|%s|%s|%s|%s|%s|%s|%s|%.0f|%.0f\n",
                        s.getId(), s.getName(), s.getPhone(), s.getEmail(), s.getRegNo(),
                        s.getDepartment(), s.getAllocatedRoom(), s.getTotalFee(), s.getFeePaid());
            }
            for (FeeTransaction t : transactions) {
                pw.printf("TXN|%s|%s|%.0f|%s|%s\n", t.getTxnId(), t.getRegNo(), t.getAmount(), t.getDate(), t.getPaymentMode());
            }
        } catch (Exception ignored) {}
    }

    private void initDefaultSeedData() {
        rooms.clear();
        students.clear();
        transactions.clear();

        rooms.put("R101", new StandardRoom("R101", 3, 7000));
        rooms.put("R102", new StandardRoom("R102", 3, 7000));
        rooms.put("R103", new StandardRoom("R103", 2, 8500));
        rooms.put("R201", new DeluxeACRoom("R201", 2, 14000, true));
        rooms.put("R202", new DeluxeACRoom("R202", 2, 14000, true));
        rooms.put("R203", new DeluxeACRoom("R203", 4, 11000, true));

        Student s1 = new Student("S101", "Aarav Sharma", "9876543210", "aarav@vit.ac.in", "24BCE1042", "CSE", "R101", 48000, 48000);
        Student s2 = new Student("S102", "Priya Patel", "9876543211", "priya@vit.ac.in", "24BEE1089", "ECE", "R101", 48000, 35000);
        Student s3 = new Student("S103", "Rohan Verma", "9876543212", "rohan@vit.ac.in", "24BME1015", "MECH", "R201", 105000, 105000);
        Student s4 = new Student("S104", "Sneha Reddy", "9876543213", "sneha@vit.ac.in", "24BCS1077", "CSE", "R201", 105000, 70000);
        Student s5 = new Student("S105", "Kunal Gupta", "9876543214", "kunal@vit.ac.in", "24BIT1023", "IT", "NOT_ALLOCATED", 0, 0);

        students.put(s1.getRegNo(), s1);
        students.put(s2.getRegNo(), s2);
        students.put(s3.getRegNo(), s3);
        students.put(s4.getRegNo(), s4);
        students.put(s5.getRegNo(), s5);

        rooms.get("R101").setOccupiedBeds(2);
        rooms.get("R201").setOccupiedBeds(2);

        transactions.add(new FeeTransaction("TXN8001", "24BCE1042", 48000, "2026-07-10", "NetBanking"));
        transactions.add(new FeeTransaction("TXN8002", "24BEE1089", 35000, "2026-07-11", "UPI"));
        transactions.add(new FeeTransaction("TXN8003", "24BME1015", 105000, "2026-07-12", "DebitCard"));
        transactions.add(new FeeTransaction("TXN8004", "24BCS1077", 70000, "2026-07-15", "UPI"));
    }

    public Map<String, Room> getRooms() { return rooms; }
    public Map<String, Student> getStudents() { return students; }
    public List<FeeTransaction> getTransactions() { return transactions; }

    public Room getRoom(String roomNumber) { return rooms.get(roomNumber.toUpperCase().trim()); }
    public Student getStudent(String regNo) { return students.get(regNo.toUpperCase().trim()); }

    public void addStudent(Student s) { students.put(s.getRegNo(), s); saveData(); }

    public void allocateRoom(String regNo, String roomNo, int durationMonths) throws HostelException {
        Student s = getStudent(regNo);
        if (s == null) throw new InvalidDataException("Student with RegNo '" + regNo + "' not found.");
        Room newRoom = getRoom(roomNo);
        if (newRoom == null) throw new InvalidDataException("Room '" + roomNo + "' does not exist.");
        if (!newRoom.hasAvailableBed()) throw new RoomFullException("Room " + roomNo + " has no available beds!");

        if (!"NOT_ALLOCATED".equals(s.getAllocatedRoom())) {
            Room oldRoom = getRoom(s.getAllocatedRoom());
            if (oldRoom != null) oldRoom.vacateBed();
        }

        newRoom.allocateBed();
        s.setAllocatedRoom(newRoom.getRoomNumber());
        double calculatedFee = newRoom.calculateTotalFee(durationMonths);
        double totalSemesterFee = calculatedFee + (4500.0 * durationMonths);
        s.setTotalFee(totalSemesterFee);
        saveData();
    }

    public void vacateStudent(String regNo) throws HostelException {
        Student s = getStudent(regNo);
        if (s == null) throw new InvalidDataException("Student not found.");
        if ("NOT_ALLOCATED".equals(s.getAllocatedRoom())) throw new InvalidDataException("Student has no room allocated.");
        if (s.getFeeDue() > 0) throw new InvalidDataException("Cannot vacate! Outstanding dues of Rs. " + s.getFeeDue() + " must be cleared first.");

        Room r = getRoom(s.getAllocatedRoom());
        if (r != null) r.vacateBed();
        s.setAllocatedRoom("NOT_ALLOCATED");
        saveData();
    }

    public void recordPayment(String regNo, double amount, String mode) throws HostelException {
        Student s = getStudent(regNo);
        if (s == null) throw new InvalidDataException("Student with RegNo '" + regNo + "' not found.");
        s.payFee(amount);
        String txnId = "TXN" + (8000 + transactions.size() + 1);
        String today = LocalDate.now().toString();
        transactions.add(new FeeTransaction(txnId, s.getRegNo(), amount, today, mode));
        saveData();
    }
}

public class Main {
    private static HostelDatabase db = new HostelDatabase();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length > 0) {
            String flag = args[0].toLowerCase();
            if (flag.equals("--test")) { runAutomatedTests(); return; }
            else if (flag.equals("--demo")) { runAutomatedDemo(); return; }
            else if (flag.equals("--help") || flag.equals("-h")) {
                System.out.println("Smart Hostel Management System (SHMS)");
                System.out.println("  (no args)  : Launch interactive console UI");
                System.out.println("  --test     : Run automated grading test suite");
                System.out.println("  --demo     : Run non-interactive demonstration");
                return;
            }
        }

        boolean exitApp = false;
        while (!exitApp) {
            printLoginMenu();
            System.out.print("Enter Choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": handleAdminLogin(); break;
                case "2": handleStudentPortal(); break;
                case "3": runAutomatedDemo(); break;
                case "4": runAutomatedTests(); break;
                case "0":
                    db.saveData();
                    System.out.println("\nThank you for using Smart Hostel Management System. Goodbye!");
                    exitApp = true;
                    break;
                default:
                    System.out.println("Invalid option! Please select from 0 to 4.\n");
            }
        }
    }

    private static void printLoginMenu() {
        System.out.println("\n========================================================");
        System.out.println("       SMART HOSTEL MANAGEMENT SYSTEM (SHMS)");
        System.out.println("            Programming in Java Project");
        System.out.println("========================================================");
        System.out.println("  1. Admin Login (Warden / Management)");
        System.out.println("  2. Student Self-Service Portal");
        System.out.println("  3. Run Non-Interactive Live Demo");
        System.out.println("  4. Run Automated Diagnostic Test Suite");
        System.out.println("  0. Exit");
        System.out.println("--------------------------------------------------------");
    }

    private static void handleAdminLogin() {
        System.out.println("\n--- ADMIN AUTHENTICATION ---");
        System.out.print("Enter Admin Username [default: admin]: ");
        String u = scanner.nextLine().trim();
        System.out.print("Enter Password [default: admin123]: ");
        String p = scanner.nextLine().trim();
        if (u.equals("admin") && p.equals("admin123")) {
            System.out.println("\nLogin Successful! Welcome to Warden Admin Dashboard.");
            runAdminDashboard();
        } else {
            System.out.println("Invalid Admin Credentials. Returning to main menu.");
        }
    }

    private static void runAdminDashboard() {
        boolean inAdmin = true;
        while (inAdmin) {
            System.out.println("\n=================== ADMIN DASHBOARD ====================");
            printDashboardStats();
            System.out.println("----------------- OPERATIONS MENU ----------------------");
            System.out.println("  1. Manage Students (View, Register, Allocate Room)");
            System.out.println("  2. Manage Fee Payments & Dues");
            System.out.println("  3. Manage Hostel Rooms & Capacity");
            System.out.println("  4. View All Payment Transactions");
            System.out.println("  0. Logout to Main Menu");
            System.out.println("--------------------------------------------------------");
            System.out.print("Select Operation: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1": handleStudentMenu(); break;
                case "2": handleFeeMenu(); break;
                case "3": handleRoomMenu(); break;
                case "4": handleViewTransactions(); break;
                case "0": inAdmin = false; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void printDashboardStats() {
        int totalRooms = db.getRooms().size();
        int totalBeds = 0;
        int occupiedBeds = 0;
        for (Room r : db.getRooms().values()) {
            totalBeds += r.getCapacity();
            occupiedBeds += r.getOccupiedBeds();
        }
        int availableBeds = totalBeds - occupiedBeds;
        double totalCollected = 0;
        double totalDues = 0;
        for (Student s : db.getStudents().values()) {
            totalCollected += s.getFeePaid();
            totalDues += s.getFeeDue();
        }
        System.out.printf("  * Total Rooms: %d  |  Total Capacity: %d Beds\n", totalRooms, totalBeds);
        System.out.printf("  * Occupied Beds: %d  |  Available Beds: %d (Occupancy: %.1f%%)\n", 
                occupiedBeds, availableBeds, (occupiedBeds * 100.0 / totalBeds));
        System.out.printf("  * Registered Students: %d\n", db.getStudents().size());
        System.out.printf("  * Total Fee Collected: Rs. %.0f  |  Outstanding Dues: Rs. %.0f\n", totalCollected, totalDues);
    }

    private static void handleStudentMenu() {
        System.out.println("\n---------------- STUDENT MANAGEMENT ------------------");
        System.out.println("  1. View All Registered Students");
        System.out.println("  2. Register New Student");
        System.out.println("  3. Allocate / Change Room for Student");
        System.out.println("  4. Vacate Student from Room");
        System.out.println("  5. Search Student by Registration Number");
        System.out.println("  0. Back");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();
        switch (ch) {
            case "1":
                System.out.println("\n--- REGISTERED STUDENTS LIST ---");
                for (Student s : db.getStudents().values()) System.out.println(s);
                break;
            case "2":
                try {
                    System.out.print("Enter Student Full Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter Registration Number (e.g. 24BCE1999): ");
                    String reg = scanner.nextLine().trim();
                    System.out.print("Enter Department (CSE/ECE/MECH/IT): ");
                    String dept = scanner.nextLine().trim();
                    System.out.print("Enter Phone Number: ");
                    String ph = scanner.nextLine().trim();
                    System.out.print("Enter Email Address: ");
                    String em = scanner.nextLine().trim();
                    String newId = "S" + (100 + db.getStudents().size() + 1);
                    db.addStudent(new Student(newId, name, ph, em, reg, dept));
                    System.out.println("Student registered successfully with ID: " + newId);
                } catch (Exception e) { System.out.println("Registration failed: " + e.getMessage()); }
                break;
            case "3":
                try {
                    System.out.print("Enter Student Registration Number: ");
                    String reg = scanner.nextLine().trim();
                    System.out.print("Enter Room Number to Allocate (e.g. R101, R201): ");
                    String rno = scanner.nextLine().trim();
                    System.out.print("Enter Duration in Months (e.g. 6): ");
                    int months = Integer.parseInt(scanner.nextLine().trim());
                    db.allocateRoom(reg, rno, months);
                    Student s = db.getStudent(reg);
                    System.out.printf("SUCCESS! Room %s allocated to %s. Total Semester Fee: Rs. %.0f\n", rno, s.getName(), s.getTotalFee());
                } catch (Exception e) { System.out.println("Allocation Error: " + e.getMessage()); }
                break;
            case "4":
                try {
                    System.out.print("Enter Student Registration Number to Vacate: ");
                    String reg = scanner.nextLine().trim();
                    db.vacateStudent(reg);
                    System.out.println("SUCCESS: Student vacated and bed freed up.");
                } catch (Exception e) { System.out.println("Vacate Error: " + e.getMessage()); }
                break;
            case "5":
                System.out.print("Enter Registration Number to Search: ");
                String search = scanner.nextLine().trim();
                Student found = db.getStudent(search);
                if (found != null) {
                    System.out.println("\n--- STUDENT PROFILE FOUND ---");
                    System.out.println("Name         : " + found.getName());
                    System.out.println("Reg No       : " + found.getRegNo());
                    System.out.println("Department   : " + found.getDepartment());
                    System.out.println("Allocated Room: " + found.getAllocatedRoom());
                    System.out.println("Total Fee    : Rs. " + found.getTotalFee());
                    System.out.println("Fee Paid     : Rs. " + found.getFeePaid());
                    System.out.println("Pending Due  : Rs. " + found.getFeeDue());
                    System.out.println("Contact      : " + found.getPhone() + " | " + found.getEmail());
                } else {
                    System.out.println("No student found with RegNo: " + search);
                }
                break;
            default: break;
        }
    }

    private static void handleFeeMenu() {
        System.out.println("\n---------------- FEE MANAGEMENT ----------------------");
        System.out.println("  1. View Students with Outstanding Fee Dues");
        System.out.println("  2. Record Student Fee Payment");
        System.out.println("  3. Generate Student Fee Receipt");
        System.out.println("  0. Back");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();
        switch (ch) {
            case "1":
                System.out.println("\n--- OUTSTANDING FEE DEFAULTERS ---");
                boolean anyDue = false;
                for (Student s : db.getStudents().values()) {
                    if (s.getFeeDue() > 0) {
                        System.out.printf("  * %-10s | %-18s | Room: %-5s | Due: Rs. %.0f\n",
                                s.getRegNo(), s.getName(), s.getAllocatedRoom(), s.getFeeDue());
                        anyDue = true;
                    }
                }
                if (!anyDue) System.out.println("All students have cleared their dues!");
                break;
            case "2":
                try {
                    System.out.print("Enter Student RegNo: ");
                    String reg = scanner.nextLine().trim();
                    Student s = db.getStudent(reg);
                    if (s == null) { System.out.println("Student not found."); break; }
                    System.out.println("Current Pending Dues: Rs. " + s.getFeeDue());
                    System.out.print("Enter Payment Amount: Rs. ");
                    double amt = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Enter Payment Mode (UPI / NetBanking / Cash): ");
                    String mode = scanner.nextLine().trim();
                    db.recordPayment(reg, amt, mode);
                    System.out.printf("Payment of Rs. %.0f recorded successfully! Remaining Due: Rs. %.0f\n", amt, s.getFeeDue());
                } catch (Exception e) { System.out.println("Payment Error: " + e.getMessage()); }
                break;
            case "3":
                System.out.print("Enter Student RegNo for Receipt: ");
                String rno = scanner.nextLine().trim();
                Student st = db.getStudent(rno);
                if (st != null) {
                    System.out.println("\n========================================================");
                    System.out.println("             HOSTEL FEE PAYMENT RECEIPT");
                    System.out.println("========================================================");
                    System.out.println("Student Name     : " + st.getName());
                    System.out.println("Registration No  : " + st.getRegNo());
                    System.out.println("Department       : " + st.getDepartment());
                    System.out.println("Room Allocated   : " + st.getAllocatedRoom());
                    System.out.println("Total Fee Billed : Rs. " + st.getTotalFee());
                    System.out.println("Total Fee Paid   : Rs. " + st.getFeePaid());
                    System.out.println("Balance Due      : Rs. " + st.getFeeDue());
                    System.out.println("Payment Status   : " + (st.getFeeDue() == 0 ? "PAID FULLY" : "PARTIAL / PENDING"));
                    System.out.println("Generated Date   : " + LocalDate.now());
                    System.out.println("========================================================");
                } else { System.out.println("Student not found."); }
                break;
            default: break;
        }
    }

    private static void handleRoomMenu() {
        System.out.println("\n--- HOSTEL ROOMS INVENTORY ---");
        for (Room r : db.getRooms().values()) {
            System.out.println(r);
            System.out.println("    Amenities: " + r.getAmenities());
        }
    }

    private static void handleViewTransactions() {
        System.out.println("\n--- ALL FEE PAYMENT TRANSACTIONS ---");
        for (FeeTransaction t : db.getTransactions()) System.out.println(t);
    }

    private static void handleStudentPortal() {
        System.out.print("\nEnter Your Registration Number: ");
        String reg = scanner.nextLine().trim();
        Student s = db.getStudent(reg);
        if (s == null) {
            System.out.println("Student registration number not found. Please contact Warden office.");
            return;
        }
        System.out.println("\n================ STUDENT SELF-SERVICE PORTAL ================");
        System.out.println("Welcome, " + s.getName() + " (" + s.getDepartment() + ")");
        System.out.println("Allocated Room : " + s.getAllocatedRoom());
        System.out.println("Total Hostel Fee: Rs. " + s.getTotalFee());
        System.out.println("Amount Paid     : Rs. " + s.getFeePaid());
        System.out.println("Pending Due     : Rs. " + s.getFeeDue());
        if (!"NOT_ALLOCATED".equals(s.getAllocatedRoom())) {
            Room r = db.getRoom(s.getAllocatedRoom());
            if (r != null) {
                System.out.println("Room Type      : " + r.getRoomType());
                System.out.println("Room Amenities : " + r.getAmenities());
            }
        }
        System.out.println("=============================================================");
    }

    public static void runAutomatedDemo() {
        System.out.println("========================================================");
        System.out.println("     NON-INTERACTIVE AUTOMATED DEMONSTRATION MODE       ");
        System.out.println("========================================================");
        HostelDatabase demoDb = new HostelDatabase();
        System.out.println("\n[1] Current Room Inventory:");
        for (Room r : demoDb.getRooms().values()) System.out.println("  " + r);

        System.out.println("\n[2] Current Students and Allocations:");
        for (Student s : demoDb.getStudents().values()) System.out.println("  " + s);

        System.out.println("\n[3] Testing Allocation for Unallocated Student S105 (Kunal Gupta):");
        try {
            demoDb.allocateRoom("24BIT1023", "R102", 6);
            System.out.println("  Allocated Kunal Gupta to R102 for 6 months.");
            Student k = demoDb.getStudent("24BIT1023");
            System.out.println("  Total Fee Billed: Rs. " + k.getTotalFee());
        } catch (Exception e) { System.out.println("  Notice: " + e.getMessage()); }

        System.out.println("\n[4] Recording Fee Payment for Priya Patel (24BEE1089):");
        try {
            demoDb.recordPayment("24BEE1089", 13000, "UPI");
            System.out.println("  Payment of Rs. 13,000 recorded. New due: Rs. " + demoDb.getStudent("24BEE1089").getFeeDue());
        } catch (Exception e) { System.out.println("  Payment notice: " + e.getMessage()); }

        System.out.println("\n[5] Summary Statistics:");
        int totalBeds = 0, occupiedBeds = 0;
        for (Room r : demoDb.getRooms().values()) {
            totalBeds += r.getCapacity();
            occupiedBeds += r.getOccupiedBeds();
        }
        System.out.printf("  Total Beds: %d | Occupied: %d | Available: %d\n", totalBeds, occupiedBeds, (totalBeds - occupiedBeds));
        System.out.println("Demonstration run completed successfully.");
    }

    public static void runAutomatedTests() {
        System.out.println("\n========================================================");
        System.out.println("    RUNNING AUTOMATED UNIT & CONFLICT TEST HARNESS     ");
        System.out.println("========================================================");
        int passed = 0, total = 6;
        HostelDatabase testDb = new HostelDatabase();

        System.out.print("[TEST 1] Verifying Room & Student Catalog Loading ...... ");
        if (testDb.getRooms().size() >= 4 && testDb.getStudents().size() >= 3) {
            System.out.println("PASS (Rooms: " + testDb.getRooms().size() + ", Students: " + testDb.getStudents().size() + ")");
            passed++;
        } else { System.out.println("FAIL"); }

        System.out.print("[TEST 2] Testing Polymorphic Fee Calculation ........... ");
        Room std = testDb.getRoom("R101");
        Room dlx = testDb.getRoom("R201");
        double stdFee = std.calculateTotalFee(6);
        double dlxFee = dlx.calculateTotalFee(6);
        if (stdFee == 48000.0 && dlxFee == 105000.0) {
            System.out.println("PASS (Standard: Rs." + stdFee + ", Deluxe AC: Rs." + dlxFee + ")");
            passed++;
        } else { System.out.println("FAIL"); }

        System.out.print("[TEST 3] Testing Room Allocation & Bed Counter ......... ");
        try {
            Room r103 = testDb.getRoom("R103");
            Student testStudent = testDb.getStudent("24BIT1023");
            testStudent.setAllocatedRoom("NOT_ALLOCATED");
            r103.setOccupiedBeds(0);
            testDb.allocateRoom("24BIT1023", "R103", 6);
            if (r103.getOccupiedBeds() == 1 && "R103".equals(testStudent.getAllocatedRoom())) {
                System.out.println("PASS (Bed assigned in R103)");
                passed++;
            } else { System.out.println("FAIL"); }
        } catch (Exception e) { System.out.println("FAIL (" + e.getMessage() + ")"); }

        System.out.print("[TEST 4] Testing Capacity Limit & RoomFullException ... ");
        try {
            Room r201 = testDb.getRoom("R201");
            r201.setOccupiedBeds(r201.getCapacity());
            r201.allocateBed();
            System.out.println("FAIL (Room allowed over-allocation)");
        } catch (RoomFullException e) {
            System.out.println("PASS (Prevented over-allocation: " + e.getMessage() + ")");
            passed++;
        }

        System.out.print("[TEST 5] Testing Fee Payment & Overpay Protection ..... ");
        try {
            Student s = testDb.getStudent("24BEE1089");
            s.setTotalFee(48000);
            s.setFeePaid(30000);
            double initialDue = s.getFeeDue(); // 18000
            testDb.recordPayment("24BEE1089", 5000, "UPI");
            if (s.getFeeDue() == initialDue - 5000) {
                System.out.println("PASS (Payment recorded, dues updated)");
                passed++;
            } else { System.out.println("FAIL"); }
        } catch (Exception e) { System.out.println("FAIL (" + e.getMessage() + ")"); }

        System.out.print("[TEST 6] Testing Vacate With Dues Check ................ ");
        try {
            Student defaulter = testDb.getStudent("24BCS1077");
            testDb.vacateStudent(defaulter.getRegNo());
            System.out.println("FAIL (Student vacated without clearing dues)");
        } catch (InvalidDataException e) {
            System.out.println("PASS (Blocked vacate with dues: " + e.getMessage() + ")");
            passed++;
        } catch (Exception e) { System.out.println("FAIL (" + e.getMessage() + ")"); }

        System.out.println("========================================================");
        System.out.printf("Test Summary: %d / %d Tests Passed (Score: %.1f%%)\n", passed, total, (passed * 100.0 / total));
        System.out.println("========================================================\n");
    }
}
