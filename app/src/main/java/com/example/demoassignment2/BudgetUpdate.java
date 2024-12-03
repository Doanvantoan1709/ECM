package com.example.demoassignment2;

import static java.security.AccessController.getContext;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import database.Budget;
import database.DatabaseHelper;
import database.ExpenseEntity;

public class BudgetUpdate extends AppCompatActivity {
    private EditText expenseNoteControl;
    private EditText expenseAmountControl;
    private EditText expenseDateControl;
    private Spinner expenseTypeControl;
    private DatabaseHelper databaseHelper;
    private Budget budget;
    private Button editButton, resetButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        // Kết nối các View

        expenseAmountControl = findViewById(R.id.editTextNumberDecimal);
        expenseDateControl = findViewById(R.id.editTextDate);
        expenseTypeControl = findViewById(R.id.spinner);
        editButton = findViewById(R.id.btnSave);
        resetButton = findViewById(R.id.button2);
        // Nhận Intent và Bundle
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            // Nhận ExpenseEntity từ Bundle
            budget = (Budget) bundle.getSerializable("budget");
            if (budget != null) {
                // Hiển thị thông tin từ expense vào các EditText

                expenseAmountControl.setText(String.valueOf(budget.getAmount()));
                expenseDateControl.setText(budget.getExpenseDate());
                // Set the Spinner value
                String expenseType = budget.getExpenseType(); // Assuming `ExpenseEntity` has `getExpenseType()`
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
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String updatedAmountText = expenseAmountControl.getText().toString();
//                String updatedDate = expenseDateControl.getText().toString();
//                String updatedType = expenseTypeControl.getSelectedItem().toString();
//
//
//
//
//                // Save to database or send back via Intent
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("update_budget", budget);
//                setResult(RESULT_OK, resultIntent);
//                Toast.makeText(getApplication(), "Expense updated successfully", Toast.LENGTH_SHORT).show();
//                   databaseHelper.updateBudget(budget);
//                finish(); // Close the activity
//            }
//        });
        editButton=findViewById(R.id.btnUpdate);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin đã chỉnh sửa từ các trường nhập liệu
                String updatedExpenseAmount = expenseAmountControl.getText().toString();
                String updatedExpenseDate = expenseDateControl.getText().toString();
                String updatedExpenseType = expenseTypeControl.getSelectedItem().toString();

                // Kiểm tra tính hợp lệ
                boolean isValid = true;
                StringBuilder errorMessage = new StringBuilder();

                if (updatedExpenseAmount.isEmpty()) {
                    isValid = false;
                    errorMessage.append("Please enter amount .\n");
                } else {
                    try {
                        int amount = Integer.parseInt(updatedExpenseAmount);
                        if (amount <= 0) {
                            isValid = false;
                            errorMessage.append("Please enter amount > 0 .\n");
                        }
                    } catch (NumberFormatException e) {
                        isValid = false;
                        errorMessage.append("Please Etner amount correct.\n");
                    }
                }

                if (updatedExpenseDate.isEmpty()) {
                    isValid = false;
                    errorMessage.append("Please Enter Date.\n");
                }

                if (updatedExpenseType.equals("Please Enter Budget Type ")) { // Thay bằng giá trị mặc định của Spinner nếu cần
                    isValid = false;
                    errorMessage.append("Vui lòng chọn loại chi phí .\n");
                }

                if (!isValid) {
                    // Hiển thị thông báo lỗi
                    Toast.makeText(getApplicationContext(), errorMessage.toString().trim(), Toast.LENGTH_LONG).show();
                    return;
                }

                // Nếu hợp lệ, cập nhật đối tượng `Budget`
                try {
                    budget.setAmount(Integer.parseInt(updatedExpenseAmount));
                    budget.setExpenseDate(updatedExpenseDate);
                    budget.setExpenseType(updatedExpenseType);

                    // Cập nhật vào cơ sở dữ liệu
                    int rowsAffected = databaseHelper.updateBudget(budget);
                    if (rowsAffected > 0) {
                        Toast.makeText(getApplicationContext(), "Update Succecsfull", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình trước đó
                    } else {
                        Toast.makeText(getApplicationContext(), "Update Failed ", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error when enter data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });




        LinearLayout back = findViewById(R.id.backIcon);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(BudgetUpdate.this, MainActivity.class);
                intent.putExtra("BudgetList", "BudgetList"); // Truyền thông tin về fragment
                startActivity(intent);
                finish();

            }
        });


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