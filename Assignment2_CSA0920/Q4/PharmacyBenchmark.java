package Q4;

import java.util.*;

public class PharmacyBenchmark {

    static class Medicine {
        final String code;
        final String name;
        int stock;

        Medicine(String code, String name, int stock) {
            this.code = code;
            this.name = name;
            this.stock = stock;
        }
    }

    public static void testInventory(int size) {
        System.out.println("=== Pharmacy Inventory Size: " + size + " medicines ===");
        List<String> codes = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            codes.add(String.format("MED-%06d", i));
        }
        Collections.shuffle(codes, new Random(123));

        Map<String, Medicine> hashMap = new HashMap<>();
        Map<String, Medicine> linkedMap = new LinkedHashMap<>();
        Map<String, Medicine> treeMap = new TreeMap<>();

        for (String c : codes) {
            Medicine m = new Medicine(c, "Drug_" + c, 100);
            hashMap.put(c, m);
            linkedMap.put(c, m);
            treeMap.put(c, m);
        }

        int lookups = 100_000;
        String[] queries = new String[lookups];
        Random r = new Random(99);
        for (int i = 0; i < lookups; i++) {
            queries[i] = codes.get(r.nextInt(size));
        }

        long t1 = System.nanoTime();
        long check = 0;
        for (String q : queries) check += hashMap.get(q).stock;
        long hmLookup = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        for (String q : queries) check += linkedMap.get(q).stock;
        long lhmLookup = System.nanoTime() - t2;

        long t3 = System.nanoTime();
        for (String q : queries) check += treeMap.get(q).stock;
        long tmLookup = System.nanoTime() - t3;

        // Sorted Code Report
        long t4 = System.nanoTime();
        List<String> sortedFromHM = new ArrayList<>(hashMap.keySet());
        Collections.sort(sortedFromHM);
        long hmSortReport = System.nanoTime() - t4;

        long t5 = System.nanoTime();
        List<String> sortedFromTM = new ArrayList<>(treeMap.keySet());
        long tmSortReport = System.nanoTime() - t5;

        // Insertion Order Report
        long t6 = System.nanoTime();
        List<String> insertFromLHM = new ArrayList<>(linkedMap.keySet());
        long lhmInsertReport = System.nanoTime() - t6;

        System.out.printf("100k Lookups:      HashMap = %6.2f ms | LinkedHashMap = %6.2f ms | TreeMap = %6.2f ms%n",
                hmLookup / 1e6, lhmLookup / 1e6, tmLookup / 1e6);
        System.out.printf("Code-Order Report: HashMap(+sort) = %5.2f ms | TreeMap(natural) = %5.2f ms (rows=%d)%n",
                hmSortReport / 1e6, tmSortReport / 1e6, sortedFromTM.size());
        System.out.printf("Insert-Order Rpt:  LinkedHashMap(natural) = %5.2f ms (rows=%d, check=%d)%n%n",
                lhmInsertReport / 1e6, insertFromLHM.size(), check);
    }

    public static void main(String[] args) {
        testInventory(500);
        testInventory(5_000);
        testInventory(50_000);
    }
}