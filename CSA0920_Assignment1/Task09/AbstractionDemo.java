package Task09;

interface Chargeable {
    double chargingFee(int kWh);

    default double applyFastChargerTax(double baseCharge) {
        return Math.round(baseCharge * 1.05 * 100.0) / 100.0;
    }

    static boolean isValidKWh(int kWh) {
        return kWh >= 0 && kWh <= 200;
    }
}

interface Payable {
    double generateBill(int minutes);
}

abstract class AbstractVehicle implements Payable {
    private final String regNo;

    protected AbstractVehicle(String regNo) {
        this.regNo = regNo;
    }

    public String getRegNo() { return regNo; }
    protected abstract double ratePerHour();

    @Override
    public double generateBill(int minutes) {
        return minutes <= 0 ? 0.0 : ((minutes + 59) / 60) * ratePerHour();
    }
}

class PlugInHybrid extends AbstractVehicle implements Chargeable {
    public PlugInHybrid(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30.0; }
    @Override public double chargingFee(int kWh) { return kWh * 12.0; }
}

public class AbstractionDemo {
    public static void main(String[] args) {
        PlugInHybrid phev = new PlugInHybrid("TN07EV7777");
        double parking = phev.generateBill(120);
        double chargingWithTax = Chargeable.isValidKWh(10)
                ? phev.applyFastChargerTax(phev.chargingFee(10)) : 0.0;

        System.out.println("Vehicle: " + phev.getRegNo());
        System.out.println("Parking Fee: Rs. " + parking);
        System.out.println("Charging Fee (with 5% tax): Rs. " + chargingWithTax);
    }
}