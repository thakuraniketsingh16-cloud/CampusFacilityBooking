package campus.models;

/**
 * Base abstract class representing any bookable facility on campus.
 * Demonstrates abstraction and encapsulation.
 */
public abstract class Facility {
    private String id;
    private String name;
    private String type;
    private int capacity;
    private String location;
    private boolean underMaintenance;

    // Default constructor
    public Facility() {
    }

    public Facility(String id, String name, String type, int capacity, String location) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.location = location;
        this.underMaintenance = false;
    }

    // Abstract method to be implemented by child classes (polymorphism)
    public abstract String getSpecificDetails();

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        } else {
            System.out.println("Warning: Capacity must be positive");
            this.capacity = 1;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s | Type: %-11s | Cap: %3d | Loc: %s", 
                id, name, type, capacity, location);
    }
}
