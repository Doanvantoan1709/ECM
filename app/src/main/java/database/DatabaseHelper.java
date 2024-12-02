package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ExpenseDB.db";

    /* Inner class that defines the table contents */
    public static class ExpenseEntry implements BaseColumns {
        public static final String TABLE_NAME = "expense";
        public static final String COLUMN_NAME_EXPENSENAME = "name";
        public static final String COLUMN_NAME_DATE = "expenseDate";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_TYPE = "type";
    }

    ///budget
    public static class BudgetEntry implements BaseColumns {
        public static final String TABLE_NAME = "budget";

        public static final String COLUMN_NAME_EXPENSEDATE = "expenseDate";
        public static final String COLUMN_NAME_EXPENSETYPE = "expenseType";
        public static final String COLUMN_NAME_AMOUNT = "amount";
    }
    private static final String SQL_CREATE_BUDGET_ENTRIES =
            "CREATE TABLE " + BudgetEntry.TABLE_NAME + " (" +
                    BudgetEntry._ID + " INTEGER PRIMARY KEY," +

                    BudgetEntry.COLUMN_NAME_EXPENSEDATE + " TEXT," +
                    BudgetEntry.COLUMN_NAME_EXPENSETYPE + " TEXT," +
                    BudgetEntry.COLUMN_NAME_AMOUNT + " INTEGER)";

    private static final String SQL_DELETE_BUDGET_ENTRIES =
            "DROP TABLE IF EXISTS " + BudgetEntry.TABLE_NAME;
    //end

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES); // Tạo bảng expense
        db.execSQL(SQL_CREATE_BUDGET_ENTRIES); // Tạo bảng budget
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES); // Xóa bảng expense
        db.execSQL(SQL_DELETE_BUDGET_ENTRIES); // Xóa bảng budget
        onCreate(db); // Tạo lại các bảng
    }


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ExpenseEntry.TABLE_NAME + " (" +
                    ExpenseEntry._ID + " INTEGER PRIMARY KEY," +
                    ExpenseEntry.COLUMN_NAME_EXPENSENAME + " TEXT," +
                    ExpenseEntry.COLUMN_NAME_DATE + " TEXT," +
                    ExpenseEntry.COLUMN_NAME_AMOUNT + " TEXT," +
                    ExpenseEntry.COLUMN_NAME_TYPE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ExpenseEntry.TABLE_NAME;

    private SQLiteDatabase database;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertExpense(ExpenseEntity expense){

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ExpenseEntry.COLUMN_NAME_EXPENSENAME, expense.getExpenseName());
        values.put(ExpenseEntry.COLUMN_NAME_AMOUNT, expense.getAmount());
        values.put(ExpenseEntry.COLUMN_NAME_TYPE, expense.getExpenseType());
        values.put(ExpenseEntry.COLUMN_NAME_DATE, expense.getExpenseDate());

        // Insert the new row, returning the primary key value of the new row
        return database.insertOrThrow(ExpenseEntry.TABLE_NAME, null, values);
    }

    //delte expense

    //delete budget
    public void deleteExpense(int id) {
        database.delete(ExpenseEntry.TABLE_NAME, ExpenseEntry._ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public List<ExpenseEntity> getAllExpenses() {
        Cursor results = database.query(ExpenseEntry.TABLE_NAME, new String[] {ExpenseEntry._ID,ExpenseEntry.COLUMN_NAME_EXPENSENAME,ExpenseEntry.COLUMN_NAME_AMOUNT,ExpenseEntry.COLUMN_NAME_TYPE, ExpenseEntry.COLUMN_NAME_DATE},
                null, null, null, null, ExpenseEntry.COLUMN_NAME_DATE);

        results.moveToFirst();
        List<ExpenseEntity> expenses = new ArrayList<>();
        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String name = results.getString(1);
            String amount = results.getString(2);
            String type = results.getString(3);
            String date = results.getString(4);
            ExpenseEntity expense = new ExpenseEntity();
            expense.setId(id);
            expense.setExpenseName(name);
            expense.setExpenseType(type);
            expense.setAmount(amount);
            expense.setExpenseDate(date);
            expenses.add(expense);
            results.moveToNext();
        }

        return expenses;

    }



    //ínert budget
    public long insertBudget(Budget budget) {
        ContentValues values = new ContentValues();
        values.put(BudgetEntry.COLUMN_NAME_EXPENSEDATE, budget.getExpenseDate());
        values.put(BudgetEntry.COLUMN_NAME_EXPENSETYPE, budget.getExpenseType());
        values.put(BudgetEntry.COLUMN_NAME_AMOUNT, budget.getAmount());
        return database.insertOrThrow(BudgetEntry.TABLE_NAME, null, values);
    }

    //get all Budget
    // Get all budgets
    public List<Budget> getAllBudgets() {
        Cursor results = database.query(BudgetEntry.TABLE_NAME,
                new String[]{
                        BudgetEntry._ID,
                        BudgetEntry.COLUMN_NAME_EXPENSEDATE,
                        BudgetEntry.COLUMN_NAME_EXPENSETYPE,
                        BudgetEntry.COLUMN_NAME_AMOUNT // Thêm cột amount
                },
                null, null, null, null, BudgetEntry.COLUMN_NAME_EXPENSEDATE);

        List<Budget> budgets = new ArrayList<>();
        results.moveToFirst();
        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String expenseDate = results.getString(1);
            String expenseType = results.getString(2);
            int amount = results.getInt(3); // Đọc giá trị amount

            Budget budget = new Budget();
            budget.setId(id);
            budget.setExpenseDate(expenseDate);
            budget.setExpenseType(expenseType);
            budget.setAmount(amount); // Gán amount vào đối tượng

            budgets.add(budget);
            results.moveToNext();
        }

        return budgets;
    }


    //delete budget
    public void deleteBudget(int id) {
        database.delete(BudgetEntry.TABLE_NAME, BudgetEntry._ID + "=?",
                new String[]{String.valueOf(id)});
    }


    //update budget
    public int updateBudget(Budget budget) {
        ContentValues values = new ContentValues();
        values.put(BudgetEntry.COLUMN_NAME_EXPENSEDATE, budget.getExpenseDate());
        values.put(BudgetEntry.COLUMN_NAME_EXPENSETYPE, budget.getExpenseType());
        values.put(BudgetEntry.COLUMN_NAME_AMOUNT, budget.getAmount());

        return database.update(BudgetEntry.TABLE_NAME, values, BudgetEntry._ID + "=?",
                new String[]{String.valueOf(budget.getId())});
    }


}
