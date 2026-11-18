package Task06;

abstract class Vehicle {
    private final String regNo;

    public Vehicle(String regNo) {
        this.regNo = regNo;
        System.out.println("1. Vehicle constructor executed for: " + regNo);
    }

    public String getRegNo() { return regNo; }
    public abstract double getBaseHourlyRate();
}

class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) { super(regNo); }
    @Override public double getBaseHourlyRate() { return 10.00; }
}

class Car extends Vehicle {
    public Car(String regNo) { super(regNo); }
    @Override public double getBaseHourlyRate() { return 30.00; }
}

class ElectricVehicle extends Vehicle {
    private final double batteryCapacityKWh;

    public ElectricVehicle(String regNo, double batteryCapacityKWh) {
        super(regNo);
        this.batteryCapacityKWh = batteryCapacityKWh;
        System.out.println("2. ElectricVehicle constructor executed (Battery: " + batteryCapacityKWh + " kWh)");
    }

    public double getBatteryCapacityKWh() { return batteryCapacityKWh; }
    @Override public double getBaseHourlyRate() { return 25.00; }
}

class ElectricCar extends ElectricVehicle {
    private final int seatingCapacity;

    public ElectricCar(String regNo, double batteryCapacityKWh, int seatingCapacity) {
        super(regNo, batteryCapacityKWh);
        this.seatingCapacity = seatingCapacity;
        System.out.println("3. ElectricCar constructor executed (Seats: " + seatingCapacity + ")");
    }

    public int getSeatingCapacity() { return seatingCapacity; }
    @Override public double getBaseHourlyRate() { return 30.00; }
}

public class VehicleHierarchyDemo {
    public static void main(String[] args) {
        ElectricCar ev = new ElectricCar("TN03C3", 60.0, 5);
        System.out.println("Created " + ev.getRegNo()
                + " | Battery: " + ev.getBatteryCapacityKWh() + " kWh"
                + " | Seats: " + ev.getSeatingCapacity()
                + " | Rate: Rs. " + ev.getBaseHourlyRate() + "/hr");
    }
}