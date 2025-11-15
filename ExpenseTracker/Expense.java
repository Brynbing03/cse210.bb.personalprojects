// Expense.java
public class Expense {
    private String name;
    private String category;
    private double amount;

    public Expense(String name, String category, double amount) {
        this.name = name;
        this.category = category;
        this.amount = amount;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return String.format("%s (%s): $%.2f", name, category, amount);
    }

    // Serialization to a simple CSV line for file saving
    public String toCSV() {
        return String.format("%s,%s,%.2f", escape(name), escape(category), amount);
    }

    // Helper to escape commas (simple)
    private String escape(String s) {
        return s.replace(",", "\\,");
    }

    // Parse from CSV line (simple)
    public static Expense fromCSV(String line) {
        // split on commas not preceded by backslash
        String[] parts = line.split("(?<!\\\\),");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replace("\\,", ",");
        }
        if (parts.length < 3) return null;
        String name = parts[0];
        String category = parts[1];
        double amount = Double.parseDouble(parts[2]);
        return new Expense(name, category, amount);
    }
}
