package database;

import java.io.Serializable;

public class Budget  implements Serializable {

    private  int id;

    private String expenseDate;
    private String expenseType;
    private int amount;


    public Budget( String expenseDate, String expenseType, int id,int amount) {

        this.expenseDate = expenseDate;
        this.expenseType = expenseType;
        this.id = id;
        this.amount=amount;

    }

    public Budget() {
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return

                "Date : "+     expenseDate  +"\n"
           +"Group : "  + expenseType +"\n"+
                "Amount : "+ amount
                ;
    }
}
