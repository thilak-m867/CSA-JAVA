package Task11;

import java.util.*;

enum UserType { STUDENT, STAFF, VISITOR }
enum SlotType { STANDARD, EV_CHARGING }

interface Chargeable {
    double chargingFee(int kWh);
    default double applyChargingTax(double amount) { return Math.round(amount * 1.05 * 100.0) / 100.0; }
}

abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank()) throw new IllegalArgumentException("Registration number required.");
        this.regNo = regNo.trim().toUpperCase();
    }

    public String getRegNo() { return regNo; }
    protected abstract double ratePerHour();
    public abstract SlotType getRequiredSlotType();

    public final double calculateFee(int minutes) {
        if (minutes <= 0) return 0.0;
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    @Override public boolean equals(Object o) {
        return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
    }
    @Override public int hashCode() { return regNo.hashCode(); }
}

class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 10.0; }
    @Override public SlotType getRequiredSlotType() { return SlotType.STANDARD; }
}

class Car extends Vehicle {
    public Car(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30.0; }
    @Override public SlotType getRequiredSlotType() { return SlotType.STANDARD; }
}

class ElectricCar extends Car implements Chargeable {
    private final boolean needsCharging;
    public ElectricCar(String regNo, boolean needsCharging) {
        super(regNo);
        this.needsCharging = needsCharging;
    }
    public boolean isNeedsCharging() { return needsCharging; }
    @Override public SlotType getRequiredSlotType() {
        return needsCharging ? SlotType.EV_CHARGING : SlotType.STANDARD;
    }
    @Override public double chargingFee(int kWh) {
        if (kWh < 0) throw new IllegalArgumentException("kWh cannot be negative.");
        return kWh * 12.0;
    }
}

class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private final SlotType slotType;
    private Vehicle occupant;

    public ParkingSlot(int floor, int slotNumber, SlotType slotType) {
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.slotType = slotType;
    }

    public boolean isAvailable() { return occupant == null; }
    public boolean canAccommodate(Vehicle v) { return isAvailable() && slotType == v.getRequiredSlotType(); }
    public void assign(Vehicle v) { this.occupant = v; }
    public void release() { this.occupant = null; }
    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
}

class ParkingTicket {
    private static long counter = 5000L;
    private final String ticketId;
    private final Vehicle vehicle;
    private final UserType userType;
    private final int floor;
    private final int slot;

    public ParkingTicket(Vehicle vehicle, UserType userType, int floor, int slot) {
        this.ticketId = "TKT-" + (++counter);
        this.vehicle = vehicle;
        this.userType = userType;
        this.floor = floor;
        this.slot = slot;
    }

    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public UserType getUserType() { return userType; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
}

public class CampusSmartParkingSystem {
    private final ParkingSlot[][] layout;
    private final Map<String, ParkingTicket> activeByRegNo = new HashMap<>();
    private double dailyCollection = 0.0;

    public CampusSmartParkingSystem(int[] stdPerFloor, int[] evPerFloor) {
        layout = new ParkingSlot[stdPerFloor.length][];
        for (int f = 0; f < stdPerFloor.length; f++) {
            layout[f] = new ParkingSlot[stdPerFloor[f] + evPerFloor[f]];
            int idx = 0;
            for (int s = 0; s < stdPerFloor[f]; s++) layout[f][idx++] = new ParkingSlot(f, idx - 1, SlotType.STANDARD);
            for (int e = 0; e < evPerFloor[f]; e++) layout[f][idx++] = new ParkingSlot(f, idx - 1, SlotType.EV_CHARGING);
        }
    }

    public ParkingTicket park(Vehicle v, UserType user) {
        if (activeByRegNo.containsKey(v.getRegNo())) {
            throw new IllegalStateException("Duplicate registration number already parked: " + v.getRegNo());
        }
        for (int f = 0; f < layout.length; f++) {
            for (int s = 0; s < layout[f].length; s++) {
                if (layout[f][s].canAccommodate(v)) {
                    layout[f][s].assign(v);
                    ParkingTicket t = new ParkingTicket(v, user, f, s);
                    activeByRegNo.put(v.getRegNo(), t);
                    return t;
                }
            }
        }
        return null; // Full
    }

    public double exit(String regNo, int durationMinutes, int kWhConsumed) {
        ParkingTicket t = activeByRegNo.remove(regNo.toUpperCase());
        if (t == null) throw new IllegalArgumentException("Active ticket not found for: " + regNo);
        layout[t.getFloor()][t.getSlot()].release();

        if (t.getUserType() == UserType.STUDENT || t.getUserType() == UserType.STAFF) {
            return 0.0; // Monthly pass exemption
        }
        Vehicle v = t.getVehicle();
        double fee = v.calculateFee(durationMinutes);
        if (v instanceof ElectricCar ec && ec.isNeedsCharging()) {
            fee += ec.chargingFee(kWhConsumed);
        }
        dailyCollection += fee;
        return fee;
    }

    public double getDailyCollection() { return dailyCollection; }

    public static void main(String[] args) {
        CampusSmartParkingSystem sys = new CampusSmartParkingSystem(new int[]{2, 1}, new int[]{1, 0});

        // 1. Normal & Boundary Entry Tests
        ParkingTicket t1 = sys.park(new TwoWheeler("TN01A1"), UserType.VISITOR);          // [0,0]
        ParkingTicket t2 = sys.park(new Car("TN02B2"), UserType.STAFF);                   // [0,1]
        ParkingTicket t3 = sys.park(new ElectricCar("TN03C3", true), UserType.VISITOR);   // [0,2]
        ParkingTicket t4 = sys.park(new Car("TN04D4"), UserType.VISITOR);                 // [1,0] (Last slot)
        ParkingTicket t5 = sys.park(new Car("TN05E5"), UserType.VISITOR);                 // null (Full)

        System.out.println("T1 (" + t1.getTicketId() + "): [" + t1.getFloor() + ", " + t1.getSlot() + "]");
        System.out.println("T2 (" + t2.getTicketId() + "): [" + t2.getFloor() + ", " + t2.getSlot() + "]");
        System.out.println("T3 (" + t3.getTicketId() + "): [" + t3.getFloor() + ", " + t3.getSlot() + "]");
        System.out.println("T4 (" + t4.getTicketId() + "): [" + t4.getFloor() + ", " + t4.getSlot() + "]");
        System.out.println("T5 when full: " + t5);

        // 2. Invalid Duplicate Registration Test
        try {
            sys.park(new TwoWheeler("TN01A1"), UserType.VISITOR);
        } catch (IllegalStateException e) {
            System.out.println("Duplicate Test Passed: " + e.getMessage());
        }

        // 3. Exit & Billing Tests
        double f1 = sys.exit("TN01A1", 60, 0);   // Exact 60-min boundary -> 1 hr @ 10 = Rs. 10.0
        double f2 = sys.exit("TN02B2", 240, 0);  // Staff pass -> Rs. 0.0
        double f3 = sys.exit("TN03C3", 130, 10); // 3 hrs @ 30 + 10*12 = Rs. 210.0
        double f4 = sys.exit("TN04D4", 0, 0);    // Zero-minute stay -> Rs. 0.0

        System.out.printf("Fees -> Bike: %.1f | Staff: %.1f | EV: %.1f | ZeroMin: %.1f%n", f1, f2, f3, f4);
        System.out.printf("Total Daily Collection: Rs. %.1f%n", sys.getDailyCollection());
    }
}