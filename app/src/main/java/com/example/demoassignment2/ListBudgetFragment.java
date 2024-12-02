package com.example.demoassignment2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import database.Budget;
import database.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListBudgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListBudgetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListBudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListBudgetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListBudgetFragment newInstance(String param1, String param2) {
        ListBudgetFragment fragment = new ListBudgetFragment();
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
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_list_budget, container, false);

        // Lấy tất cả các chi phí từ cơ sở dữ liệu
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Budget> allExpense = dbHelper.getAllBudgets();

        // Tạo ArrayAdapter với layout item_expense chứa TextView
        ArrayAdapter<Budget> adapter = new ArrayAdapter<Budget>(
                requireActivity(),
                R.layout.item_budget,  // Layout cho mỗi item
                allExpense
        )
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Inflate view từ layout item_expense
                View view = super.getView(position, convertView, parent);

                // Lấy đối tượng ExpenseEntity tại vị trí hiện tại
                Budget expense = getItem(position);

                // Tìm TextView trong layout item_expense
                TextView tvExpense = view.findViewById(R.id.tv_budget);

                // Đặt giá trị cho TextView
                if (expense != null && tvExpense != null) {
                    tvExpense.setText(expense.toString());  // Hiển thị thông tin chi phí
                }

                return view;
            }
        };

        // Kết nối adapter với ListView
        ListView listView = view.findViewById(R.id.listAllBudget);
        listView.setAdapter(adapter);

        // Đặt sự kiện click cho item trong ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Budget entry = (Budget) parent.getItemAtPosition(position);
                final String[] options = {"Delete", "Update"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(options, (dialog, item) -> {
                    if (options[item].equals("Delete")) {
                        // Xóa chi phí khỏi ListView
                        allExpense.remove(position);
                        adapter.notifyDataSetChanged();

                        // Xóa chi phí khỏi cơ sở dữ liệu
                        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                        dbHelper.deleteBudget(entry.getId());
                    } else if (options[item].equals("Update")) {
                        Intent itent=new Intent(getActivity(),BudgetUpdate.class);
                        startActivity(itent);
                    }
                });
                builder.show();
            }
        });

        return view;
    }

}