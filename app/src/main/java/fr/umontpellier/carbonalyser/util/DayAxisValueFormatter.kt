package fr.umontpellier.carbonalyser.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class DayAxisValueFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val day = value.toInt() // Ajoute 1 car les jours commencent à partir de 1
        return if (day % 2 == 0) { // Alterne entre afficher les chiffres en haut et en bas
            " "
        } else {
            day.toString()
        }
    }
}
