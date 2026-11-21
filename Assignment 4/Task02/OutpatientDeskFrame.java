package Task02;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class OutpatientDeskFrame extends JFrame {
    private final JTextField patientIdField = new JTextField(15);
    private final JComboBox<String> deptBox =
            new JComboBox<>(new String[]{"Cardiology", "Neurology", "Orthopedics", "General Medicine"});
    private final JComboBox<String> doctorBox = new JComboBox<>();
    private final JComboBox<String> slotBox =
            new JComboBox<>(new String[]{"09:30 AM", "10:30 AM", "11:30 AM", "02:30 PM", "04:00 PM"});
    private final JLabel statusLabel = new JLabel("Ready for booking.");

    private final Map<String, List<String>> doctorMap = Map.of(
            "Cardiology", List.of("Dr. Ramesh Kumar", "Dr. Sneha Nair"),
            "Neurology", List.of("Dr. Arvind Menon", "Dr. Priya S"),
            "Orthopedics", List.of("Dr. Karthik R", "Dr. Divya V"),
            "General Medicine", List.of("Dr. Suresh Babu", "Dr. Meena K")
    );
    private final Set<String> bookedSlots = new HashSet<>();
    private boolean inProgress = false;

    public OutpatientDeskFrame() {
        super("Hospital Outpatient Appointment Desk");
        setLayout(new BorderLayout(8, 8));

        JLabel header = new JLabel("OUTPATIENT APPOINTMENT BOOKING", SwingConstants.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Patient ID:"));
        form.add(patientIdField);
        form.add(new JLabel("Department:"));
        form.add(deptBox);
        form.add(new JLabel("Doctor:"));
        form.add(doctorBox);
        form.add(new JLabel("Time Slot:"));
        form.add(slotBox);
        form.add(new JLabel("Booking Status:"));
        form.add(statusLabel);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        JButton bookBtn = new JButton("Book Appointment");
        JButton clearBtn = new JButton("Clear Form");
        buttons.add(bookBtn);
        buttons.add(clearBtn);
        add(buttons, BorderLayout.SOUTH);

        patientIdField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { inProgress = true; }
            public void removeUpdate(DocumentEvent e) { inProgress = !patientIdField.getText().trim().isEmpty(); }
            public void changedUpdate(DocumentEvent e) { inProgress = true; }
        });

        deptBox.addActionListener(e -> refreshDoctors());
        bookBtn.addActionListener(e -> bookAppointment());
        clearBtn.addActionListener(e -> {
            patientIdField.setText("");
            inProgress = false;
            statusLabel.setText("Form cleared.");
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (inProgress) {
                    int choice = JOptionPane.showConfirmDialog(
                            OutpatientDeskFrame.this,
                            "A booking is in progress. Discard and exit?",
                            "Confirm Exit",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (choice != JOptionPane.YES_OPTION) return;
                }
                dispose();
            }
        });

        refreshDoctors();
        pack();
        setLocationRelativeTo(null);
    }

    private void refreshDoctors() {
        String dept = (String) deptBox.getSelectedItem();
        doctorBox.removeAllItems();
        if (dept != null && doctorMap.containsKey(dept)) {
            for (String doc : doctorMap.get(dept)) {
                doctorBox.addItem(doc);
            }
        }
    }

    private void bookAppointment() {
        String pid = patientIdField.getText().trim();
        if (pid.isEmpty()) {
            statusLabel.setText("Error: Patient ID cannot be empty.");
            return;
        }
        String doc = (String) doctorBox.getSelectedItem();
        String slot = (String) slotBox.getSelectedItem();
        String key = doc + "@" + slot;
        if (bookedSlots.contains(key)) {
            statusLabel.setText("Error: " + doc + " is already booked at " + slot);
            return;
        }
        bookedSlots.add(key);
        inProgress = false;
        statusLabel.setText("Confirmed: " + pid + " with " + doc + " at " + slot);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OutpatientDeskFrame().setVisible(true));
    }
}