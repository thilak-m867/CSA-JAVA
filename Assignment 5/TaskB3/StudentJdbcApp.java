package TaskB3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

record StudentRecord(String id, String name, String dept, double cgpa) {}

class StudentDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/simats_db";
    private static final String USER = "root";
    private static final String PWD = "root";

    public void upsertStudent(StudentRecord s) throws SQLException {
        String sql = "INSERT INTO students (student_id, full_name, department, cgpa) VALUES (?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), department = VALUES(department), cgpa = VALUES(cgpa)";
        try (Connection c = DriverManager.getConnection(URL, USER, PWD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.id());
            ps.setString(2, s.name());
            ps.setString(3, s.dept());
            ps.setDouble(4, s.cgpa());
            ps.executeUpdate();
        }
    }

    public List<StudentRecord> fetchAll() throws SQLException {
        List<StudentRecord> list = new ArrayList<>();
        String sql = "SELECT student_id, full_name, department, cgpa FROM students ORDER BY cgpa DESC";
        try (Connection c = DriverManager.getConnection(URL, USER, PWD);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new StudentRecord(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getDouble("cgpa")
                ));
            }
        }
        return list;
    }

    public boolean updateCgpa(String id, double newCgpa) throws SQLException {
        String sql = "UPDATE students SET cgpa = ? WHERE student_id = ?";
        try (Connection c = DriverManager.getConnection(URL, USER, PWD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, newCgpa);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteById(String id) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection c = DriverManager.getConnection(URL, USER, PWD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

public class StudentJdbcApp {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();
        try {
            dao.upsertStudent(new StudentRecord("192572046", "Shree Thilak", "CSE", 9.15));
            dao.upsertStudent(new StudentRecord("192572019", "Ananya Rao", "ECE", 8.85));
            dao.upsertStudent(new StudentRecord("192572077", "Vikram Menon", "CSE", 8.40));

            System.out.println("Initial Records:");
            dao.fetchAll().forEach(System.out::println);

            dao.updateCgpa("192572046", 9.25);
            dao.deleteById("192572077");

            System.out.println("\nAfter Update & Delete:");
            dao.fetchAll().forEach(System.out::println);
        } catch (SQLException e) {
            System.err.println("JDBC Error: " + e.getMessage());
        }
    }
}