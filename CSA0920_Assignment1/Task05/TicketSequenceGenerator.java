package Task05;

public class TicketSequenceGenerator {
    private static long counter = 5000L;
    private static int activeOccupancy = 0;

    static {
        System.out.println("[INIT] TicketSequenceGenerator initialized at baseline: " + counter);
    }

    public static synchronized String generateNextTicketId() {
        activeOccupancy++;
        return "TKT-" + (++counter);
    }

    public static synchronized void recordVehicleExit() {
        if (activeOccupancy > 0) activeOccupancy--;
    }

    public static int getActiveOccupancy() {
        return activeOccupancy;
    }

    public static void main(String[] args) {
        System.out.println("Generated: " + generateNextTicketId() + " | Occupancy: " + getActiveOccupancy());
        System.out.println("Generated: " + generateNextTicketId() + " | Occupancy: " + getActiveOccupancy());
        recordVehicleExit();
        System.out.println("After 1 Exit | Occupancy: " + getActiveOccupancy());
    }
}