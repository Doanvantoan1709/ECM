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

    /* Inner class defining table structure */
    public static class ExpenseEntry implements BaseColumns {
        public static final String TABLE_NAME = "expense";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_DATE = "expenseDate";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_TYPE = "type";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ExpenseEntry.TABLE_NAME + " (" +
                    ExpenseEntry._ID + " INTEGER PRIMARY KEY," +
                    ExpenseEntry.COLUMN_NAME_NOTE + " TEXT," +
                    ExpenseEntry.COLUMN_NAME_DATE + " TEXT," +
                    ExpenseEntry.COLUMN_NAME_AMOUNT + " REAL," +
                    ExpenseEntry.COLUMN_NAME_TYPE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ExpenseEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Example: Add new columns or alter schema here
            db.execSQL("ALTER TABLE " + ExpenseEntry.TABLE_NAME +
                    " ADD COLUMN new_column_name TEXT DEFAULT ''");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

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

    public List<ExpenseEntity> getAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.query(
                ExpenseEntry.TABLE_NAME,
                new String[]{ExpenseEntry._ID, ExpenseEntry.COLUMN_NAME_NOTE,
                        ExpenseEntry.COLUMN_NAME_AMOUNT, ExpenseEntry.COLUMN_NAME_TYPE,
                        ExpenseEntry.COLUMN_NAME_DATE},
                null, null, null, null, ExpenseEntry.COLUMN_NAME_DATE);

        List<ExpenseEntity> expenses = new ArrayList<>();
        if (results != null) {
            try {
                if (results.moveToFirst()) {
                    do {
                        ExpenseEntity expense = new ExpenseEntity();
                        expense.setId(results.getInt(0));
                        expense.setExpenseNote(results.getString(1));
                        expense.setAmount(String.valueOf(results.getDouble(2))); // Changed to double for REAL column
                        expense.setExpenseType(results.getString(3));
                        expense.setExpenseDate(results.getString(4));
                        expenses.add(expense);
                    } while (results.moveToNext());
                }
            } finally {
                results.close();
            }
        }
        db.close();
        return expenses;
    }

    public ExpenseEntity getExpenseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                ExpenseEntry.TABLE_NAME,
                null,
                ExpenseEntry._ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        ExpenseEntity expense = null;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    expense = new ExpenseEntity();
                    expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseEntry._ID)));
                    expense.setExpenseNote(cursor.getString(cursor.getColumnIndexOrThrow(ExpenseEntry.COLUMN_NAME_NOTE)));
                    expense.setAmount(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseEntry.COLUMN_NAME_AMOUNT))));
                    expense.setExpenseType(cursor.getString(cursor.getColumnIndexOrThrow(ExpenseEntry.COLUMN_NAME_TYPE)));
                    expense.setExpenseDate(cursor.getString(cursor.getColumnIndexOrThrow(ExpenseEntry.COLUMN_NAME_DATE)));
                }
            } finally {
                cursor.close();
            }
        }
        db.close();
        return expense;
    }

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

    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ExpenseEntry.TABLE_NAME, ExpenseEntry._ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
