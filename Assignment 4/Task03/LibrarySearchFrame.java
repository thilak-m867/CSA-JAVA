package Task03;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class LibrarySearchFrame extends JFrame {
    private final JTextField searchField = new JTextField(20);
    private final JComboBox<String> categoryBox =
            new JComboBox<>(new String[]{"All", "Computer Science", "Electronics", "Mathematics"});
    private final JTextArea resultsArea = new JTextArea(12, 45);
    private final JLabel statusLabel = new JLabel("Ready. Enter keywords and click Search.");

    public LibrarySearchFrame() {
        super("Campus Library Search");
        setLayout(new BorderLayout(6, 6));

        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        JLabel title = new JLabel("CAMPUS LIBRARY SEARCH", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        northPanel.add(title);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton searchBtn = new JButton("Search");
        searchRow.add(new JLabel("Keyword:"));
        searchRow.add(searchField);
        searchRow.add(new JLabel("Category:"));
        searchRow.add(categoryBox);
        searchRow.add(searchBtn);
        northPanel.add(searchRow);

        resultsArea.setEditable(false);
        resultsArea.setText("1. [CS101] Introduction to Algorithms - Cormen (Available)\n"
                          + "2. [CS204] Effective Java - Joshua Bloch (Available)\n"
                          + "3. [CS309] Operating System Concepts - Silberschatz (Issued)\n");
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        JButton prevBtn = new JButton("Prev");
        JButton nextBtn = new JButton("Next");
        JButton reserveBtn = new JButton("Reserve");
        navRow.add(prevBtn);
        navRow.add(nextBtn);
        navRow.add(reserveBtn);

        southPanel.add(navRow, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> statusLabel.setText(
                "Searched '" + searchField.getText() + "' in " + categoryBox.getSelectedItem()));
        prevBtn.addActionListener(e -> statusLabel.setText("Showing previous page of results."));
        nextBtn.addActionListener(e -> statusLabel.setText("Showing next page of results."));
        reserveBtn.addActionListener(e -> statusLabel.setText("Selected book reserved."));

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchFrame().setVisible(true));
    }
}
