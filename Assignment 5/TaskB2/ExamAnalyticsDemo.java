package TaskB2;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

record ExamResult(String studentId, String name, String subject, int marks) {}

public class ExamAnalyticsDemo {
    public static void main(String[] args) {
        List<ExamResult> results = List.of(
                new ExamResult("192572046", "Shree Thilak", "CSA09", 94),
                new ExamResult("192572012", "Karthik R", "CSA09", 78),
                new ExamResult("192572089", "Divya S", "CSA09", 45),
                new ExamResult("192572046", "Shree Thilak", "CSA12", 89),
                new ExamResult("192572012", "Karthik R", "CSA12", 82),
                new ExamResult("192572089", "Divya S", "CSA12", 61)
        );

        // Summary statistics for CSA09
        DoubleSummaryStatistics stats = results.stream()
                .filter(r -> r.subject().equals("CSA09"))
                .mapToDouble(ExamResult::marks)
                .summaryStatistics();
        System.out.printf("CSA09 Stats -> Count: %d | Min: %.1f | Max: %.1f | Avg: %.2f%n",
                stats.getCount(), stats.getMin(), stats.getMax(), stats.getAverage());

        // Partition into Pass (>= 50) vs Fail (< 50)
        Map<Boolean, List<ExamResult>> passFail = results.stream()
                .collect(Collectors.partitioningBy(r -> r.marks() >= 50));
        System.out.println("Passed Entries: " + passFail.get(true).size());
        System.out.println("Failed Entries: " + passFail.get(false));

        // Average score per student
        Map<String, Double> avgPerStudent = results.stream()
                .collect(Collectors.groupingBy(
                        ExamResult::name,
                        Collectors.averagingInt(ExamResult::marks)
                ));
        System.out.println("Student Averages: " + avgPerStudent);
    }
}