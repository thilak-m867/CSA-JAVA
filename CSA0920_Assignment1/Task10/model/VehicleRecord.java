package Task10.model;

public class VehicleRecord {
    private final String regNo;
    private int durationMinutes;

    public VehicleRecord(String regNo) {
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("Registration number cannot be blank.");
        }
        this.regNo = regNo.trim().toUpperCase();
    }

    public String getRegNo() { return regNo; }

    public int getDurationMinutes() { return durationMinutes; }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative.");
        }
        this.durationMinutes = durationMinutes;
    }
}