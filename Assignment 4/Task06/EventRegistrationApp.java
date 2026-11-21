package Task06;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

class Registration {
    final String studentId;
    final String studentName;
    final String eventName;

    Registration(String id, String name, String event) {
        this.studentId = id;
        this.studentName = name;
        this.eventName = event;
    }
}

class RegistrationDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/campus_events";
    private static final String USER = "root";
    private static final String PWD = "root";

    public boolean exists(String studentId, String eventName) throws SQLException {
        String sql = "SELECT 1 FROM registrations WHERE student_id = ? AND event_name = ?";
        try (Connection c = DriverManager.getConnection(URL, USER, PWD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, eventName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insert(Registration r) throws SQLException {
        String sql = "INSERT INTO registrations (student_id, student_name, event_name) VALUES (?, ?, ?)";
        try (Connection c = DriverManager.getConnection(URL, USER, PWD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.studentId);
            ps.setString(2, r.studentName);
            ps.setString(3, r.eventName);
            ps.executeUpdate();
        }
    }

    public List<Registration> fetchPage(String eventFilter, int limit, int offset) throws SQLException {
        List<Registration> list = new ArrayList<>();
        boolean all = "All Events".equals(eventFilter);
        String sql = all
                ? "SELECT student_id, student_name, event_name FROM registrations ORDER BY reg_id DESC LIMIT ? OFFSET ?"
                : "SELECT student_id, student_name, event_name FROM registrations WHERE event_name = ? ORDER BY reg_id DESC LIMIT ? OFFSET ?";
        try (Connection c = DriverManager.getConnection(URL, USER, PWD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (all) {
                ps.setInt(1, limit);
                ps.setInt(2, offset);
            } else {
                ps.setString(1, eventFilter);
                ps.setInt(2, limit);
                ps.setInt(3, offset);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Registration(rs.getString(1), rs.getString(2), rs.getString(3)));
                }
            }
        }
        return list;
    }
}

public class EventRegistrationApp extends JFrame {
    private final JTextField idField = new JTextField(12);
    private final JTextField nameField = new JTextField(15);
    private final JComboBox<String> eventBox =
            new JComboBox<>(new String[]{"Hackathon", "Robotics", "CodeSprint", "Quiz"});
    private final JComboBox<String> filterBox =
            new JComboBox<>(new String[]{"All Events", "Hackathon", "Robotics", "CodeSprint", "Quiz"});
    private final DefaultTableModel model =
            new DefaultTableModel(new String[]{"Student ID", "Student Name", "Event"}, 0);
    private final JLabel statusLabel = new JLabel("Ready.");
    private final RegistrationDAO dao = new RegistrationDAO();
    private int offset = 0;
    private static final int PAGE_SIZE = 50;

    public EventRegistrationApp() {
        super("Campus Event Registration");
        setLayout(new BorderLayout(8, 8));

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportItem = new JMenuItem("Export Visible Table to CSV");
        JMenuItem exitItem = new JMenuItem("Exit");
        exportItem.addActionListener(e -> exportCsv());
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exportItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton registerBtn = new JButton("Register");
        regRow.add(new JLabel("Student ID:"));
        regRow.add(idField);
        regRow.add(new JLabel("Name:"));
        regRow.add(nameField);
        regRow.add(new JLabel("Event:"));
        regRow.add(eventBox);
        regRow.add(registerBtn);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton prevBtn = new JButton("Prev Page");
        JButton nextBtn = new JButton("Next Page");
        filterRow.add(new JLabel("Filter:"));
        filterRow.add(filterBox);
        filterRow.add(prevBtn);
        filterRow.add(nextBtn);

        topPanel.add(regRow);
        topPanel.add(filterRow);

        JTable table = new JTable(model);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> registerStudent());
        filterBox.addActionListener(e -> { offset = 0; loadPage(); });
        prevBtn.addActionListener(e -> { if (offset >= PAGE_SIZE) { offset -= PAGE_SIZE; loadPage(); } });
        nextBtn.addActionListener(e -> { offset += PAGE_SIZE; loadPage(); });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(760, 440);
        setLocationRelativeTo(null);
        loadPage();
    }

    private void registerStudent() {
        String sid = idField.getText().trim();
        String sname = nameField.getText().trim();
        String ev = (String) eventBox.getSelectedItem();

        if (sid.isEmpty() || sname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and Name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        statusLabel.setText("Registering...");
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                if (dao.exists(sid, ev)) return "DUPLICATE";
                dao.insert(new Registration(sid, sname, ev));
                return "OK";
            }

            @Override
            protected void done() {
                try {
                    String res = get();
                    if ("DUPLICATE".equals(res)) {
                        statusLabel.setText("Duplicate rejected.");
                        JOptionPane.showMessageDialog(EventRegistrationApp.this,
                                "Student " + sid + " is already registered for " + ev,
                                "Duplicate Registration", JOptionPane.WARNING_MESSAGE);
                    } else {
                        statusLabel.setText("Registered " + sid + " for " + ev);
                        idField.setText("");
                        nameField.setText("");
                        loadPage();
                    }
                } catch (Exception ex) {
                    statusLabel.setText("DB Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadPage() {
        String filter = (String) filterBox.getSelectedItem();
        statusLabel.setText("Loading page...");
        new SwingWorker<List<Registration>, Void>() {
            @Override
            protected List<Registration> doInBackground() throws Exception {
                return dao.fetchPage(filter, PAGE_SIZE, offset);
            }

            @Override
            protected void done() {
                try {
                    List<Registration> rows = get();
                    model.setRowCount(0);
                    for (Registration r : rows) {
                        model.addRow(new Object[]{r.studentId, r.studentName, r.eventName});
                    }
                    statusLabel.setText("Showing " + rows.size() + " records (offset " + offset + ")");
                } catch (Exception ex) {
                    statusLabel.setText("Load failed: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void exportCsv() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("registrations_export.csv"))) {
            pw.println("StudentID,StudentName,Event");
            for (int r = 0; r < model.getRowCount(); r++) {
                pw.println(model.getValueAt(r, 0) + "," + model.getValueAt(r, 1) + "," + model.getValueAt(r, 2));
            }
            statusLabel.setText("Exported to registrations_export.csv");
        } catch (Exception ex) {
            statusLabel.setText("Export error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EventRegistrationApp().setVisible(true));
    }
}