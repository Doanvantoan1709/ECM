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

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "ExpenseDB.db";

    // Expense table schema
    public static class ExpenseEntry implements BaseColumns {
        public static final String TABLE_NAME = "expense";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_DATE = "expenseDate";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_TYPE = "type";
    }

    // Budget table schema
    public static class BudgetEntry implements BaseColumns {
        public static final String TABLE_NAME = "budget";
        public static final String COLUMN_NAME_EXPENSEDATE = "expenseDate";
        public static final String COLUMN_NAME_EXPENSETYPE = "expenseType";
        public static final String COLUMN_NAME_AMOUNT = "amount";
    }

    // SQL for creating and deleting tables
    private static final String SQL_CREATE_EXPENSE_ENTRIES =
            "CREATE TABLE " + ExpenseEntry.TABLE_NAME + " (" +
                    ExpenseEntry._ID + " INTEGER PRIMARY KEY," +
                    ExpenseEntry.COLUMN_NAME_NOTE + " TEXT," +
                    ExpenseEntry.COLUMN_NAME_DATE + " TEXT," +
                    ExpenseEntry.COLUMN_NAME_AMOUNT + " REAL," +
                    ExpenseEntry.COLUMN_NAME_TYPE + " TEXT)";

    private static final String SQL_DELETE_EXPENSE_ENTRIES =
            "DROP TABLE IF EXISTS " + ExpenseEntry.TABLE_NAME;

    private static final String SQL_CREATE_BUDGET_ENTRIES =
            "CREATE TABLE " + BudgetEntry.TABLE_NAME + " (" +
                    BudgetEntry._ID + " INTEGER PRIMARY KEY," +
                    BudgetEntry.COLUMN_NAME_EXPENSEDATE + " TEXT," +
                    BudgetEntry.COLUMN_NAME_EXPENSETYPE + " TEXT," +
                    BudgetEntry.COLUMN_NAME_AMOUNT + " INTEGER)";

    private static final String SQL_DELETE_BUDGET_ENTRIES =
            "DROP TABLE IF EXISTS " + BudgetEntry.TABLE_NAME;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EXPENSE_ENTRIES);
        db.execSQL(SQL_CREATE_BUDGET_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_EXPENSE_ENTRIES);
        db.execSQL(SQL_DELETE_BUDGET_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Insert a new expense
    public long insertExpense(ExpenseEntity expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ExpenseEntry.COLUMN_NAME_NOTE, expense.getExpenseNote());
        values.put(ExpenseEntry.COLUMN_NAME_AMOUNT, expense.getAmount());
        values.put(ExpenseEntry.COLUMN_NAME_TYPE, expense.getExpenseType());
        values.put(ExpenseEntry.COLUMN_NAME_DATE, expense.getExpenseDate());
        long result = db.insertOrThrow(ExpenseEntry.TABLE_NAME, null, values);
        db.close();
        return result;
    }

    // Get all expenses
    public List<ExpenseEntity> getAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ExpenseEntity> expenses = new ArrayList<>();
        Cursor cursor = db.query(
                ExpenseEntry.TABLE_NAME,
                new String[]{ExpenseEntry._ID, ExpenseEntry.COLUMN_NAME_NOTE,
                        ExpenseEntry.COLUMN_NAME_AMOUNT, ExpenseEntry.COLUMN_NAME_TYPE,
                        ExpenseEntry.COLUMN_NAME_DATE},
                null, null, null, null, ExpenseEntry.COLUMN_NAME_DATE);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        ExpenseEntity expense = new ExpenseEntity();
                        expense.setId(cursor.getInt(0));
                        expense.setExpenseNote(cursor.getString(1));
                        expense.setAmount(String.valueOf(cursor.getDouble(2)));
                        expense.setExpenseType(cursor.getString(3));
                        expense.setExpenseDate(cursor.getString(4));
                        expenses.add(expense);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        db.close();
        return expenses;
    }

    // Update an expense
    public void updateExpense(ExpenseEntity expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ExpenseEntry.COLUMN_NAME_NOTE, expense.getExpenseNote());
        values.put(ExpenseEntry.COLUMN_NAME_AMOUNT, expense.getAmount());
        values.put(ExpenseEntry.COLUMN_NAME_TYPE, expense.getExpenseType());
        values.put(ExpenseEntry.COLUMN_NAME_DATE, expense.getExpenseDate());
        db.update(ExpenseEntry.TABLE_NAME, values,
                ExpenseEntry._ID + "=?", new String[]{String.valueOf(expense.getId())});
        db.close();
    }

    // Delete an expense
    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ExpenseEntry.TABLE_NAME, ExpenseEntry._ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Insert a new budget
    public long insertBudget(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BudgetEntry.COLUMN_NAME_EXPENSEDATE, budget.getExpenseDate());
        values.put(BudgetEntry.COLUMN_NAME_EXPENSETYPE, budget.getExpenseType());
        values.put(BudgetEntry.COLUMN_NAME_AMOUNT, budget.getAmount());
        long result = db.insertOrThrow(BudgetEntry.TABLE_NAME, null, values);
        db.close();
        return result;
    }

    // Get all budgets
    public List<Budget> getAllBudgets() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Budget> budgets = new ArrayList<>();
        Cursor cursor = db.query(
                BudgetEntry.TABLE_NAME,
                new String[]{BudgetEntry._ID, BudgetEntry.COLUMN_NAME_EXPENSEDATE,
                        BudgetEntry.COLUMN_NAME_EXPENSETYPE, BudgetEntry.COLUMN_NAME_AMOUNT},
                null, null, null, null, BudgetEntry.COLUMN_NAME_EXPENSEDATE);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Budget budget = new Budget();
                        budget.setId(cursor.getInt(0));
                        budget.setExpenseDate(cursor.getString(1));
                        budget.setExpenseType(cursor.getString(2));
                        budget.setAmount(cursor.getInt(3));
                        budgets.add(budget);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        db.close();
        return budgets;
    }

    // Update a budget
    public void updateBudget(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BudgetEntry.COLUMN_NAME_EXPENSEDATE, budget.getExpenseDate());
        values.put(BudgetEntry.COLUMN_NAME_EXPENSETYPE, budget.getExpenseType());
        values.put(BudgetEntry.COLUMN_NAME_AMOUNT, budget.getAmount());
        db.update(BudgetEntry.TABLE_NAME, values,
                BudgetEntry._ID + "=?", new String[]{String.valueOf(budget.getId())});
        db.close();
    }

    // Delete a budget
    public void deleteBudget(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BudgetEntry.TABLE_NAME, BudgetEntry._ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
