package database;

public class Budget {

    private  int id;
    private String budgetType;
    private String expenseDate;
    private String expenseType;


    public Budget(String budgetType, String expenseDate, String expenseType, int id) {
        this.budgetType = budgetType;
        this.expenseDate = expenseDate;
        this.expenseType = expenseType;
        this.id = id;
    }

    public Budget() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", budgetType='" + budgetType + '\'' +
                ", expenseDate='" + expenseDate + '\'' +
                ", expenseType='" + expenseType + '\'' +
                '}';
    }
}
