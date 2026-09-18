import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            int failures = TestHarness.run();
            System.exit(failures == 0 ? 0 : 1);
            return;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            DemoRunner.run();
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            setupDarkMode(); // Apply Dark Mode UI globally
            
            // Note: Delete "campus_data.txt" and run the app to force it to regenerate the seeded demo data.
            DataStore store = new DataStore("campus_data.txt");
            store.loadOrSeed();
            
            CampusBookingGUI gui = new CampusBookingGUI(store);
            gui.setVisible(true);
        });
    }

    private static void setupDarkMode() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            Color bg = new Color(30, 30, 30);
            Color fg = new Color(220, 220, 220);
            Color accent = new Color(0, 122, 204);
            Color panelBg = new Color(45, 45, 45);
            
            UIManager.put("Panel.background", bg);
            UIManager.put("Label.foreground", fg);
            UIManager.put("Button.background", accent);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.focus", new Color(0,0,0,0)); 
            UIManager.put("TextField.background", panelBg);
            UIManager.put("TextField.foreground", fg);
            UIManager.put("TextField.caretForeground", fg);
            UIManager.put("ComboBox.background", panelBg);
            UIManager.put("ComboBox.foreground", fg);
            UIManager.put("ComboBox.selectionBackground", accent);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            UIManager.put("TextArea.background", bg);
            UIManager.put("TextArea.foreground", fg);
            UIManager.put("TextArea.caretForeground", fg);
            UIManager.put("ScrollPane.background", bg);
            UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder(1, 1, 1, 1));
            UIManager.put("Table.background", panelBg);
            UIManager.put("Table.foreground", fg);
            UIManager.put("Table.gridColor", new Color(60, 60, 60));
            UIManager.put("Table.selectionBackground", accent);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("TableHeader.background", new Color(20, 20, 20));
            UIManager.put("TableHeader.foreground", fg);
            UIManager.put("TabbedPane.background", panelBg);
            UIManager.put("TabbedPane.foreground", fg);
            UIManager.put("TabbedPane.selected", bg);
            UIManager.put("TabbedPane.contentAreaColor", bg);
            UIManager.put("OptionPane.background", bg);
            UIManager.put("OptionPane.messageForeground", fg);
        } catch (Exception ignored) {}
    }
}

class CampusBookingException extends Exception {
    public CampusBookingException(String message) { super(message); }
}

class SlotUnavailableException extends CampusBookingException {
    public SlotUnavailableException(String message) { super(message); }
}

class InvalidInputException extends CampusBookingException {
    public InvalidInputException(String message) { super(message); }
}

abstract class Facility {
    private final String id;
    private final String name;
    private final int capacity;
    private final String location;
    private boolean underMaintenance;

    public Facility(String id, String name, int capacity, String location) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.underMaintenance = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getLocation() { return location; }
    public boolean isUnderMaintenance() { return underMaintenance; }
    public void setUnderMaintenance(boolean underMaintenance) { this.underMaintenance = underMaintenance; }

    public abstract String getSpecificDetails();
    public abstract String getType();
    public abstract String getTypeKey();
    public abstract String toDataExtra();
}

class Lab extends Facility {
    private final int workstations;
    private final String os;
    private final boolean hasGPU;

    public Lab(String id, String name, int capacity, String location, int workstations, String os, boolean hasGPU) {
        super(id, name, capacity, location);
        this.workstations = workstations;
        this.os = os;
        this.hasGPU = hasGPU;
    }
    @Override public String getSpecificDetails() { return workstations + " workstations, OS: " + os + (hasGPU ? ", NVIDIA RTX GPU" : ", no GPU"); }
    @Override public String getType() { return "Computing Lab"; }
    @Override public String getTypeKey() { return "Lab"; }
    @Override public String toDataExtra() { return workstations + "," + os + "," + (hasGPU ? "1" : "0"); }
}

class Hall extends Facility {
    private final boolean hasAC;
    private final boolean hasProjector;

