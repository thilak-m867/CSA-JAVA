package TaskB4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

record StudentProfile(String id, String name, String dept, double cgpa) {}

public class MiniProjectApp {
    private static final String URL = "jdbc:mysql://localhost:3306/simats_db";
    private static final String USER = "root";
    private static final String PWD = "root";

    public static List<StudentProfile> loadFromDb() throws SQLException {
        List<StudentProfile> students = new ArrayList<>();
        String sql = "SELECT student_id, full_name, department, cgpa FROM students";
        try (Connection c = DriverManager.getConnection(URL, USER, PWD);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(new StudentProfile(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getDouble("cgpa")
                ));
            }
        }
        return students;
    }

    public static void main(String[] args) {
        try {
            List<StudentProfile> cohort = loadFromDb();
            System.out.println("Loaded " + cohort.size() + " student records from MySQL.\n");

            // Stream 1: Honor Roll (CGPA >= 8.5)
            System.out.println("Honor Roll (CGPA >= 8.5):");
            cohort.stream()
                    .filter(s -> s.cgpa() >= 8.5)
                    .sorted(Comparator.comparingDouble(StudentProfile::cgpa).reversed())
                    .forEach(s -> System.out.printf("  %s - %s (%s): %.2f%n", s.id(), s.name(), s.dept(), s.cgpa()));

            // Stream 2: Average CGPA by Department
            Map<String, Double> deptAvg = cohort.stream()
                    .collect(Collectors.groupingBy(
                            StudentProfile::dept,
                            Collectors.averagingDouble(StudentProfile::cgpa)
                    ));
            System.out.println("\nDepartment Average CGPA: " + deptAvg);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}