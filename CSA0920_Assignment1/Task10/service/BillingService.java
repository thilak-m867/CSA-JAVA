package Task10.service;

import Task10.model.VehicleRecord;

public class BillingService {
    int computeHours(int minutes) {
        return minutes <= 0 ? 0 : (minutes + 59) / 60;
    }

    public double calculateStandardFee(VehicleRecord record, double ratePerHour) {
        return computeHours(record.getDurationMinutes()) * ratePerHour;
    }
}