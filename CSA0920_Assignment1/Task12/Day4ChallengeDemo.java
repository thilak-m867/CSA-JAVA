package Task12;

import java.time.LocalTime;

// 1. Pass Abstraction replaces hardcoded UserType if-else checks
interface ParkingPass {
    boolean isExemptToday();
}

class MonthlyCampusPass implements ParkingPass {
    @Override public boolean isExemptToday() { return true; }
}

class VisitorOneDayPass implements ParkingPass {
    private final boolean validForToday;
    public VisitorOneDayPass(boolean validForToday) { this.validForToday = validForToday; }
    @Override public boolean isExemptToday() { return validForToday; }
}

class NoPass implements ParkingPass {
    @Override public boolean isExemptToday() { return false; }
}

// 2. PricingStrategy decouples dynamic/flat/peak rules from Vehicle hierarchy
interface PricingStrategy {
    double computeFee(int minutes, LocalTime entryTime);
}

class HourlyPricing implements PricingStrategy {
    private final double hourlyRate;
    public HourlyPricing(double hourlyRate) { this.hourlyRate = hourlyRate; }
    @Override public double computeFee(int minutes, LocalTime entryTime) {
        return minutes <= 0 ? 0.0 : ((minutes + 59) / 60) * hourlyRate;
    }
}

class FlatRatePricing implements PricingStrategy {
    private final double flatFee;
    public FlatRatePricing(double flatFee) { this.flatFee = flatFee; }
    @Override public double computeFee(int minutes, LocalTime entryTime) {
        return minutes <= 0 ? 0.0 : flatFee;
    }
}

class PeakHourMultiplierPricing implements PricingStrategy {
    private final double baseRate;
    public PeakHourMultiplierPricing(double baseRate) { this.baseRate = baseRate; }
    @Override public double computeFee(int minutes, LocalTime entryTime) {
        if (minutes <= 0) return 0.0;
        int hours = (minutes + 59) / 60;
        boolean isPeak = !entryTime.isBefore(LocalTime.of(9, 0)) && entryTime.isBefore(LocalTime.of(17, 0));
        double effectiveRate = isPeak ? baseRate * 2.0 : baseRate;
        return hours * effectiveRate;
    }
}

// 3. Extensible Vehicle Base + New Categories
abstract class ExtensibleVehicle {
    private final String regNo;
    private final PricingStrategy pricingStrategy;

    protected ExtensibleVehicle(String regNo, PricingStrategy pricingStrategy) {
        this.regNo = regNo;
        this.pricingStrategy = pricingStrategy;
    }
    public String getRegNo() { return regNo; }
    public double calculateFee(int minutes, LocalTime entryTime) {
        return pricingStrategy.computeFee(minutes, entryTime);
    }
}

class EBike extends ExtensibleVehicle {
    public EBike(String regNo) { super(regNo, new FlatRatePricing(15.0)); } // Flat Rs. 15
}

class DeliveryVan extends ExtensibleVehicle {
    public DeliveryVan(String regNo) { super(regNo, new PeakHourMultiplierPricing(30.0)); } // 2x Car rate (Rs. 60/hr) 9AM-5PM
}

public class Day4ChallengeDemo {
    public static void main(String[] args) {
        // 5 irregular floors: {20, 35, 50, 75, 100} slots
        int[] fiveIrregularFloors = {20, 35, 50, 75, 100};
        System.out.println("Configured Floors: " + fiveIrregularFloors.length + " | Floor 4 Slots: " + fiveIrregularFloors[4]);

        ExtensibleVehicle ebike = new EBike("TN09EB101");
        ExtensibleVehicle vanPeak = new DeliveryVan("TN09DV202");
        ExtensibleVehicle vanOffPeak = new DeliveryVan("TN09DV303");

        ParkingPass visitorDayPass = new VisitorOneDayPass(true);
        ParkingPass regularVisitor = new NoPass();

        double ebikeFee = visitorDayPass.isExemptToday() ? 0.0 : ebike.calculateFee(180, LocalTime.of(10, 30));
        double vanPeakFee = regularVisitor.isExemptToday() ? 0.0 : vanPeak.calculateFee(120, LocalTime.of(11, 0));
        double vanOffPeakFee = regularVisitor.isExemptToday() ? 0.0 : vanOffPeak.calculateFee(120, LocalTime.of(19, 0));

        System.out.println("E-Bike (3 hrs, Visitor One-Day Pass): Rs. " + ebikeFee);
        System.out.println("Delivery Van (2 hrs @ 11:00 AM Peak): Rs. " + vanPeakFee);
        System.out.println("Delivery Van (2 hrs @ 07:00 PM Off-Peak): Rs. " + vanOffPeakFee);
    }
}