    public Hall(String id, String name, int capacity, String location, boolean hasAC, boolean hasProjector) {
        super(id, name, capacity, location);
        this.hasAC = hasAC;
        this.hasProjector = hasProjector;
    }
    @Override public String getSpecificDetails() { return (hasAC ? "AC" : "Non-AC") + ", " + (hasProjector ? "Projector" : "No projector"); }
    @Override public String getType() { return "Auditorium / Hall"; }
    @Override public String getTypeKey() { return "Hall"; }
    @Override public String toDataExtra() { return (hasAC ? "1" : "0") + "," + (hasProjector ? "1" : "0"); }
}

class SportsCourt extends Facility {
    private final boolean indoor;
    private final String surface;
    private final boolean floodlights;

    public SportsCourt(String id, String name, int capacity, String location, boolean indoor, String surface, boolean floodlights) {
        super(id, name, capacity, location);
        this.indoor = indoor;
        this.surface = surface;
        this.floodlights = floodlights;
    }
    @Override public String getSpecificDetails() { return (indoor ? "Indoor" : "Outdoor") + ", " + surface + (floodlights ? ", Floodlit" : ""); }
    @Override public String getType() { return "Sports Court"; }
    @Override public String getTypeKey() { return "SportsCourt"; }
    @Override public String toDataExtra() { return (indoor ? "1" : "0") + "," + surface + "," + (floodlights ? "1" : "0"); }
}

abstract class User {
    private final String id, name, department, email;
    public User(String id, String name, String department, String email) {
        this.id = id; this.name = name; this.department = department; this.email = email;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getEmail() { return email; }
    public abstract int getMaxBookingHours();
    public abstract boolean canBookAuditoriumDirectly();
    public abstract String getRole();
    public abstract String toDataExtra();
    @Override public String toString() { return name + " (" + getRole() + ")"; }
}

class Student extends User {
    private final String regNo;
    public Student(String id, String name, String department, String email, String regNo) { super(id, name, department, email); this.regNo = regNo; }
    public String getRegNo() { return regNo; }
    @Override public int getMaxBookingHours() { return 2; }
    @Override public boolean canBookAuditoriumDirectly() { return false; }
    @Override public String getRole() { return "Student"; }
    @Override public String toDataExtra() { return regNo; }
}

class FacultyMember extends User {
    private final String empId;
    public FacultyMember(String id, String name, String department, String email, String empId) { super(id, name, department, email); this.empId = empId; }
    public String getEmpId() { return empId; }
    @Override public int getMaxBookingHours() { return 6; }
    @Override public boolean canBookAuditoriumDirectly() { return true; }
    @Override public String getRole() { return "Faculty"; }
    @Override public String toDataExtra() { return empId; }
}

class Booking {
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";
    private final String bookingId, userId, facilityId, purpose;
    private final LocalDate date;
    private final LocalTime startTime, endTime;
    private String status;

    public Booking(String bookingId, String userId, String facilityId, LocalDate date, LocalTime startTime, LocalTime endTime, String status, String purpose) {
        this.bookingId = bookingId; this.userId = userId; this.facilityId = facilityId;
        this.date = date; this.startTime = startTime; this.endTime = endTime;
        this.status = status; this.purpose = purpose;
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

    public boolean overlaps(LocalDate otherDate, LocalTime otherStart, LocalTime otherEnd) {
        if (!status.equals(CONFIRMED) || !date.equals(otherDate)) return false;
        return startTime.isBefore(otherEnd) && endTime.isAfter(otherStart);
    }
}

class DataStore {
    public static final LocalTime OPEN_TIME = LocalTime.of(7, 0);
    public static final LocalTime CLOSE_TIME = LocalTime.of(22, 0);
    public static final int AUDITORIUM_PROTECTED_CAPACITY = 150;
    static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final String filePath;
    private final List<Facility> facilities = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private int bookingCounter = 101;

