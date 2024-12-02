package database;

import java.io.Serializable;

public class ExpenseEntity implements Serializable {
    private int id;
    private String expenseNote;
    private String expenseDate;
    private String expenseType;

    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public ExpenseEntity(int id, String expenseNote, String expenseDate, String expenseType, String amount) {
        this.id = id;
        this.expenseNote = expenseNote;
        this.expenseDate = expenseDate;
        this.expenseType = expenseType;
        this.amount = amount;
    }

    public ExpenseEntity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpenseNote() {
        return expenseNote;
    }

    public void setExpenseNote(String expenseNote) {this.expenseNote = expenseNote;}

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String toString() {
        return "Group: " +  expenseType + "\n" + "Note: " + expenseNote + "\n" + "Amount: " + amount + " VNĐ" + "\n" + "Calendar: " + expenseDate;
    }
}
