package campus.models;

/**
 * Represents sports courts (badminton, tennis, basketball).
 */
public class SportsCourt extends Facility {
    private String courtSurface;
    private boolean isIndoor;
    private boolean hasLighting;

    public SportsCourt(String id, String name, int capacity, String location, String details) {
        super(id, name, "SportsCourt", capacity, location);
        parseSportsDetails(details);
    }

    private void parseSportsDetails(String details) {
        String lower = details != null ? details.toLowerCase() : "";
        this.isIndoor = lower.contains("indoor");
        this.hasLighting = lower.contains("light") || lower.contains("flood");
        if (lower.contains("synthetic") || lower.contains("mat")) {
            this.courtSurface = "Synthetic Mat";
        } else if (lower.contains("clay")) {
            this.courtSurface = "Clay";
        } else if (lower.contains("wooden")) {
            this.courtSurface = "Polished Wood";
        } else {
            this.courtSurface = "Standard Hardcourt";
        }
    }

    public String getCourtSurface() {
        return courtSurface;
    }

    public void setCourtSurface(String courtSurface) {
        this.courtSurface = courtSurface;
    }

    public boolean isIndoor() {
        return isIndoor;
    }

    public void setIndoor(boolean indoor) {
        isIndoor = indoor;
    }

    public boolean isHasLighting() {
        return hasLighting;
    }

    public void setHasLighting(boolean hasLighting) {
        this.hasLighting = hasLighting;
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Type: %s, Surface: %s, Lighting: %s",
                isIndoor ? "Indoor Court" : "Outdoor Ground",
                courtSurface,
                hasLighting ? "Yes (Night Play OK)" : "Daytime Only");
    }
}
