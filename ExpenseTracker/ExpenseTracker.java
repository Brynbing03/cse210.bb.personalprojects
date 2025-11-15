// ExpenseTracker.java
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExpenseTracker {
    private static ArrayList<Expense> expenses = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "expenses.txt";

    public static void main(String[] args) {
        loadFromFile(); // try to load saved expenses
        System.out.println("Welcome to the Expense Tracker!");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt();

            switch (choice) {
                case 1: addExpense(); break;
                case 2: viewExpenses(); break;
                case 3: calculateTotal(); break;
                case 4: filterByCategory(); break;
                case 5: saveToFile(); break;
                case 6: loadFromFile(); break;
                case 7: running = false; System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. Calculate Total");
        System.out.println("4. Filter by Category");
        System.out.println("5. Save Expenses to File");
        System.out.println("6. Load Expenses from File");
        System.out.println("7. Quit");
        System.out.print("Choose an option: ");
    }

    private static int readInt() {
        while (true) {
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            String line = scanner.nextLine();
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid amount (e.g., 12.50): ");
            }
        }
    }

    private static void addExpense() {
        System.out.print("Enter expense name: ");
        String name = scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = readDouble();

        System.out.print("Is this recurring? (y/n): ");
        String r = scanner.nextLine().trim().toLowerCase();
        if (r.equals("y") || r.equals("yes")) {
            System.out.print("Enter frequency (e.g., monthly): ");
            String frequency = scanner.nextLine();
            RecurringExpense re = new RecurringExpense(name, category, amount, frequency);
            expenses.add(re);
        } else {
            Expense e = new Expense(name, category, amount);
            expenses.add(e);
        }
        System.out.println("Expense added!");
    }

    private static void viewExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }
        System.out.println("\nYour Expenses:");
        for (Expense e : expenses) {
            System.out.println(e);
        }
    }

    private static void calculateTotal() {
        double total = 0.0;
        for (Expense e : expenses) total += e.getAmount();
        System.out.println("Total spending: $" + String.format("%.2f", total));
    }

    private static void filterByCategory() {
        System.out.print("Enter category to filter: ");
        String cat = scanner.nextLine().trim();
        boolean found = false;
        for (Expense e : expenses) {
            if (e.getCategory().equalsIgnoreCase(cat)) {
                System.out.println(e);
                found = true;
            }
        }
        if (!found) System.out.println("No expenses found for category: " + cat);
    }

    // File I/O: save
    private static void saveToFile() {
        try (FileWriter fw = new FileWriter(DATA_FILE)) {
            for (Expense e : expenses) {
                // If it's RecurringExpense, it overrides toCSV to include R prefix
                if (e instanceof RecurringExpense) {
                    fw.write(((RecurringExpense) e).toCSV() + System.lineSeparator());
                } else {
                    fw.write("E," + e.toCSV() + System.lineSeparator());
                }
            }
            System.out.println("Saved to " + DATA_FILE);
        } catch (IOException ex) {
            System.out.println("Error saving: " + ex.getMessage());
        }
    }

    // File I/O: load
    private static void loadFromFile() {
        File f = new File(DATA_FILE);
        if (!f.exists()) {
            return; // nothing to load
        }
        ArrayList<Expense> loaded = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(f)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.startsWith("R,")) {
                    RecurringExpense re = RecurringExpense.fromCSV(line);
                    if (re != null) loaded.add(re);
                } else if (line.startsWith("E,")) {
                    Expense e = Expense.fromCSV(line.substring(2));
                    if (e != null) loaded.add(e);
                }
            }
            if (!loaded.isEmpty()) {
                expenses = loaded;
                System.out.println("Loaded " + expenses.size() + " expenses from " + DATA_FILE);
            }
        } catch (IOException ex) {
            System.out.println("Error loading file: " + ex.getMessage());
        }
    }
}
