package TaskA3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

interface Identifiable<ID> {
    ID getId();
}

class Repository<T extends Identifiable<ID>, ID> {
    private final Map<ID, T> store = new HashMap<>();

    public void save(T entity) {
        store.put(entity.getId(), entity);
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean deleteById(ID id) {
        return store.remove(id) != null;
    }
}

record Student(String rollNo, String name, double cgpa) implements Identifiable<String> {
    @Override
    public String getId() { return rollNo; }
}

public class GenericRepositoryDemo {
    // Upper-bounded wildcard (? extends Number) to sum numeric collections
    public static double sumNumbers(List<? extends Number> numbers) {
        double total = 0.0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return total;
    }

    public static void main(String[] args) {
        Repository<Student, String> studentRepo = new Repository<>();
        studentRepo.save(new Student("192572046", "Shree Thilak", 9.1));
        studentRepo.save(new Student("192572018", "Arjun Nair", 8.6));

        System.out.println("All Students: " + studentRepo.findAll());
        System.out.println("Find 192572046: " + studentRepo.findById("192572046").orElse(null));

        List<Double> cgpas = studentRepo.findAll().stream().map(Student::cgpa).toList();
        System.out.printf("Average CGPA: %.2f%n", sumNumbers(cgpas) / cgpas.size());
    }
}