package TaskA1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CourseRegistrationDemo {
    public static void main(String[] args) {
        // 1. ArrayList: Preserves registration insertion order & allows duplicates
        List<String> rawRegistrations = new ArrayList<>(List.of(
                "192572046", "192572012", "192572046", "192572089", "192572012", "192572034"
        ));
        System.out.println("Raw Submissions (List): " + rawRegistrations);

        // 2. HashSet: Eliminates duplicate student IDs in O(1) average time
        Set<String> uniqueStudents = new HashSet<>(rawRegistrations);
        System.out.println("Unique Students (HashSet): " + uniqueStudents);

        // 3. TreeSet: Maintains sorted ascending order of registered IDs
        Set<String> sortedStudents = new TreeSet<>(uniqueStudents);
        System.out.println("Sorted Roster (TreeSet): " + sortedStudents);

        // 4. HashMap: Maps Course Code -> Set of enrolled Student IDs
        Map<String, Set<String>> courseMap = new HashMap<>();
        enroll(courseMap, "CSA09", "192572046");
        enroll(courseMap, "CSA09", "192572012");
        enroll(courseMap, "CSA09", "192572046"); // Duplicate ignored
        enroll(courseMap, "CSA12", "192572089");
        enroll(courseMap, "CSA12", "192572034");

        System.out.println("\nCourse-wise Enrollment (HashMap):");
        courseMap.forEach((course, students) ->
                System.out.println("  " + course + " (" + students.size() + " students) -> " + students)
        );
    }

    private static void enroll(Map<String, Set<String>> map, String course, String studentId) {
        map.computeIfAbsent(course, k -> new LinkedHashSet<>()).add(studentId);
    }
}