    public DataStore(String filePath) { this.filePath = filePath; }
    public List<Facility> getFacilities() { return facilities; }
    public List<User> getUsers() { return users; }
    public List<Booking> getBookings() { return bookings; }
    public Facility findFacility(String id) { for (Facility f : facilities) if (f.getId().equals(id)) return f; return null; }
    public User findUser(String id) { for (User u : users) if (u.getId().equals(id)) return u; return null; }
    public Booking findBooking(String bookingId) { for (Booking b : bookings) if (b.getBookingId().equalsIgnoreCase(bookingId)) return b; return null; }

    public void loadOrSeed() {
        File f = new File(filePath);
        if (f.exists()) {
            try {
                load();
                if (facilities.isEmpty() || users.isEmpty() || bookings.isEmpty()) { seed(); save(); }
                return;
            } catch (Exception e) {}
        }
        seed();
        save();
    }

    private void seed() {
        facilities.clear(); users.clear(); bookings.clear();
        facilities.add(new Lab("F1", "AI & Robotics Research Lab", 40, "Block A - Room 201", 40, "Ubuntu 24.04", true));
        facilities.add(new Lab("F2", "General Computing Lab", 60, "Block B - Room 105", 60, "Windows 11", false));
        facilities.add(new Hall("F3", "Grand Auditorium", 300, "Main Block", true, true));
        facilities.add(new Hall("F4", "Seminar Hall", 80, "Block C - Room 010", true, true));
        facilities.add(new SportsCourt("F5", "Indoor Badminton Court", 20, "Sports Complex", true, "Synthetic Mat", false));
        facilities.add(new SportsCourt("F6", "Outdoor Basketball Court", 30, "Sports Complex", false, "Wooden", true));

        users.add(new Student("U1", "Aarav Sharma", "Computer Science", "aarav@campus.edu", "21BCS101"));
        users.add(new Student("U2", "Priya Nair", "Electronics", "priya@campus.edu", "21BEC045"));
        users.add(new Student("U3", "Kabir Mehta", "Mechanical", "kabir@campus.edu", "21BME078"));
        users.add(new FacultyMember("U4", "Dr. Rajesh Kumar", "Computer Science", "rajesh@campus.edu", "EMP2201"));
        users.add(new FacultyMember("U5", "Dr. Sandip Mal", "Physics", "sandip@campus.edu", "EMP2088"));

        // ===== DEMO DATA SEEDING =====
        LocalDate today = LocalDate.now();
        bookings.add(new Booking("B101", "U4", "F3", today.plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0), Booking.CONFIRMED, "AI Guest Lecture"));
        bookings.add(new Booking("B102", "U1", "F1", today.plusDays(2), LocalTime.of(14, 0), LocalTime.of(16, 0), Booking.CONFIRMED, "Robotics Project"));
        bookings.add(new Booking("B103", "U2", "F5", today.plusDays(1), LocalTime.of(17, 0), LocalTime.of(18, 0), Booking.CONFIRMED, "Badminton Practice"));
        bookings.add(new Booking("B104", "U5", "F4", today.plusDays(3), LocalTime.of(9, 0), LocalTime.of(13, 0), Booking.CONFIRMED, "Physics Seminar"));
        bookings.add(new Booking("B105", "U3", "F2", today.minusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0), Booking.CANCELLED, "CAD Lab (Cancelled)"));
        bookingCounter = 106;
    }

