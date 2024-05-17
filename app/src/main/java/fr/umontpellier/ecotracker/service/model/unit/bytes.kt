package fr.umontpellier.ecotracker.service.model.unit

import kotlin.math.abs
import kotlin.math.log
import kotlin.math.pow

const val AVG_MONTHLY_USAGE_FRANCE = (10.5e9).toLong()
const val AVG_DAY_USAGE_FRANCE = (AVG_MONTHLY_USAGE_FRANCE / 31.0).toLong()

// https://fr.statista.com/statistiques/1360250/consommation-mensuelle-moyenne-de-donnees-internet-sur-telephone-mobile-france/
enum class Usage {
    HIGH,
    MEDIUM,
    LOW
}

@JvmInline
value class Bytes(val value: Long) {

    fun usage(days: Long): Usage {
        return when (value) {
            in 0..(0.75 * AVG_DAY_USAGE_FRANCE * days).toLong() -> Usage.LOW
            in (0.75 * AVG_DAY_USAGE_FRANCE * days).toLong()..(1.25 * AVG_DAY_USAGE_FRANCE * days).toLong() -> Usage.MEDIUM
            else -> Usage.HIGH
        }
    }

    operator fun plus(other: Bytes): Bytes {
        return Bytes(this.value + other.value)
    }

    override fun toString(): String {
        val absBytes = abs(value.toDouble())
        val unit = "B"
        if (absBytes < 1000) {
            return "$value $unit"
        }
        val exp = (log(absBytes, 10.0) / log(1000.0, 10.0)).toInt()
        val pre = "kMGTPE"[exp - 1]
        return String.format("%.1f %c%s", value / 1000.0.pow(exp), pre, unit)
    }

}