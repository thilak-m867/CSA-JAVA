package Task10.app;

import Task10.model.VehicleRecord;
import Task10.service.BillingService;

public class EncapsulationDemo {
    public static void main(String[] args) {
        VehicleRecord rec = new VehicleRecord("tn01ab1111");
        rec.setDurationMinutes(125);

        BillingService service = new BillingService();
        System.out.println("Plate: " + rec.getRegNo() + " | Fee: Rs. " + service.calculateStandardFee(rec, 30.0));

        Integer slotA = 200;
        Integer slotB = 200;
        System.out.println("Integer == above 127: " + (slotA == slotB) + " | .equals(): " + slotA.equals(slotB));
    }
}