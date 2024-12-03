package com.example.demoassignment2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;
import database.DatabaseHelper;
import database.ExpenseEntity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewExpenseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewExpense.
     */
    // TODO: Rename and change types and number of parameters
    public static NewExpenseFragment newInstance(String param1, String param2) {
        NewExpenseFragment fragment = new NewExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_expense, container, false);

        EditText expenseDate = view.findViewById(R.id.editTextDate);
        expenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewExpenseFragment.DatePickerFragment datePicker = new NewExpenseFragment.DatePickerFragment();
                datePicker.editText = expenseDate;
                datePicker.show(getChildFragmentManager(), "datePicker");
            }

        });

        Button btnreset=view.findViewById(R.id.button2);
        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset Spinner (Đặt về giá trị mặc định)
                Spinner expenseTypeControl = view.findViewById(R.id.spinner);
                expenseTypeControl.setSelection(0); // Đặt lại Spinner về giá trị đầu tiên (mặc định)

                // Reset EditText cho số tiền
                EditText expenseAmountControl = view.findViewById(R.id.editTextNumberDecimal);
                expenseAmountControl.setText(""); // Xóa giá trị trong EditText

                // Reset EditText cho ngày
                EditText expenseDateControl = view.findViewById(R.id.editTextDate);
                expenseDateControl.setText(""); // Xóa giá trị trong EditText

                EditText expenseNoteControl = view.findViewById(R.id.editTextNote);
                expenseNoteControl.setText("");
            }
        });

        Button saveBtn = view.findViewById(R.id.button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ẩn bàn phím
                View currentView = requireActivity().getCurrentFocus();
                if (currentView != null) {
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
                }
                EditText expenseNoteControl = view.findViewById(R.id.editTextNote);
                String expenseNote = expenseNoteControl.getText().toString().trim();

                Spinner expenseTypeControl = view.findViewById(R.id.spinner);
                String expenseType = expenseTypeControl.getSelectedItem().toString();

                EditText expenseAmountControl = view.findViewById(R.id.editTextNumberDecimal);
                String expenseAmount = expenseAmountControl.getText().toString().trim();

                EditText expenseDateControl = view.findViewById(R.id.editTextDate);
                String expenseDate = expenseDateControl.getText().toString().trim();

                // Validate dữ liệu
                if (expenseNote.isEmpty()) {
                    expenseNoteControl.setError("Expense note is required");
                    expenseNoteControl.requestFocus();
                    return;
                }

                if (expenseType.equals("Select Type")) { // Giả sử spinner có "Select Type" làm placeholder
                    Toast.makeText(getContext(), "Please select an expense type", Toast.LENGTH_SHORT).show();
                    expenseTypeControl.requestFocus();
                    return;
                }

                if (expenseAmount.isEmpty()) {
                    expenseAmountControl.setError("Expense amount is required");
                    expenseAmountControl.requestFocus();
                    return;
                }

                try {
                    double amount = Double.parseDouble(expenseAmount);
                    if (amount <= 0) {
                        expenseAmountControl.setError("Amount must be greater than zero");
                        expenseAmountControl.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    expenseAmountControl.setError("Invalid amount format");
                    expenseAmountControl.requestFocus();
                    return;
                }

                if (expenseDate.isEmpty()) {
                    expenseDateControl.setError("Expense date is required");
                    expenseDateControl.requestFocus();
                    return;
                }

                // Nếu tất cả dữ liệu hợp lệ, lưu vào database
                ExpenseEntity expense = new ExpenseEntity();
                expense.setExpenseNote(expenseNote);
                expense.setExpenseType(expenseType);
                expense.setAmount(expenseAmount);
                expense.setExpenseDate(expenseDate);

                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                long id = dbHelper.insertExpense(expense);

                // Reset Spinner (Đặt về giá trị mặc định)
                expenseTypeControl.setSelection(0); // Đặt lại Spinner về giá trị đầu tiên (mặc định)

                // Reset EditText cho số tiền
                expenseAmountControl.setText(""); // Xóa giá trị trong EditText

                // Reset EditText cho ngày
                expenseDateControl.setText(""); // Xóa giá trị trong EditText

                // Reset EditText cho ghi chú
                expenseNoteControl.setText("");
                Toast.makeText(getContext(), "Add Expense successfull !", Toast.LENGTH_LONG).show();
            }
        });

        return view;
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
            editText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }
    }


}