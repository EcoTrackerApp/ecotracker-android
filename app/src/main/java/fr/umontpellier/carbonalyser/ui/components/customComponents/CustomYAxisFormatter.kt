package fr.umontpellier.carbonalyser.ui.components.customComponents

import com.github.mikephil.charting.formatter.ValueFormatter

class CustomYAxisFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value < 1) {
            "${(value * 1024).toInt()} Mb"
        } else if (value < 10) {
            "%.1f Mb".format(value)
        } else {
            "${value.toInt()} Gb"
        }
    }
}
