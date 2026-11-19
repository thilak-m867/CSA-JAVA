package Q1;

import java.util.ArrayList;
import java.util.List;

public class TicketBenchmark {

    private static List<String> generateFields(int count) {
        List<String> fields = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            fields.add("FIELD_" + i + ":VAL_" + (1000 + i) + "\n");
        }
        return fields;
    }

    public static String buildWithConcat(List<String> fields) {
        String ticket = "";
        for (String f : fields) {
            ticket = ticket + f;
        }
        return ticket;
    }

    public static String buildWithBuilder(List<String> fields) {
        StringBuilder sb = new StringBuilder();
        for (String f : fields) {
            sb.append(f);
        }
        return sb.toString();
    }

    public static String buildWithBuffer(List<String> fields) {
        StringBuffer sbuf = new StringBuffer();
        for (String f : fields) {
            sbuf.append(f);
        }
        return sbuf.toString();
    }

    private static long getUsedMemory() {
        System.gc();
        try { Thread.sleep(20); } catch (InterruptedException ignored) {}
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public static void runWorkload(String name, int tickets, int fieldCount) {
        List<String> fields = generateFields(fieldCount);
        int runs = 5;

        // Warmup JVM JIT
        for (int i = 0; i < 50; i++) {
            buildWithConcat(fields);
            buildWithBuilder(fields);
            buildWithBuffer(fields);
        }

        System.out.println("=== " + name + " (" + tickets + " tickets x " + fieldCount + " fields) ===");

        // 1. String Concatenation (+)
        long memBefore = getUsedMemory();
        long totalTime = 0;
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            for (int t = 0; t < tickets; t++) buildWithConcat(fields);
            totalTime += (System.nanoTime() - start);
        }
        long memAfter = getUsedMemory();
        long estObjectsConcat = (long) tickets * fieldCount;
        System.out.printf("String (+):    Avg Time = %8.3f ms | Est. Objects = %9d | Delta Mem = %6d KB%n",
                (totalTime / runs) / 1e6, estObjectsConcat, Math.max(0, (memAfter - memBefore) / 1024));

        // 2. StringBuilder
        memBefore = getUsedMemory();
        totalTime = 0;
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            for (int t = 0; t < tickets; t++) buildWithBuilder(fields);
            totalTime += (System.nanoTime() - start);
        }
        memAfter = getUsedMemory();
        long estObjectsBuilder = (long) tickets * 2;
        System.out.printf("StringBuilder: Avg Time = %8.3f ms | Est. Objects = %9d | Delta Mem = %6d KB%n",
                (totalTime / runs) / 1e6, estObjectsBuilder, Math.max(0, (memAfter - memBefore) / 1024));

        // 3. StringBuffer
        memBefore = getUsedMemory();
        totalTime = 0;
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            for (int t = 0; t < tickets; t++) buildWithBuffer(fields);
            totalTime += (System.nanoTime() - start);
        }
        memAfter = getUsedMemory();
        System.out.printf("StringBuffer:  Avg Time = %8.3f ms | Est. Objects = %9d | Delta Mem = %6d KB%n%n",
                (totalTime / runs) / 1e6, estObjectsBuilder, Math.max(0, (memAfter - memBefore) / 1024));
    }

    public static void main(String[] args) {
        runWorkload("Workload A (Short)", 100, 10);
        runWorkload("Workload B (Standard Heavy)", 10_000, 50);
        runWorkload("Workload C (Large Manifest)", 2_000, 200);
    }
}