package Task01;

public class ParallelArrayLookupDemo {
    static final int MAX_CAPACITY = 100;
    static String[] regNo = new String[MAX_CAPACITY];
    static String[] type = new String[MAX_CAPACITY];
    static long[] entryTime = new long[MAX_CAPACITY];

    public static int findVehicleIndex(String targetReg, int count) {
        for (int i = 0; i < count; i++) {
            if (regNo[i].equals(targetReg)) {
                return i; // O(N) linear scan
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        regNo[0] = "TN01AB1111"; type[0] = "Car"; entryTime[0] = System.currentTimeMillis();
        regNo[1] = "TN02CD2222"; type[1] = "TwoWheeler"; entryTime[1] = System.currentTimeMillis();

        int idx = findVehicleIndex("TN02CD2222", 2);
        System.out.println("Found TN02CD2222 at index: " + idx + " (" + type[idx] + ")");
    }
}