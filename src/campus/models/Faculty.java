package campus.models;

/**
 * Faculty entity. Has higher booking limits (up to 6 hrs) and auditorium access.
 */
public class Faculty extends User {
    private String employeeId;
    private String designation;

    public Faculty(String userId, String name, String email, String department, String extraInfo) {
        super(userId, name, "Faculty", email, department);
        parseFacultyExtra(extraInfo);
    }

    private void parseFacultyExtra(String extra) {
        this.employeeId = "EMP-" + getUserId();
        this.designation = "Faculty Member";

        if (extra != null) {
            String[] tokens = extra.split(",");
            for (String t : tokens) {
                if (t.toLowerCase().contains("empid")) {
                    String[] sub = t.split(":");
                    if (sub.length > 1) this.employeeId = sub[1].trim();
                } else {
                    this.designation = t.trim();
                }
            }
        }
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public int getMaxBookingHours() {
        // Faculty can reserve slots up to 6 hours for research / seminars
        return 6;
    }

    @Override
    public boolean canBookAuditoriumDirectly() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " | " + designation + " (" + employeeId + ")";
    }
}
