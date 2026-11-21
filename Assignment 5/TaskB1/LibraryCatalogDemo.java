package TaskB1;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

record Book(String isbn, String title, String author, String genre, int year, boolean available) {}

public class LibraryCatalogDemo {
    public static void main(String[] args) {
        List<Book> catalog = List.of(
                new Book("978-0134685991", "Effective Java", "Joshua Bloch", "Programming", 2018, true),
                new Book("978-0262033848", "Introduction to Algorithms", "Thomas Cormen", "Algorithms", 2009, false),
                new Book("978-0132350884", "Clean Code", "Robert Martin", "Programming", 2008, true),
                new Book("978-1491950357", "Designing Data-Intensive Apps", "Martin Kleppmann", "Systems", 2017, true),
                new Book("978-0201633610", "Design Patterns", "Erich Gamma", "Programming", 1994, false)
        );

        // Available Programming books sorted by publication year descending
        List<Book> availableProg = catalog.stream()
                .filter(Book::available)
                .filter(b -> b.genre().equalsIgnoreCase("Programming"))
                .sorted(Comparator.comparingInt(Book::year).reversed())
                .toList();

        System.out.println("Available Programming Books (Newest First):");
        availableProg.forEach(b -> System.out.println("  " + b.title() + " (" + b.year() + ") by " + b.author()));

        // Group book count by genre
        Map<String, Long> countByGenre = catalog.stream()
                .collect(Collectors.groupingBy(Book::genre, Collectors.counting()));
        System.out.println("\nBook Count by Genre: " + countByGenre);
    }
}