package Task04;

public class ParkingTicket {
    private final String ticketId;
    private final String vehicleNo;
    private final int floor;
    private final int slot;
    private final long entryTimeMillis;
    private long exitTimeMillis;

    // Overloaded Constructor 1: Defaults entryTimeMillis to current system clock
    public ParkingTicket(String ticketId, String vehicleNo, int floor, int slot) {
        this(ticketId, vehicleNo, floor, slot, System.currentTimeMillis());
    }

    // Overloaded Constructor 2: Master Parameterized Constructor
    public ParkingTicket(String ticketId, String vehicleNo, int floor, int slot, long entryTimeMillis) {
        if (ticketId == null || vehicleNo == null || floor < 0 || slot < 0) {
            throw new IllegalArgumentException("Invalid ticket parameters.");
        }
        this.ticketId = ticketId;
        this.vehicleNo = vehicleNo;
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMillis = entryTimeMillis;
        this.exitTimeMillis = -1L;
    }

    // Copy Constructor: Safely duplicates a ticket for receipt re-printing
    public ParkingTicket(ParkingTicket original) {
        this(original.ticketId, original.vehicleNo, original.floor, original.slot, original.entryTimeMillis);
        this.exitTimeMillis = original.exitTimeMillis;
    }

    public void markExit(long exitTimeMillis) { this.exitTimeMillis = exitTimeMillis; }
    public String getTicketId() { return ticketId; }
    public String getVehicleNo() { return vehicleNo; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }

    public static void main(String[] args) {
        ParkingTicket original = new ParkingTicket("TKT-101", "TN01AB1111", 0, 2);
        ParkingTicket duplicate = new ParkingTicket(original);
        System.out.println("Original: " + original.getTicketId() + " | Vehicle: " + original.getVehicleNo());
        System.out.println("Duplicate Copy: " + duplicate.getTicketId() + " | Slot: [" + duplicate.getFloor() + ", " + duplicate.getSlot() + "]");
    }
}