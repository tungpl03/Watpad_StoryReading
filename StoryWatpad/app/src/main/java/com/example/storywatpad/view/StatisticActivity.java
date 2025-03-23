package com.example.storywatpad.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticActivity extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button dateButton1, dateButton2;
    AppCompatButton show, back;
    private String startDate = "2025-03-23";
    private String endDate = "2025-03-16";
    private boolean isStartDatePicker = true;

    private PieChart pieChart;
    private BarChart barChart;


    DatabaseHandler db = new DatabaseHandler(this);

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_layout);

        dateButton1 = findViewById(R.id.datePickerButton1);
        dateButton2 = findViewById(R.id.datePickerButton2);
        show = findViewById(R.id.show);
        back = findViewById(R.id.back);
        pieChart = findViewById(R.id.story_Category);
        barChart = findViewById(R.id.story_bar_chart);

        setDefaultDates();

        dateButton1.setText(startDate);
        dateButton2.setText(endDate);

        initDatePicker();

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPieChartData(startDate, endDate);
                loadBarChartData(startDate, endDate);

                String message = "Start Date: " + startDate + "\nEnd Date: " + endDate;
                Toast.makeText(StatisticActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        loadBarChartData(startDate, endDate);

        loadPieChartData(startDate, endDate);

        back.setOnClickListener(view -> {
            finish(); // Đóng Activity hiện tại để quay lại MainActivity
        });
    }

    private void loadBarChartData(String startDate, String endDate) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Cursor cursor = db.getCursor("SELECT CreatedAt, COUNT(StoryId) FROM Story " +
                "WHERE CreatedAt BETWEEN '" + startDate + "' AND '" + endDate + "' " +
                "GROUP BY CreatedAt ORDER BY CreatedAt");

        int index = 0;
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                int count = cursor.getInt(1);
                entries.add(new BarEntry(index, count));
                labels.add(date);
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        BarDataSet barDataSet = new BarDataSet(entries, "Stories Per Day");
        barDataSet.setColors(Color.parseColor("#97FFFF"));
        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.getDescription().setText("Number of stories added per day");
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.invalidate();
    }

//    private void loadPieChartData(String startDate, String endDate) {
//
//        pieChart.clearChart(); // Xóa dữ liệu cũ nếu có
//
//        // Câu truy vấn lấy số lượng truyện theo thể loại trong khoảng thời gian
//        Cursor cursor = db.getCursor("SELECT Genre.Name, COUNT(Story.StoryId) AS StoryCount " +
//                "FROM Story " +
//                "JOIN Genre ON Story.GenreId = Genre.GenreId " +
//                "WHERE Story.CreatedAt BETWEEN '"+startDate+"' AND  '" +endDate+"' GROUP BY Genre.Name");
//
//        // Màu sắc cho biểu đồ
//        String[] colors = {"#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#00FFFF", "#FF00FF", "#C0C0C0", "#800000", "#008000", "#000080", "#CC33FF"};
//        int colorIndex = 0;
//
//        if (cursor.moveToFirst()) {
//            do {
//                String genreName = cursor.getString(0);
//                int count = cursor.getInt(1);
//
//                pieChart.addPieSlice(new PieModel(genreName, count, Color.parseColor(colors[colorIndex])));
//                colorIndex++;
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        pieChart.startAnimation();
//
//    }
private void loadPieChartData(String startDate, String endDate) {
    pieChart.clearChart(); // Xóa dữ liệu cũ nếu có
    LinearLayout legendLayout = findViewById(R.id.legend_layout);
    legendLayout.removeAllViews(); // Xóa dữ liệu cũ nếu có

    Cursor cursor = db.getCursor("SELECT Genre.Name, COUNT(Story.StoryId) AS StoryCount " +
            "FROM Story " +
            "JOIN Genre ON Story.GenreId = Genre.GenreId " +
            "WHERE Story.CreatedAt BETWEEN '"+startDate+"' AND  '" +endDate+"' GROUP BY Genre.Name");

    String[] colors = {"#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#00FFFF", "#FF00FF", "#C0C0C0", "#800000", "#008000", "#000080", "#CC33FF"};
    int colorIndex = 0;

    if (cursor.moveToFirst()) {
        do {
            String genreName = cursor.getString(0);
            int count = cursor.getInt(1);
            int color = Color.parseColor(colors[colorIndex % colors.length]);

            // Thêm dữ liệu vào biểu đồ
            pieChart.addPieSlice(new PieModel(genreName, count, color));

            // Hiển thị danh sách genre + màu sắc tương ứng
            addLegendItem(legendLayout, genreName + " (" + count + ")", color);

            colorIndex++;
        } while (cursor.moveToNext());
    }

    cursor.close();
    pieChart.startAnimation();
}

    // Hàm tạo legend (danh sách thể loại + màu)
    private void addLegendItem(LinearLayout parent, String text, int color) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(10, 5, 10, 5);

        View colorBox = new View(this);
        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(40, 40);
        colorParams.setMargins(5, 5, 10, 5);
        colorBox.setLayoutParams(colorParams);
        colorBox.setBackgroundColor(color);

        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);

        itemLayout.addView(colorBox);
        itemLayout.addView(textView);
        parent.addView(itemLayout);
    }

    private void setDefaultDates() {
        Calendar cal = Calendar.getInstance();

        // Ngày hôm nay
        int endYear = cal.get(Calendar.YEAR);
        int endMonth = cal.get(Calendar.MONTH) + 1; // Chuyển từ 0-11 sang 1-12
        int endDay = cal.get(Calendar.DAY_OF_MONTH);
        endDate = makeDateString(endYear, endMonth, endDay);

        // Ngày trước 1 tuần
        cal.add(Calendar.MONTH, -1);
        int startYear = cal.get(Calendar.YEAR);
        int startMonth = cal.get(Calendar.MONTH) + 1;
        int startDay = cal.get(Calendar.DAY_OF_MONTH);
        startDate = makeDateString(startYear, startMonth, startDay);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month+1;
                String date = makeDateString(year, month, day);
                if (isStartDatePicker) {
                    startDate = date;
                    dateButton1.setText(date);
                } else {
                    endDate = date;
                    dateButton2.setText(date);
                }
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }
    

    private String makeDateString(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month, day);
    }



    public void openDatePicker(View view){
        if (view.getId() == R.id.datePickerButton1) {
            isStartDatePicker = true;
        } else if (view.getId() == R.id.datePickerButton2) {
            isStartDatePicker = false;
        }
        datePickerDialog.show();
    }


}
