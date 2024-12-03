package com.example.demoassignment2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DatabaseHelper;
import database.ExpenseEntity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BarChart barChart;
    private PieChart pieChart;
    private DatabaseHelper dbHelper;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        // Lấy đối tượng BarChart từ layout
        barChart = view.findViewById(R.id.chart1);
        pieChart=view.findViewById(R.id.pieChart);
        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(getContext());
        TextView money=view.findViewById(R.id.money);

        // Lấy dữ liệu chi phí cho tháng hiện tại và hiển thị lên BarChart
        List<ExpenseEntity> currentMonthExpenses = getExpensesForCurrentMonth(dbHelper);
        displayExpensesOnChart(currentMonthExpenses);
        displayExpenseTypeDistributionOnPieChart(currentMonthExpenses);



        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        // Lấy tổng budget
        float totalBudget = getTotalBudget();

        // Hiển thị tổng budget vào TextView
        money.setText(String.format("%.2f VNĐ", totalBudget));
        return  view;
    }



    public List<ExpenseEntity> getExpensesForCurrentMonth(DatabaseHelper dbHelper) {
        // Lấy ngày tháng hiện tại



        // Truy vấn cơ sở dữ liệu để lấy chi phí trong tháng hiện tại
        List<ExpenseEntity> expenses = dbHelper.getAllExpenses(); // Giả sử phương thức này đã có
        List<ExpenseEntity> currentMonthExpenses = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentMonth = new SimpleDateFormat("MM/yyyy").format(new Date()); // lấy tháng-năm hiện tại (MM/yyyy)

        for (ExpenseEntity expense : expenses) {
            try {
                // Phân tích ngày tháng từ chuỗi (định dạng dd/MM/yyyy)
                Date expenseDate = sdf.parse(expense.getExpenseDate());
                SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy");
                String expenseMonth = monthFormat.format(expenseDate);

                // So sánh tháng-năm hiện tại và tháng-năm trong chi phí
                if (expenseMonth.equals(currentMonth)) {
                    currentMonthExpenses.add(expense);
                    Log.d("Expense", "Expense Date: " + expense.getExpenseDate() + ", Amount: " + expense.getAmount());
                }
            } catch (ParseException e) {
                Log.e("Expense", "Failed to parse date: " + expense.getExpenseDate(), e);
            }
        }


        return currentMonthExpenses;
    }


    public void displayExpensesOnChart(List<ExpenseEntity> expenses) {
        Map<String, Float> expenseByType = new HashMap<>();

        // Tính tổng chi phí cho mỗi loại chi phí
        for (ExpenseEntity expense : expenses) {
            String type = expense.getExpenseType();
            float amount = Float.parseFloat(expense.getAmount());

            // Cộng dồn số tiền theo từng loại chi phí
            expenseByType.put(type, expenseByType.getOrDefault(type, 0f) + amount);
        }

        // Chuẩn bị dữ liệu cho BarChart
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Float> entry : expenseByType.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            xValues.add(entry.getKey()); // Lưu trữ loại chi phí để hiển thị trên trục X
            index++;
        }

        // Tùy chỉnh Y-axis (trục Y bên trái)
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);  // Bắt đầu từ 0
        yAxisLeft.setDrawGridLines(false);  // Ẩn đường lưới
        yAxisLeft.setDrawAxisLine(false);   // Ẩn trục Y bên trái
        yAxisLeft.setEnabled(false);        // Không hiển thị nhãn bên trục Y trái

        // Vô hiệu hóa trục Y bên phải
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Tùy chỉnh X-axis (trục X)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);  // Ẩn đường lưới dọc
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);       // Đảm bảo chỉ số không bị lặp
        xAxis.setLabelCount(xValues.size()); // Gán nhãn xValues cho trục X

        // Tạo và định dạng BarDataSet
        BarDataSet dataSet = new BarDataSet(entries, "Chi phí theo loại");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Sử dụng màu sắc từ template
        dataSet.setValueTypeface(Typeface.SANS_SERIF);
        dataSet.setValueTextSize(12f); // Cỡ chữ cho giá trị trên thanh

        // Tạo ValueFormatter tùy chỉnh để thêm "VND"
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                float value = barEntry.getY();
                if (value >= 1_000_000) {
                    return String.format("%.1fM", value / 1_000_000); // Triệu
                } else if (value >= 1_000) {
                    return String.format("%.0fK", value / 1_000); // Nghìn
                } else {
                    return String.valueOf((int) value); // Giá trị nguyên nếu nhỏ hơn 1000
                }
            }
        });

        // Chuẩn bị dữ liệu cho BarChart
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.3f); // Độ rộng của các cột

        // Gán dữ liệu vào biểu đồ
        barChart.setData(barData);

        // Vô hiệu hóa phần mô tả và chú giải
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Cập nhật biểu đồ
        barChart.invalidate();
    }

    public void displayExpenseTypeDistributionOnPieChart(List<ExpenseEntity> expenses) {
        Map<String, Integer> expenseTypeCount = new HashMap<>();

        // Đếm số lượng cho từng loại expenseType
        for (ExpenseEntity expense : expenses) {
            String type = expense.getExpenseType();
            expenseTypeCount.put(type, expenseTypeCount.getOrDefault(type, 0) + 1);
        }

        // Tổng số expense
        int totalExpenses = expenses.size();

        // Chuẩn bị dữ liệu cho PieChart
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : expenseTypeCount.entrySet()) {
            float percentage = ((float) entry.getValue() / totalExpenses) * 100f;
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey() + " (" + String.format("%.1f%%", percentage) + ")"));
        }

        // Tạo PieDataSet
        PieDataSet dataSet = new PieDataSet(pieEntries, "Phân bổ loại chi phí");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Sử dụng màu sắc từ template
        dataSet.setValueTextSize(12f); // Cỡ chữ của giá trị trên biểu đồ
        dataSet.setSliceSpace(3f); // Khoảng cách giữa các lát cắt
        dataSet.setSelectionShift(8f); // Hiệu ứng khi chọn lát cắt

        // Hiển thị giá trị số lượng trên lát cắt
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return String.valueOf((int) value); // Hiển thị số lượng
            }
        });

        // Tạo PieData
        PieData pieData = new PieData(dataSet);

        // Cập nhật PieChart
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false); // Hiển thị giá trị thực tế thay vì phần trăm
        pieChart.getDescription().setEnabled(false); // Ẩn phần mô tả
        pieChart.getLegend().setEnabled(true); // Hiển thị chú giải
        pieChart.getLegend().setWordWrapEnabled(true); // Tự động xuống dòng cho chú giải dài
        // Vô hiệu hóa phần mô tả và chú giải
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        // Làm mới biểu đồ
        pieChart.invalidate();
    }

    // Thêm phương thức này vào trong DatabaseHelper
    public float getTotalBudget() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + DatabaseHelper.BudgetEntry.COLUMN_NAME_AMOUNT + ") FROM " + DatabaseHelper.BudgetEntry.TABLE_NAME, null);
        float totalBudget = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalBudget = cursor.getFloat(0);  // Lấy giá trị tổng từ cột SUM
            }
            cursor.close();
        }
        db.close();
        return totalBudget;
    }



    @Override
    public void onResume() {
        super.onResume();
        DatabaseHelper dbHelper=new DatabaseHelper(getContext());

        // Lấy dữ liệu chi phí cho tháng hiện tại và hiển thị lên BarChart
        List<ExpenseEntity> currentMonthExpenses = getExpensesForCurrentMonth(dbHelper);
        displayExpensesOnChart(currentMonthExpenses);
        displayExpenseTypeDistributionOnPieChart(currentMonthExpenses);
    }
}