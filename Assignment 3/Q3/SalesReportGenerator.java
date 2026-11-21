package Q3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SalesReportGenerator {
    // 1. Manual close version: if reader #2 throws an exception, writer & reader #1 can leak
    public static void generateManual(List<Path> storeFiles, Path outFile) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(outFile);
        double grandTotal = 0.0;
        for (Path store : storeFiles) {
            BufferedReader reader = Files.newBufferedReader(store);
            String line;
            while ((line = reader.readLine()) != null) {
                grandTotal += Double.parseDouble(line.trim());
            }
            reader.close();
        }
        writer.write("Grand Total Sales: " + grandTotal);
        writer.close();
    }

    // 2. Robust try-with-resources version: closes in reverse declaration order even on failure
    public static void generateSafe(List<Path> storeFiles, Path outFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outFile)) {
            double grandTotal = 0.0;
            for (Path store : storeFiles) {
                try (BufferedReader reader = Files.newBufferedReader(store)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        grandTotal += Double.parseDouble(line.trim());
                    }
                } catch (NumberFormatException ex) {
                    throw new IOException("Corrupt data in store file: " + store, ex);
                }
            }
            writer.write("Grand Total Sales: " + grandTotal);
        }
    }

    public static void main(String[] args) throws IOException {
        Path s1 = Files.createTempFile("store1", ".txt");
        Path s2 = Files.createTempFile("store2", ".txt");
        Path report = Files.createTempFile("daily_report", ".txt");

        Files.writeString(s1, "1200.50\n800.00\n");
        Files.writeString(s2, "1500.25\n499.25\n");

        generateSafe(List.of(s1, s2), report);
        System.out.println("Safe Report Output: " + Files.readString(report));

        // Corrupt s2 to test safe cleanup
        Files.writeString(s2, "1500.25\nCORRUPT_ENTRY\n");
        try {
            generateSafe(List.of(s1, s2), report);
        } catch (IOException e) {
            System.out.println("Caught expected failure safely: " + e.getMessage());
        }

        Files.deleteIfExists(s1);
        Files.deleteIfExists(s2);
        Files.deleteIfExists(report);
    }
}