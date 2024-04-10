package fr.umontpellier.ecotracker.service.model.unit

import kotlin.math.abs
import kotlin.math.log
import kotlin.math.pow

@JvmInline
value class CO2(val value: Double) {

    override fun toString(): String {
        val absBytes = abs(value)
        val unit = "kgCO2"
        if (absBytes < 1000) {
            return "$value $unit"
        }
        val exp = (log(absBytes, 10.0) / log(1000.0, 10.0)).toInt()
        val pre = "kMGTPE"[exp - 1]
        return String.format("%.1f %c%s", value / 1000.0.pow(exp), pre, unit)
    }

}