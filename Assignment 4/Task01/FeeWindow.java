package Task01;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

class FeeService {
    private final List<String> paymentRecords = new ArrayList<>();

    public String processPayment(String regNo, String amountText) throws Exception {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Register number is required.");
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Amount must be a valid number.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        Thread.sleep(500);
        String record = regNo.trim() + " -> Rs " + String.format("%.2f", amount);
        paymentRecords.add(record);
        return "Receipt: Paid Rs " + String.format("%.2f", amount) + " for " + regNo.trim();
    }

    public String generateReceipt(String regNo, String amountText) {
        if (regNo.trim().isEmpty() || amountText.trim().isEmpty()) {
            return "Enter register number and amount to preview receipt.";
        }
        return "FEE RECEIPT | RegNo: " + regNo.trim() + " | Amount: Rs " + amountText.trim();
    }
}

public class FeeWindow extends Frame {
    private final TextField regNo = new TextField(15);
    private final TextField amount = new TextField(15);
    private final Label status = new Label("Ready");
    private final FeeService service = new FeeService();

    public FeeWindow() {
        super("Student Fee Payment");
        setLayout(new BorderLayout(8, 8));

        Panel form = new Panel(new GridLayout(2, 2, 6, 6));
        form.add(new Label("Register No:"));
        form.add(regNo);
        form.add(new Label("Amount (Rs):"));
        form.add(amount);

        Button pay = new Button("Pay");
        Button clear = new Button("Clear");
        Button receipt = new Button("Receipt");

        pay.addActionListener(e -> handlePay(pay));
        clear.addActionListener(e -> {
            regNo.setText("");
            amount.setText("");
            status.setText("Cleared.");
        });
        receipt.addActionListener(e ->
            status.setText(service.generateReceipt(regNo.getText(), amount.getText()))
        );

        Panel buttons = new Panel();
        buttons.add(pay);
        buttons.add(clear);
        buttons.add(receipt);

        add(form, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setSize(420, 180);
    }

    private void handlePay(Button payBtn) {
        String r = regNo.getText();
        String a = amount.getText();
        payBtn.setEnabled(false);
        status.setText("Processing payment...");

        new Thread(() -> {
            try {
                String msg = service.processPayment(r, a);
                EventQueue.invokeLater(() -> {
                    status.setText(msg);
                    payBtn.setEnabled(true);
                });
            } catch (Exception ex) {
                EventQueue.invokeLater(() -> {
                    status.setText("Error: " + ex.getMessage());
                    payBtn.setEnabled(true);
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        new FeeWindow().setVisible(true);
    }
}