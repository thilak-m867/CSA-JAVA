package Q4;

import java.util.concurrent.atomic.AtomicInteger;

class StationThread extends Thread {
    private final int scripts;
    private final AtomicInteger total;

    StationThread(String name, int scripts, AtomicInteger total) {
        super(name);
        this.scripts = scripts;
        this.total = total;
    }

    @Override
    public void run() {
        for (int i = 1; i <= scripts; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                interrupt();
                return;
            }
        }
        total.addAndGet(scripts);
        System.out.println(getName() + " finished scanning " + scripts + " scripts.");
    }
}

class StationTask implements Runnable {
    private final String stationName;
    private final int scripts;
    private final AtomicInteger total;

    StationTask(String name, int scripts, AtomicInteger total) {
        this.stationName = name;
        this.scripts = scripts;
        this.total = total;
    }

    @Override
    public void run() {
        for (int i = 1; i <= scripts; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        total.addAndGet(scripts);
        System.out.println(stationName + " finished on " + Thread.currentThread().getName());
    }
}

public class ScannerCellDemo {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger total = new AtomicInteger(0);
        Thread[] stations = new Thread[4];

        for (int i = 0; i < 4; i++) {
            stations[i] = new Thread(
                    new StationTask("Station-" + (i + 1), 15, total),
                    "Worker-" + (i + 1)
            );
            stations[i].start();
        }

        System.out.println("Station-1 alive right after start(): " + stations[0].isAlive());

        for (Thread t : stations) {
            t.join();
        }

        System.out.println("All stations done. Total scripts scanned = " + total.get());
    }
}