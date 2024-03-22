package fr.umontpellier.carbonalyser.ui.components.customComponents

import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class CustomValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value == 0f) ""
        else if (value < 1) {
            "${(value * 1024).toInt()}"
        } else if (value < 10) {
            "%.1f".format(value)
        } else {
            "${value.toInt()}"
        }
    }
}