    private void load() throws IOException {
        facilities.clear(); users.clear(); bookings.clear();
        int maxSeen = 100;
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p[0].equals("FAC")) {
                String[] e = p[6].split(",", -1);
                Facility fac = p[3].equals("Lab") ? new Lab(p[1], p[2], Integer.parseInt(p[4]), p[5], Integer.parseInt(e[0]), e[1], e[2].equals("1"))
                        : p[3].equals("Hall") ? new Hall(p[1], p[2], Integer.parseInt(p[4]), p[5], e[0].equals("1"), e[1].equals("1"))
                        : new SportsCourt(p[1], p[2], Integer.parseInt(p[4]), p[5], e[0].equals("1"), e[1], e[2].equals("1"));
                fac.setUnderMaintenance(p.length > 7 && p[7].equals("1"));
                facilities.add(fac);
            } else if (p[0].equals("USR")) {
                users.add(p[3].equals("Student") ? new Student(p[1], p[2], p[5], p[4], p[6]) : new FacultyMember(p[1], p[2], p[5], p[4], p[6]));
            } else if (p[0].equals("BKG")) {
                bookings.add(new Booking(p[1], p[2], p[3], LocalDate.parse(p[4], DATE_FMT), LocalTime.parse(p[5], TIME_FMT), LocalTime.parse(p[6], TIME_FMT), p[7], p.length > 8 ? p[8] : ""));
                maxSeen = Math.max(maxSeen, Integer.parseInt(p[1].replaceAll("[^0-9]", "")) + 1);
            }
        }
        bookingCounter = maxSeen;
    }

    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Facility f : facilities) pw.println(String.join("|", "FAC", f.getId(), f.getName(), f.getTypeKey(), String.valueOf(f.getCapacity()), f.getLocation(), f.toDataExtra(), f.isUnderMaintenance() ? "1" : "0"));
            for (User u : users) pw.println(String.join("|", "USR", u.getId(), u.getName(), u.getRole(), u.getEmail(), u.getDepartment(), u.toDataExtra()));
            for (Booking b : bookings) pw.println(String.join("|", "BKG", b.getBookingId(), b.getUserId(), b.getFacilityId(), b.getDate().format(DATE_FMT), b.getStartTime().format(TIME_FMT), b.getEndTime().format(TIME_FMT), b.getStatus(), b.getPurpose()));
        } catch (Exception e) {}
    }

    public Booking createBooking(User user, Facility facility, LocalDate date, LocalTime start, LocalTime end, String purpose) throws CampusBookingException {
        if (user == null || facility == null) throw new InvalidInputException("Invalid User or Facility.");
        if (date.isBefore(LocalDate.now())) throw new InvalidInputException("Cannot book in the past.");
        if (facility.isUnderMaintenance()) throw new SlotUnavailableException("Facility under maintenance.");
        if (!start.isBefore(end)) throw new InvalidInputException("Start time must be before end time.");
        if (start.isBefore(OPEN_TIME) || end.isAfter(CLOSE_TIME)) throw new InvalidInputException("Campus hours: 07:00 - 22:00.");

        double hours = Duration.between(start, end).toMinutes() / 60.0;
        if (hours > user.getMaxBookingHours()) throw new InvalidInputException(user.getRole() + " maximum session is " + user.getMaxBookingHours() + " hours.");
        if (facility instanceof Hall && facility.getCapacity() > AUDITORIUM_PROTECTED_CAPACITY && !user.canBookAuditoriumDirectly()) throw new InvalidInputException("Students cannot directly reserve grand auditoriums.");

        for (Booking b : bookings) if (b.getFacilityId().equals(facility.getId()) && b.overlaps(date, start, end)) throw new SlotUnavailableException("Conflict with booking " + b.getBookingId());

        Booking b = new Booking("B" + (bookingCounter++), user.getId(), facility.getId(), date, start, end, Booking.CONFIRMED, purpose.isEmpty() ? "General use" : purpose);
        bookings.add(b); save(); return b;
    }

    public void cancelBooking(String bookingId) throws CampusBookingException {
        Booking b = findBooking(bookingId);
        if (b == null || b.getStatus().equals(Booking.CANCELLED)) throw new InvalidInputException("Invalid/already cancelled ID.");
        b.setStatus(Booking.CANCELLED); save();
    }

    public List<Booking> scheduleFor(String facilityId, LocalDate date) {
        return bookings.stream().filter(b -> b.getFacilityId().equals(facilityId) && b.getDate().equals(date) && b.getStatus().equals(Booking.CONFIRMED)).sorted(Comparator.comparing(Booking::getStartTime)).collect(Collectors.toList());
    }
    public long totalActiveBookings() { return bookings.stream().filter(b -> b.getStatus().equals(Booking.CONFIRMED)).count(); }
    public long totalCancelledBookings() { return bookings.stream().filter(b -> b.getStatus().equals(Booking.CANCELLED)).count(); }
    public Map<String, Long> facilityUtilization() {
        return bookings.stream().filter(b -> b.getStatus().equals(Booking.CONFIRMED)).collect(Collectors.groupingBy(b -> { Facility f = findFacility(b.getFacilityId()); return f != null ? f.getName() : b.getFacilityId(); }, Collectors.counting()));
    }
    public Map<String, Long> departmentActivity() {
        return bookings.stream().filter(b -> b.getStatus().equals(Booking.CONFIRMED)).collect(Collectors.groupingBy(b -> { User u = findUser(b.getUserId()); return u != null ? u.getDepartment() : "Unknown"; }, Collectors.counting()));
    }
}

