package fr.umontpellier.carbonalyser.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class MonthAxisValueFormatter : ValueFormatter() {
    private val months = arrayOf(
        "", "Jan", "Fev", "Mars", "Avr", "Mai", "Juin",
        "Juil", "Aout", "Sep", "Oct", "Nov", "Dec"
    )

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val index = value.toInt()
        return if (index in months.indices) {
            months[index]
        } else {
            ""
        }
    }
}