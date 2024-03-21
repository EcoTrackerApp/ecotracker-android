package fr.umontpellier.carbonalyser.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class DayAxisValueFormatter : ValueFormatter() {
    private val hours = arrayOf(
        "00",
        "01",
        "02",
        "03",
        "04",
        "05",
        "06",
        "07",
        "08",
        "09",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23"

    )

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val index = value.toInt().coerceIn(0, hours.size - 1)
        return hours[index]
    }
}
