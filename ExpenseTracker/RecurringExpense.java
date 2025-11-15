// RecurringExpense.java
public class RecurringExpense extends Expense {
    private String frequency; // e.g., "monthly", "weekly"

    public RecurringExpense(String name, String category, double amount, String frequency) {
        super(name, category, amount);
        this.frequency = frequency;
    }

    public String getFrequency() { return frequency; }

    @Override
    public String toString() {
        return super.toString() + " [Recurring: " + frequency + "]";
    }

    @Override
    public String toCSV() {
        // mark recurring with an R prefix
        return "R," + super.toCSV() + "," + frequency;
    }

    public static RecurringExpense fromCSV(String line) {
        // expects format: R,name,category,amount,frequency
        String[] parts = line.split("(?<!\\\\),");
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].replace("\\,", ",");
        if (parts.length < 5 || !parts[0].equals("R")) return null;
        String name = parts[1];
        String category = parts[2];
        double amount = Double.parseDouble(parts[3]);
        String frequency = parts[4];
        return new RecurringExpense(name, category, amount, frequency);
    }
}
