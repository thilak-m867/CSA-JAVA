package Q6;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

class Patient {
    final String name;
    final int severity;
    final long arrival;

    Patient(String name, int severity, long arrival) {
        this.name = name;
        this.severity = severity;
        this.arrival = arrival;
    }

    @Override
    public String toString() {
        return name + "(S" + severity + ")";
    }
}

public class Triage {
    private long counter = 0;

    // TODO 1: Create a PriorityQueue that serves the lowest severity number first and,
    // for equal severity, the earliest arrival first
    private final PriorityQueue<Patient> queue = new PriorityQueue<>(
            Comparator.comparingInt((Patient p) -> p.severity)
                      .thenComparingLong(p -> p.arrival)
    );

    void admit(String name, int severity) {
        queue.add(new Patient(name, severity, counter++));
    }

    Patient next() {
        return queue.poll();
    }

    public static void benchmarkGrowth(int n) {
        Triage t = new Triage();
        Random r = new Random(42);

        long t1 = System.nanoTime();
        for (int i = 0; i < n; i++) t.admit("P" + i, 1 + r.nextInt(5));
        long addTime = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        while (t.next() != null) {}
        long pollTime = System.nanoTime() - t2;

        System.out.printf("Size = %7d | Admit Total = %6.2f ms (%.0f ns/op) | Poll Total = %6.2f ms (%.0f ns/op)%n",
                n, addTime / 1e6, (double) addTime / n, pollTime / 1e6, (double) pollTime / n);
    }

    public static void main(String[] args) {
        Triage er = new Triage();
        er.admit("Ravi", 3);
        er.admit("Meena", 1);
        er.admit("John", 3);
        er.admit("Asha", 5);
        er.admit("Kumar", 1);
        er.admit("Lata", 2);

        StringBuilder order = new StringBuilder();
        Patient p;
        while ((p = er.next()) != null) order.append(p).append(' ');
        System.out.println(order.toString().trim());

        System.out.println("\n--- Growth Investigation ---");
        benchmarkGrowth(1_000);
        benchmarkGrowth(50_000);
        benchmarkGrowth(500_000);
    }
}