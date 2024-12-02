package com.example.demoassignment2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import database.DatabaseHelper;
import database.ExpenseEntity;

public class NewExpene extends AppCompatActivity {
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public EditText editText;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(requireContext(),
                    this, year, month, day);
        }
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            editText.setText(dayOfMonth + "/" + month + "/" + year);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expene);

        EditText expenseDate = findViewById(R.id.editTextDate);
        expenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.editText = expenseDate;
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

        Button saveBtn = findViewById(R.id.button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText expenseNameControl = findViewById(R.id.editTextText);
                String expenseName = expenseNameControl.getText().toString();

                Spinner expenseTypeControl = findViewById(R.id.spinner);
                String expenseType  = expenseTypeControl.getSelectedItem().toString();

                EditText expenseAmountControl = findViewById(R.id.editTextNumberDecimal);
                String expenseAmount = expenseAmountControl.getText().toString();

                EditText expenseDateControl = findViewById(R.id.editTextDate);
                String expenseDate = expenseDateControl.getText().toString();

                ExpenseEntity expense = new ExpenseEntity();
                expense.setExpenseName(expenseName);
                expense.setExpenseType(expenseType);
                expense.setAmount(expenseAmount);
                expense.setExpenseDate(expenseDate);

                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                long id = dbHelper.insertExpense(expense);
                Toast.makeText(NewExpene.this,"Id: " + String.valueOf(id)
                        + " da insert",Toast.LENGTH_LONG).show();

            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.newExpense) {
            Toast.makeText(getApplicationContext(),"You are here already!",Toast.LENGTH_LONG).show();

        }
        else if (item.getItemId()== R.id.listExpense) {
            Intent intent = new Intent(NewExpene.this, AllExpenses.class);
            startActivity(intent);
        }
        return true;
    }

}