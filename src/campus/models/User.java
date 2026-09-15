package campus.models;

/**
 * Base abstract class for campus users (Students and Faculty).
 */
public abstract class User {
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

    // Abstract methods illustrating polymorphism
    public abstract int getMaxBookingHours();
    public abstract boolean canBookAuditoriumDirectly();

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    // A small helper method
    public boolean checkEmailFormat() {
        // Simple check, student style
        if (email == null) return false;
        return email.contains("@") && email.contains(".");
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Role: %-8s | Dept: %-15s", 
                userId, name, role, department);
    }
}
