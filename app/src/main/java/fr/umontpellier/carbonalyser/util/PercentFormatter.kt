package fr.umontpellier.carbonalyser.util

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.formatter.PercentFormatter

class CustomPercentFormatter(private val pieChart: PieChart) : PercentFormatter(pieChart) {
    override fun getFormattedValue(value: Float): String {
        return if (value > 5) super.getFormattedValue(value) else ""
    }
}