package com.example.demoassignment2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import database.Budget;
import database.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewBudgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewBudgetFragment extends Fragment {


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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button btnSave;

    public NewBudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewBudgetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewBudgetFragment newInstance(String param1, String param2) {
        NewBudgetFragment fragment = new NewBudgetFragment();
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
        View view= inflater.inflate(R.layout.fragment_new_budget, container, false);

        EditText budgetdate = view.findViewById(R.id.editTextDate);
        budgetdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewExpene.DatePickerFragment datePicker = new NewExpene.DatePickerFragment();
                datePicker.editText = budgetdate;
                datePicker.show(requireActivity().getSupportFragmentManager(), "datePicker");
            }
        });


        btnSave=view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị từ các điều khiển
                Spinner expenseTypeControl = view.findViewById(R.id.spinner);
                String expenseType = expenseTypeControl.getSelectedItem().toString();

                EditText expenseAmountControl = view.findViewById(R.id.editTextNumberDecimal);
                String expenseAmount = expenseAmountControl.getText().toString();

                EditText expenseDateControl = view.findViewById(R.id.editTextDate);
                String expenseDate = expenseDateControl.getText().toString();

                // Biến cờ để kiểm tra tính hợp lệ
                boolean isValid = true;

                // Kiểm tra Spinner (không chọn giá trị mặc định)
                if (expenseType.equals("Chọn loại chi phí")) { // Thay "Chọn loại chi phí" bằng giá trị mặc định của bạn
                    isValid = false;
                    Toast.makeText(getContext(), "Vui lòng chọn loại chi phí.", Toast.LENGTH_SHORT).show();
                }

                // Kiểm tra số tiền
                int amount = 0;
                if (expenseAmount.isEmpty()) {
                    isValid = false;
                    expenseAmountControl.setError("Vui lòng nhập số tiền.");
                } else {
                    try {
                        amount = Integer.parseInt(expenseAmount);
                        if (amount <= 0) {
                            isValid = false;
                            expenseAmountControl.setError("Số tiền phải lớn hơn 0.");
                        }
                    } catch (NumberFormatException e) {
                        isValid = false;
                        expenseAmountControl.setError("Vui lòng nhập một số hợp lệ.");
                    }
                }

                // Kiểm tra ngày
                if (expenseDate.isEmpty()) {
                    isValid = false;
                    Toast.makeText(getContext(), "Please enter date", Toast.LENGTH_SHORT).show();
                }

                // Xử lý nếu hợp lệ
                if (isValid) {
                    // Tạo đối tượng Budget
                    Budget budget = new Budget();
                    budget.setExpenseType(expenseType);
                    budget.setExpenseDate(expenseDate);
                    budget.setAmount(amount);

                    // Lưu vào cơ sở dữ liệu
                    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                    long id = dbHelper.insertBudget(budget);

                    // Thông báo kết quả
                    Toast.makeText(getActivity(), "Id: " + id + " đã được thêm!", Toast.LENGTH_LONG).show();

                    // Reset form sau khi lưu
                    expenseAmountControl.setText("");
                    expenseDateControl.setText("");
                    expenseTypeControl.setSelection(0); // Đặt Spinner về giá trị mặc định
                }
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
           }
       });


        return  view;
    }

}