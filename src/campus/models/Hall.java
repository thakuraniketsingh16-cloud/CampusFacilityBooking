package campus.models;

/**
 * Represents auditoriums and seminar halls on campus.
 */
public class Hall extends Facility {
    private boolean hasProjector;
    private boolean hasAudioSystem;
    private boolean isAirConditioned;

    public Hall(String id, String name, int capacity, String location, String details) {
        super(id, name, "Hall", capacity, location);
        parseHallDetails(details);
    }

    private void parseHallDetails(String details) {
        // Read features from description
        String lower = details != null ? details.toLowerCase() : "";
        this.hasProjector = lower.contains("projector") || lower.contains("screen") || lower.contains("board");
        this.hasAudioSystem = lower.contains("mic") || lower.contains("sound") || lower.contains("audio");
        this.isAirConditioned = lower.contains("ac") || lower.contains("air condition") || getCapacity() > 100;
    }

    public boolean isHasProjector() {
        return hasProjector;
    }

    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public boolean isHasAudioSystem() {
        return hasAudioSystem;
    }

    public void setHasAudioSystem(boolean hasAudioSystem) {
        this.hasAudioSystem = hasAudioSystem;
    }

    public boolean isAirConditioned() {
        return isAirConditioned;
    }

    public void setAirConditioned(boolean airConditioned) {
        isAirConditioned = airConditioned;
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Projector: %s, Sound/Mic: %s, AC: %s",
                hasProjector ? "Yes" : "No",
                hasAudioSystem ? "Equipped" : "No",
                isAirConditioned ? "Yes" : "Non-AC");
    }
}
