package Task07;

abstract class BaseVehicle {
    private final String regNo;

    protected BaseVehicle(String regNo) {
        this.regNo = regNo;
    }

    public final String getRegNo() { return regNo; }
    protected abstract double ratePerHour();

    // Template method marked final so subclasses cannot break ceiling billing rules
    public final double calculateFee(int minutes) {
        if (minutes <= 0) return 0.0;
        return ((minutes + 59) / 60) * ratePerHour();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + regNo + "]";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BaseVehicle && regNo.equals(((BaseVehicle) o).regNo);
    }

    @Override
    public int hashCode() {
        return regNo.hashCode();
    }
}

class StandardCar extends BaseVehicle {
    public StandardCar(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30.0; }
}

public class OverridingAndEqualsDemo {
    public static void main(String[] args) {
        BaseVehicle v1 = new StandardCar("TN02B2");
        BaseVehicle v2 = new StandardCar("TN02B2");
        System.out.println("v1: " + v1 + " | Fee (90 mins): Rs. " + v1.calculateFee(90));
        System.out.println("v1.equals(v2): " + v1.equals(v2));
        System.out.println("HashCodes match: " + (v1.hashCode() == v2.hashCode()));
    }
}