package com.example.demoassignment2;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import database.DatabaseHelper;
import database.ExpenseEntity;

public class DetailExpense extends AppCompatActivity {

    private EditText expenseNoteControl;
    private EditText expenseAmountControl;
    private EditText expenseDateControl;
    private Spinner expenseTypeControl;
    private DatabaseHelper databaseHelper;
    private ExpenseEntity expense;
    private Button editButton, resetButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_expense);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Kết nối các View
        expenseNoteControl = findViewById(R.id.editTextNote);
        expenseAmountControl = findViewById(R.id.editTextNumberDecimal);
        expenseDateControl = findViewById(R.id.editTextDate);
        expenseTypeControl = findViewById(R.id.spinner);
        editButton = findViewById(R.id.button);
        resetButton = findViewById(R.id.button2);


        // Nhận Intent và Bundle
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            // Nhận ExpenseEntity từ Bundle
            expense = (ExpenseEntity) bundle.getSerializable("expense");
            if (expense != null) {
                // Hiển thị thông tin từ expense vào các EditText
                expenseNoteControl.setText(String.valueOf(expense.getExpenseNote()));
                expenseAmountControl.setText(String.valueOf(expense.getAmount()));
                expenseDateControl.setText(expense.getExpenseDate());
                // Set the Spinner value
                String expenseType = expense.getExpenseType(); // Assuming `ExpenseEntity` has `getExpenseType()`
                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) expenseTypeControl.getAdapter();
                if (adapter != null) {
                    int spinnerPosition = adapter.getPosition(expenseType);
                    expenseTypeControl.setSelection(spinnerPosition);
                }

            }
        }

        expenseDateControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewExpenseFragment.DatePickerFragment datePicker = new NewExpenseFragment.DatePickerFragment();
                datePicker.editText = expenseDateControl;
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }

        });


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });

// Enable Edit Button functionality
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedNote = expenseNoteControl.getText().toString();
                String updatedAmountText = expenseAmountControl.getText().toString();
                String updatedDate = expenseDateControl.getText().toString();
                String updatedType = expenseTypeControl.getSelectedItem().toString();

                // Validate dữ liệu
                if (updatedNote.isEmpty()) {
                    expenseNoteControl.setError("Expense note is required");
                    expenseNoteControl.requestFocus();
                    return;
                }

                if (updatedType.equals("Select Type")) { // Assuming spinner has "Select Type" as a placeholder
                    Toast.makeText(getApplicationContext(), "Please select an expense type", Toast.LENGTH_SHORT).show();
                    expenseTypeControl.requestFocus();
                    return;
                }

                if (updatedAmountText.isEmpty()) {
                    expenseAmountControl.setError("Expense amount is required");
                    expenseAmountControl.requestFocus();
                    return;
                }

                double updatedAmount;
                try {
                    updatedAmount = Double.parseDouble(updatedAmountText);
                    if (updatedAmount <= 0) {
                        expenseAmountControl.setError("Amount must be greater than zero");
                        expenseAmountControl.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    expenseAmountControl.setError("Invalid amount format");
                    expenseAmountControl.requestFocus();
                    return;
                }

                if (updatedDate.isEmpty()) {
                    expenseDateControl.setError("Expense date is required");
                    expenseDateControl.requestFocus();
                    return;
                }

                // Update the ExpenseEntity
                expense.setExpenseNote(updatedNote);
                expense.setAmount(String.valueOf(updatedAmount));
                expense.setExpenseDate(updatedDate);
                expense.setExpenseType(updatedType);

                // Save to database or send back via Intent
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updated_expense", expense);
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(getApplication(), "Expense updated successfully", Toast.LENGTH_SHORT).show();
//                databaseHelper.updateExpense(expense);
                updateExpense();
                finish(); // Close the activity
            }
        });

    }


    private void updateExpense() {
        if (expense != null) {
            // Get updated values from the user input
            String updatedNote = expenseNoteControl.getText().toString();
            double updatedAmount = Double.parseDouble(expenseAmountControl.getText().toString());
            String updatedDate = expenseDateControl.getText().toString();
            String updatedType = expenseTypeControl.getSelectedItem().toString();

            // Update the ExpenseEntity object
            expense.setExpenseNote(updatedNote);
            expense.setAmount(String.valueOf(updatedAmount));
            expense.setExpenseDate(updatedDate);
            expense.setExpenseType(updatedType);

            // Call DatabaseHelper to update the record
            databaseHelper.updateExpense(expense);

            // Display success message and finish activity
            Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Optionally send a result back to the calling activity
            finish(); // Close the detail activity
        }
    }


    /**
     * Reset các trường nhập liệu về trạng thái ban đầu.
     */
    private void resetFields() {
        expenseNoteControl.setText("");
        expenseAmountControl.setText("");
        expenseDateControl.setText("");
        expenseTypeControl.setSelection(0);
    }



    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public EditText editText;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            editText.setText(dayOfMonth + "/" + month + "/" + year);
        }
    }

}

