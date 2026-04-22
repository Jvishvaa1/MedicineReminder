package com.example.smartmedicinereminder

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var tvTaken: TextView
    private lateinit var tvMissed: TextView
    private lateinit var pieChart: PieChart

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tvTotal = findViewById(R.id.tvTotal)
        tvTaken = findViewById(R.id.tvTaken)
        tvMissed = findViewById(R.id.tvMissed)
        pieChart = findViewById(R.id.pieChart)

        dbHelper = DatabaseHelper(this)

        loadStats()
    }

    private fun loadStats() {
        val list = dbHelper.getAllMedicines()

        val total = list.size
        var taken = 0

        for (m in list) {
            if (m.status == 1) {
                taken++
            }
        }

        val missed = total - taken

        // ✅ Text
        tvTotal.text = "Total: $total"
        tvTaken.text = "Taken: $taken"
        tvMissed.text = "Missed: $missed"

        // ✅ Pie Chart
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(taken.toFloat(), "Taken"))
        entries.add(PieEntry(missed.toFloat(), "Missed"))

        val dataSet = PieDataSet(entries, "Medicine Report")

        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#F44336")  // Red
        )

        val data = PieData(dataSet)
        data.setValueTextSize(14f)

        pieChart.data = data
        pieChart.setUsePercentValues(true)
        pieChart.centerText = "Report"
        pieChart.setCenterTextSize(16f)
        pieChart.invalidate()
    }
}