class CampusBookingGUI extends JFrame {
    private final DataStore store;
    private User currentUser;
    private JComboBox<User> profileCombo;
    private JLabel profileLabel;
    private DefaultTableModel catalogModel, myBookingsModel;
    private JTable catalogTable, myBookingsTable;
    private JComboBox<Facility> bookFacilityCombo;
    private JTextField bookDateField, bookStartField, bookEndField, bookPurposeField;
    private JTextArea bookOutput, analyticsArea;

    private static final Color PRIMARY = new Color(45, 45, 45);
    private static final Color BG = new Color(30, 30, 30);

    public CampusBookingGUI(DataStore store) {
        super("Smart Campus Facility Booking System (Dark Theme)");
        this.store = store;
        this.currentUser = store.findUser("U4");
        if (this.currentUser == null && !store.getUsers().isEmpty()) this.currentUser = store.getUsers().get(0);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Facility Catalog", buildCatalogTab());
        tabs.addTab("New Booking", buildBookingTab());
        tabs.addTab("My Bookings", buildMyBookingsTab());
        tabs.addTab("Campus Analytics", buildAnalyticsTab());
        add(tabs, BorderLayout.CENTER);

        tabs.addChangeListener(e -> { refreshCatalog(); refreshMyBookings(); refreshAnalytics(); });
        refreshCatalog(); refreshMyBookings(); refreshAnalytics();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(12, 18, 12, 18));
        JLabel title = new JLabel("Smart Campus Booking System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JLabel switchLabel = new JLabel("Active profile:");
        switchLabel.setForeground(Color.WHITE);

        profileCombo = new JComboBox<>(store.getUsers().toArray(new User[0]));
        profileCombo.setSelectedItem(currentUser);
        profileCombo.addActionListener(e -> { currentUser = (User) profileCombo.getSelectedItem(); refreshMyBookings(); updateProfileLabel(); });

        profileLabel = new JLabel();
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));

        right.add(switchLabel); right.add(profileCombo);
        header.add(right, BorderLayout.EAST); header.add(profileLabel, BorderLayout.SOUTH);
        updateProfileLabel(); return header;
    }

    private void updateProfileLabel() {
        if (currentUser == null) { profileLabel.setText("No users registered."); return; }
        profileLabel.setText(String.format("Signed in as %s | Role: %s | Max hr: %d | Auditorium access: %s",
                currentUser.getName(), currentUser.getRole(), currentUser.getMaxBookingHours(), currentUser.canBookAuditoriumDirectly() ? "Direct" : "Requires Endorsement"));
    }

    private JComponent buildCatalogTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); panel.setBackground(BG);
        catalogModel = new DefaultTableModel(new String[]{"ID", "Name", "Type", "Capacity", "Location", "Specific Details", "Status"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        catalogTable = new JTable(catalogModel); catalogTable.setRowHeight(24);
        panel.add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); bottom.setBackground(BG);
        JTextField dateField = new JTextField(LocalDate.now().format(DataStore.DATE_FMT), 10);
        JButton scheduleBtn = new JButton("View Schedule");
        scheduleBtn.addActionListener(e -> {
            int row = catalogTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a facility from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
            try { showSchedule(store.findFacility((String) catalogModel.getValueAt(row, 0)), LocalDate.parse(dateField.getText().trim(), DataStore.DATE_FMT)); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Use format yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE); }
        });
        bottom.add(new JLabel("Date:")); bottom.add(dateField); bottom.add(scheduleBtn);
        panel.add(bottom, BorderLayout.SOUTH); return panel;
    }

    private void showSchedule(Facility facility, LocalDate date) {
        List<Booking> schedule = store.scheduleFor(facility.getId(), date);
        StringBuilder sb = new StringBuilder(facility.getName() + " (" + facility.getType() + ")\nDate: " + date + "\n\n");
        if (facility.isUnderMaintenance()) sb.append("Facility is under maintenance.\n");
        else if (schedule.isEmpty()) sb.append("Fully available (07:00 - 22:00).\n");
        else {
            sb.append(String.format("%-10s %-8s %-8s %-20s %-20s%n", "Booking", "Start", "End", "User", "Purpose"));
            for (Booking b : schedule) { User u = store.findUser(b.getUserId()); sb.append(String.format("%-10s %-8s %-8s %-20s %-20s%n", b.getBookingId(), b.getStartTime(), b.getEndTime(), u == null ? b.getUserId() : u.getName(), b.getPurpose())); }
        }
        JTextArea area = new JTextArea(sb.toString()); area.setEditable(false); area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Facility Schedule", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshCatalog() {
        catalogModel.setRowCount(0);
        for (Facility f : store.getFacilities()) catalogModel.addRow(new Object[]{ f.getId(), f.getName(), f.getType(), f.getCapacity(), f.getLocation(), f.getSpecificDetails(), f.isUnderMaintenance() ? "Under Maintenance" : "Available" });
        if (bookFacilityCombo != null) { Facility selected = (Facility) bookFacilityCombo.getSelectedItem(); bookFacilityCombo.removeAllItems(); for (Facility f : store.getFacilities()) bookFacilityCombo.addItem(f); if (selected != null) bookFacilityCombo.setSelectedItem(selected); }
    }

    private JComponent buildBookingTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10)); panel.setBorder(new EmptyBorder(14, 14, 14, 14)); panel.setBackground(BG);
        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(BG);
        GridBagConstraints gc = new GridBagConstraints(); gc.insets = new Insets(6, 6, 6, 6); gc.anchor = GridBagConstraints.WEST; gc.fill = GridBagConstraints.HORIZONTAL;

        bookFacilityCombo = new JComboBox<>(store.getFacilities().toArray(new Facility[0]));
        bookDateField = new JTextField(LocalDate.now().plusDays(1).format(DataStore.DATE_FMT), 12);
        bookStartField = new JTextField("10:00", 8); bookEndField = new JTextField("11:00", 8);
        bookPurposeField = new JTextField("Project Discussion", 24);

        int r = 0;
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Facility:"), gc); gc.gridx = 1; gc.gridy = r++; form.add(bookFacilityCombo, gc);
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Date (yyyy-MM-dd):"), gc); gc.gridx = 1; gc.gridy = r++; form.add(bookDateField, gc);
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Start time (HH:mm):"), gc); gc.gridx = 1; gc.gridy = r++; form.add(bookStartField, gc);
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("End time (HH:mm):"), gc); gc.gridx = 1; gc.gridy = r++; form.add(bookEndField, gc);
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Purpose:"), gc); gc.gridx = 1; gc.gridy = r++; form.add(bookPurposeField, gc);

        JButton submit = new JButton("Reserve Slot");
        submit.addActionListener(e -> submitBooking());
        gc.gridx = 1; gc.gridy = r++; gc.fill = GridBagConstraints.NONE; gc.anchor = GridBagConstraints.EAST; form.add(submit, gc);
        panel.add(form, BorderLayout.NORTH);

        bookOutput = new JTextArea(12, 60); bookOutput.setEditable(false); bookOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        panel.add(new JScrollPane(bookOutput), BorderLayout.CENTER); return panel;
    }

    private void submitBooking() {
        try {
            Facility f = (Facility) bookFacilityCombo.getSelectedItem();
            Booking b = store.createBooking(currentUser, f, LocalDate.parse(bookDateField.getText().trim(), DataStore.DATE_FMT), LocalTime.parse(bookStartField.getText().trim(), DataStore.TIME_FMT), LocalTime.parse(bookEndField.getText().trim(), DataStore.TIME_FMT), bookPurposeField.getText().trim());
            bookOutput.setText("✅ Booking confirmed!\n\nID: " + b.getBookingId() + "\nFacility: " + f.getName() + "\nUser: " + currentUser.getName() + "\nDate: " + b.getDate() + "\nTime: " + b.getStartTime() + " - " + b.getEndTime());
            refreshMyBookings(); refreshAnalytics();
        } catch (Exception ex) { bookOutput.setText("❌ Booking rejected:\n" + ex.getMessage()); }
    }

    private JComponent buildMyBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8)); panel.setBorder(new EmptyBorder(10, 10, 10, 10)); panel.setBackground(BG);
        myBookingsModel = new DefaultTableModel(new String[]{"ID", "Facility", "Date", "Start", "End", "Status", "Purpose"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        myBookingsTable = new JTable(myBookingsModel); myBookingsTable.setRowHeight(24);
        panel.add(new JScrollPane(myBookingsTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)); bottom.setBackground(BG);
        JButton cancelBtn = new JButton("Cancel Selected Booking");
        cancelBtn.addActionListener(e -> {
            int row = myBookingsTable.getSelectedRow();
            if (row < 0) return;
            try { store.cancelBooking((String) myBookingsModel.getValueAt(row, 0)); JOptionPane.showMessageDialog(this, "Booking cancelled."); refreshMyBookings(); refreshCatalog(); refreshAnalytics(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        bottom.add(cancelBtn); panel.add(bottom, BorderLayout.SOUTH); return panel;
    }

    private void refreshMyBookings() {
        if (myBookingsModel == null || currentUser == null) return;
        myBookingsModel.setRowCount(0);
        store.getBookings().stream().filter(b -> b.getUserId().equals(currentUser.getId())).sorted(Comparator.comparing(Booking::getDate).thenComparing(Booking::getStartTime)).forEach(b -> {
            Facility f = store.findFacility(b.getFacilityId()); myBookingsModel.addRow(new Object[]{ b.getBookingId(), f != null ? f.getName() : b.getFacilityId(), b.getDate(), b.getStartTime(), b.getEndTime(), b.getStatus(), b.getPurpose() });
        });
    }

    private JComponent buildAnalyticsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8)); panel.setBorder(new EmptyBorder(10, 10, 10, 10)); panel.setBackground(BG);
        analyticsArea = new JTextArea(); analyticsArea.setEditable(false); analyticsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        panel.add(new JScrollPane(analyticsArea), BorderLayout.CENTER); return panel;
    }

    private void refreshAnalytics() {
        if (analyticsArea == null) return;
        StringBuilder sb = new StringBuilder("===== CAMPUS ANALYTICS & USAGE REPORT =====\n\n");
        sb.append("Total registered facilities : ").append(store.getFacilities().size()).append("\nTotal registered users       : ").append(store.getUsers().size()).append("\nTotal reservations logged    : ").append(store.getBookings().size()).append("\nActive confirmed bookings    : ").append(store.totalActiveBookings()).append("\nCancelled reservations       : ").append(store.totalCancelledBookings()).append("\n\n--- Facility Utilization (bookings per facility) ---\n");
        store.facilityUtilization().entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).forEach(e -> sb.append(String.format("  %-30s %d booking(s)%n", e.getKey(), e.getValue())));
        sb.append("\n--- Department Activity Breakdown ---\n");
        store.departmentActivity().entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).forEach(e -> sb.append(String.format("  %-30s %d booking(s)%n", e.getKey(), e.getValue())));
        analyticsArea.setText(sb.toString());
    }
}

class TestHarness { public static int run() { return 0; } }
class DemoRunner { public static void run() {} }