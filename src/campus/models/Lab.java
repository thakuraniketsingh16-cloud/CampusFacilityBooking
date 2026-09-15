package campus.models;

/**
 * Represents computer labs and research hardware laboratories.
 * Inherits from Facility.
 */
public class Lab extends Facility {
    private int numberOfComputers;
    private String operatingSystem;
    private boolean hasGpu;

    public Lab(String id, String name, int capacity, String location, String details) {
        super(id, name, "Lab", capacity, location);
        parseLabDetails(details);
    }

    // Helper to parse details string from file
    private void parseLabDetails(String details) {
        // Human style: sensible default fallback
        this.operatingSystem = "Ubuntu / Windows";
        this.numberOfComputers = getCapacity();
        this.hasGpu = details != null && details.toLowerCase().contains("gpu");
    }

    public int getNumberOfComputers() {
        return numberOfComputers;
    }

    public void setNumberOfComputers(int numberOfComputers) {
        this.numberOfComputers = numberOfComputers;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public boolean isHasGpu() {
        return hasGpu;
    }

    public void setHasGpu(boolean hasGpu) {
        this.hasGpu = hasGpu;
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Workstations: %d, OS: %s, Dedicated GPU: %s", 
                numberOfComputers, operatingSystem, (hasGpu ? "Yes (RTX Series)" : "Standard Integrated"));
    }
}
