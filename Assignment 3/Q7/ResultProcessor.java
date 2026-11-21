package Q7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultProcessor {
    static class Outcome {
        final boolean ok;
        final String studentId;
        final double score;
        final String error;

        Outcome(String id, double sc) {
            this.ok = true;
            this.studentId = id;
            this.score = sc;
            this.error = null;
        }

        Outcome(String err) {
            this.ok = false;
            this.studentId = null;
            this.score = 0;
            this.error = err;
        }
    }

    static class ParseTask implements Callable<Outcome> {
        private final Path file;

        ParseTask(Path file) {
            this.file = file;
        }

        @Override
        public Outcome call() {
            try {
                List<String> lines = Files.readAllLines(file);
                if (lines.isEmpty()) return new Outcome("Empty file: " + file.getFileName());
                String[] parts = lines.get(0).split(",");
                if (parts.length != 2) return new Outcome("Corrupt format: " + file.getFileName());
                double marks = Double.parseDouble(parts[1].trim());
                if (marks < 0 || marks > 100) return new Outcome("Out-of-range marks: " + file.getFileName());
                return new Outcome(parts[0].trim(), marks);
            } catch (IOException | NumberFormatException ex) {
                return new Outcome(file.getFileName() + " -> " + ex.getClass().getSimpleName());
            }
        }
    }

    public static void runBatch(List<Path> files, int threads) throws Exception {
        long start = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        ConcurrentHashMap<String, Double> validResults = new ConcurrentHashMap<>();
        AtomicInteger okCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<Future<Outcome>> futures = new ArrayList<>(files.size());
        for (Path p : files) {
            futures.add(pool.submit(new ParseTask(p)));
        }

        for (Future<Outcome> f : futures) {
            Outcome o = f.get();
            if (o.ok) {
                validResults.put(o.studentId, o.score);
                okCount.incrementAndGet();
            } else {
                failCount.incrementAndGet();
            }
        }
        pool.shutdown();
        double ms = (System.nanoTime() - start) / 1e6;
        System.out.printf("Threads = %d | Time = %6.2f ms | Valid = %d | Failed = %d%n",
                threads, ms, okCount.get(), failCount.get());
    }

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("exam_results");
        List<Path> files = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Path p = dir.resolve("s_" + i + ".txt");
            if (i % 25 == 0) {
                Files.writeString(p, "S" + i + ",CORRUPT_SCORE"); // Inject corrupt file
            } else if (i % 40 == 0) {
                // Inject missing file (do not create on disk)
            } else {
                Files.writeString(p, "S" + i + "," + (60 + (i % 40)));
            }
            files.add(p);
        }

        for (int threads : new int[]{1, 2, 4, 8}) {
            runBatch(files, threads);
        }

        for (Path p : files) {
            Files.deleteIfExists(p);
        }
        Files.deleteIfExists(dir);
    }
}