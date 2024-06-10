package com.example.opsc_poe


import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.util.*
import kotlin.collections.ArrayList
class Graph : AppCompatActivity() {

    private lateinit var chart: BarChart
    private lateinit var periodSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        chart = findViewById(R.id.chart)
        periodSpinner = findViewById(R.id.period_spinner)

        val periods = arrayOf("Today", "This Week", "This Month", "This Year")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periods)
        periodSpinner.adapter = adapter

        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        val entries = generateDummyData(1) // Generate dummy data for today
                        updateChart(entries)
                    }
                    1 -> {
                        val entries = generateDummyData(7) // Generate dummy data for this week
                        updateChart(entries)
                    }
                    2 -> {
                        val entries = generateDummyData(30) // Generate dummy data for this month
                        updateChart(entries)
                    }
                    3 -> {
                        val entries = generateDummyData(365) // Generate dummy data for this year
                        updateChart(entries)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val entries = generateDummyData(30) // Generate dummy data for 30 days
        updateChart(entries)
    }

    private fun generateDummyData(numDays: Int): ArrayList<BarEntry> {
        val entries = ArrayList<BarEntry>()
        val random = Random()

        for (i in 0 until numDays) {
            val hoursWorked = random.nextFloat() * 10 // Generate random hours worked
            entries.add(BarEntry(i.toFloat(), hoursWorked))
        }

        return entries
    }

    private fun updateChart(entries: ArrayList<BarEntry>) {
        val dataSet = BarDataSet(entries, "Total Hours Worked")

        // Customize bar appearance
        dataSet.color = Color.BLUE

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(dataSet)

        val data = BarData(dataSets)

        // Set up chart
        chart.data = data
        chart.invalidate()
    }
}