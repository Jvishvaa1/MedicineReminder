package com.example.smartmedicinereminder;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    TextView tvTotal, tvTaken, tvMissed;
    PieChart pieChart; // ✅ NEW

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvTotal = findViewById(R.id.tvTotal);
        tvTaken = findViewById(R.id.tvTaken);
        tvMissed = findViewById(R.id.tvMissed);
        pieChart = findViewById(R.id.pieChart); // ✅ NEW

        dbHelper = new DatabaseHelper(this);

        loadStats();
    }

    private void loadStats() {
        List<Medicine> list = dbHelper.getAllMedicines();

        int total = list.size();
        int taken = 0;

        for (Medicine m : list) {
            if (m.getStatus() == 1) {
                taken++;
            }
        }

        int missed = total - taken;

        // ✅ Update Text
        tvTotal.setText("Total: " + total);
        tvTaken.setText("Taken: " + taken);
        tvMissed.setText("Missed: " + missed);

        // ✅ 🔥 PIE CHART LOGIC
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(taken, "Taken"));
        entries.add(new PieEntry(missed, "Missed"));

        PieDataSet dataSet = new PieDataSet(entries, "Medicine Report");

        // 🎨 Colors (Professional look)
        dataSet.setColors(
                Color.parseColor("#4CAF50"), // Green = Taken
                Color.parseColor("#F44336")  // Red = Missed
        );

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Report");
        pieChart.setCenterTextSize(16f);
        pieChart.invalidate(); // refresh
    }
}