package Q2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PlaylistBenchmark {

    static class Song {
        final int id;
        final String title;
        Song(int id) {
            this.id = id;
            this.title = "Track-" + id;
        }
    }

    public static void benchmarkSize(int n) {
        System.out.println("=== Playlist Size: " + n + " ===");
        int ops = 2_000;

        // 1. Add at End
        long t1 = System.nanoTime();
        List<Song> alEnd = new ArrayList<>();
        for (int i = 0; i < n; i++) alEnd.add(new Song(i));
        long alAddEnd = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        List<Song> llEnd = new LinkedList<>();
        for (int i = 0; i < n; i++) llEnd.add(new Song(i));
        long llAddEnd = System.nanoTime() - t2;

        // 2. Insert & Remove at Front (2,000 operations)
        List<Song> alFront = new ArrayList<>(alEnd);
        long t3 = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            alFront.add(0, new Song(i));
            alFront.remove(0);
        }
        long alFrontTime = System.nanoTime() - t3;

        List<Song> llFront = new LinkedList<>(llEnd);
        long t4 = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            llFront.add(0, new Song(i));
            llFront.remove(0);
        }
        long llFrontTime = System.nanoTime() - t4;

        // 3. Insert & Remove in Middle (2,000 operations)
        List<Song> alMid = new ArrayList<>(alEnd);
        long t5 = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            int mid = alMid.size() / 2;
            alMid.add(mid, new Song(i));
            alMid.remove(mid);
        }
        long alMidTime = System.nanoTime() - t5;

        List<Song> llMid = new LinkedList<>(llEnd);
        long t6 = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            int mid = llMid.size() / 2;
            llMid.add(mid, new Song(i));
            llMid.remove(mid);
        }
        long llMidTime = System.nanoTime() - t6;

        // 4. Random Access get(k) (2,000 lookups)
        Random rand = new Random(42);
        int[] indices = new int[ops];
        for (int i = 0; i < ops; i++) indices[i] = rand.nextInt(n);

        long t7 = System.nanoTime();
        long sum1 = 0;
        for (int idx : indices) sum1 += alEnd.get(idx).id;
        long alGetTime = System.nanoTime() - t7;

        long t8 = System.nanoTime();
        long sum2 = 0;
        for (int idx : indices) sum2 += llEnd.get(idx).id;
        long llGetTime = System.nanoTime() - t8;

        // 5. Full Iteration
        long t9 = System.nanoTime();
        for (Song s : alEnd) sum1 += s.id;
        long alIterTime = System.nanoTime() - t9;

        long t10 = System.nanoTime();
        for (Song s : llEnd) sum2 += s.id;
        long llIterTime = System.nanoTime() - t10;

        System.out.printf("Add End (%d):        ArrayList = %6.2f ms | LinkedList = %6.2f ms%n", n, alAddEnd / 1e6, llAddEnd / 1e6);
        System.out.printf("Front Ins/Rem (%d): ArrayList = %6.2f ms | LinkedList = %6.2f ms%n", ops, alFrontTime / 1e6, llFrontTime / 1e6);
        System.out.printf("Mid Ins/Rem (%d):   ArrayList = %6.2f ms | LinkedList = %6.2f ms%n", ops, alMidTime / 1e6, llMidTime / 1e6);
        System.out.printf("Random get(k) (%d): ArrayList = %6.2f ms | LinkedList = %6.2f ms%n", ops, alGetTime / 1e6, llGetTime / 1e6);
        System.out.printf("Full Iterate (%d):   ArrayList = %6.2f ms | LinkedList = %6.2f ms (check=%d)%n%n",
                n, alIterTime / 1e6, llIterTime / 1e6, (sum1 - sum2));
    }

    public static void main(String[] args) {
        benchmarkSize(1_000);
        benchmarkSize(20_000);
        benchmarkSize(100_000);
    }
}