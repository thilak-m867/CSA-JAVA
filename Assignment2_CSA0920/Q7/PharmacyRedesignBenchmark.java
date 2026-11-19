package Q7;

import java.util.*;

public class PharmacyRedesignBenchmark {

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

    static class OriginalPharmacy {
        private final Map<String, Medicine> map = new LinkedHashMap<>();

        public void addOrUpdate(Medicine m) {
            map.put(m.code, m);
        }

        public Medicine lookup(String code) {
            return map.get(code);
        }

        public List<Medicine> getInsertionOrderReport() {
            return new ArrayList<>(map.values());
        }

        public List<Medicine> getCodeOrderReport() {
            List<Medicine> list = new ArrayList<>(map.values());
            list.sort(Comparator.comparing(m -> m.code));
            return list;
        }
    }

    static class RedesignedPharmacy {
        private final Map<String, Medicine> map = new LinkedHashMap<>();
        private List<Medicine> cachedSortedReport = Collections.emptyList();
        private boolean dirty = false;

        public void addOrUpdate(Medicine m) {
            boolean isNewKey = !map.containsKey(m.code);
            map.put(m.code, m);
            if (isNewKey) {
                dirty = true;
            }
        }

        public Medicine lookup(String code) {
            return map.get(code);
        }

        public Collection<Medicine> getInsertionOrderReport() {
            return Collections.unmodifiableCollection(map.values());
        }

        public List<Medicine> getCodeOrderReport() {
            if (dirty) {
                List<Medicine> sorted = new ArrayList<>(map.values());
                sorted.sort(Comparator.comparing(m -> m.code));
                cachedSortedReport = Collections.unmodifiableList(sorted);
                dirty = false;
            }
            return cachedSortedReport;
        }
    }

    public static void main(String[] args) {
        int n = 50_000;
        int reportCalls = 20;

        OriginalPharmacy orig = new OriginalPharmacy();
        RedesignedPharmacy redesign = new RedesignedPharmacy();

        List<String> codes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) codes.add(String.format("MED-%06d", i));
        Collections.shuffle(codes, new Random(42));

        for (String c : codes) {
            orig.addOrUpdate(new Medicine(c, "Drug_" + c, 100));
            redesign.addOrUpdate(new Medicine(c, "Drug_" + c, 100));
        }

        long t1 = System.nanoTime();
        for (int i = 0; i < 1_000; i++) {
            String c = codes.get(i);
            orig.addOrUpdate(new Medicine(c, "Drug_" + c, 95));
        }
        int check1 = 0;
        for (int r = 0; r < reportCalls; r++) {
            check1 += orig.getCodeOrderReport().size();
        }
        long origTime = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        for (int i = 0; i < 1_000; i++) {
            String c = codes.get(i);
            redesign.addOrUpdate(new Medicine(c, "Drug_" + c, 95));
        }
        int check2 = 0;
        for (int r = 0; r < reportCalls; r++) {
            check2 += redesign.getCodeOrderReport().size();
        }
        long redesignTime = System.nanoTime() - t2;

        System.out.printf("Original Solution (20 reports + 1k stock updates):   %7.2f ms (check=%d)%n", origTime / 1e6, check1);
        System.out.printf("Redesigned Solution (20 reports + 1k stock updates): %7.2f ms (check=%d)%n", redesignTime / 1e6, check2);
        System.out.printf("Speedup Factor: %.1fx faster%n", (double) origTime / redesignTime);
    }
}