package campus.models;

/**
 * Student entity. Has limited booking duration (2 hrs max) and restricted auditorium booking.
 */
public class Student extends User {
    private String regNumber;

    public Student(String userId, String name, String email, String department, String extraInfo) {
        super(userId, name, "Student", email, department);
        parseStudentExtra(extraInfo);
    }

    private void parseStudentExtra(String extra) {
        if (extra != null && extra.contains(":")) {
            String[] parts = extra.split(":");
            if (parts.length > 1) {
                this.regNumber = parts[1].trim();
            }
        }
        if (this.regNumber == null || this.regNumber.isEmpty()) {
            this.regNumber = "REG-" + getUserId();
        }
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    @Override
    public int getMaxBookingHours() {
        // Students are restricted to 2 hours per session
        return 2;
    }

    @Override
    public boolean canBookAuditoriumDirectly() {
        // Students cannot directly book large auditoriums without faculty approval
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " | RegNo: " + regNumber;
    }
}
