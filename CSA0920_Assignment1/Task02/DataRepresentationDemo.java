package Task02;

import java.util.Arrays;

public class DataRepresentationDemo {
    public static void main(String[] args) {
        // Irregular floor capacities: Floor 0 -> 2, Floor 1 -> 4, Floor 2 -> 3
        int[] slotsPerFloor = {2, 4, 3};
        String[][] slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }

        slots[0][1] = "TN01AB1111";
        slots[2][2] = "TN09XY9999";

        for (int f = 0; f < slots.length; f++) {
            System.out.println("Floor " + f + ": " + Arrays.toString(slots[f]));
        }

        // Casting & Integer Truncation Fix (90 mins at Rs. 30/hr)
        int minutes = 90;
        double rate = 30.0;
        double wrongFee = (minutes / 60) * rate;        // Truncates to 1 hr -> Rs. 30.0
        int billableHours = (minutes + 59) / 60;        // Ceiling -> 2 hrs
        double correctFee = billableHours * rate;       // Rs. 60.0

        System.out.println("Wrong Fee (truncated): Rs. " + wrongFee);
        System.out.println("Correct Fee (ceiling): Rs. " + correctFee);
    }
}