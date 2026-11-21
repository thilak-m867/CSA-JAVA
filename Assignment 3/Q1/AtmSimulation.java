package Q1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String msg) {
        super(msg);
    }
}

public class AtmSimulation {
    public static double withdraw(String cardFile, String amountInput, double balance)
            throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(cardFile));
            String accNo = reader.readLine();
            if (accNo == null || accNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Empty card data");
            }
            double amount = Double.parseDouble(amountInput.trim());
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            if (amount > balance) {
                throw new InsufficientFundsException("Requested Rs " + amount + " exceeds balance Rs " + balance);
            }
            balance -= amount;
            System.out.println("Dispensed Rs " + amount + " for A/C " + accNo + " | New Balance: " + balance);
            return balance;
        } catch (FileNotFoundException | IllegalArgumentException e) {
            // Unrelated exceptions: FileNotFoundException (checked) & IllegalArgumentException (unchecked, covers NumberFormatException)
            System.out.println("[Multi-Catch Input/Card Error] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return balance;
        } catch (InsufficientFundsException e) {
            System.out.println("[Caught InsufficientFundsException] " + e.getMessage());
            return balance;
        } finally {
            if (reader != null) {
                reader.close();
            }
            System.out.println("[finally] Card ejected & reader session closed.");
        }
    }

    public static void main(String[] args) throws IOException {
        Path card = Files.createTempFile("card", ".txt");
        Files.writeString(card, "ACC-9001\n");

        withdraw(card.toString(), "2000", 10000);       // Test 1: Valid
        withdraw("missing_card.txt", "1000", 10000);    // Test 2: FileNotFoundException
        withdraw(card.toString(), "abc", 10000);        // Test 3: NumberFormatException (subclass of IllegalArgumentException)
        withdraw(card.toString(), "-500", 10000);       // Test 4: IllegalArgumentException
        withdraw(card.toString(), "25000", 10000);      // Test 5: InsufficientFundsException

        Files.deleteIfExists(card);
    }
}