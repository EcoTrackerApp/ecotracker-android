package fr.umontpellier.ecotracker.service.model.unit

import java.time.Duration
import java.time.Instant
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.pow

private const val CO2_PER_CAR_KM =
    97 // Conducteur moyen francais: https://carlabelling.ademe.fr/chiffrescles/r/evolutionTauxCo2#:~:text=Elle%20se%20situe%20%C3%A0%2097,tous%2Dterrains%20%C2%BB%20toujours%20constant.
private const val CO2_PER_TGV_KM =
    2.4 // https://greenly.earth/fr-fr/blog/actualites-ecologie/empreinte-carbone-comparatif-transports
private const val CO2_PER_PLANE_KM =
    225 //  https://greenly.earth/fr-fr/blog/actualites-ecologie/empreinte-carbone-comparatif-transports

private const val C02_ABSORBED_YEARLY_BY_TREE =
    25000 // https://ecotree.green/combien-de-co2-absorbe-un-arbre#:~:text=est%20plus%20importante.-,Un%20arbre%20absorbe%20environ%2025%20kg%20de%20CO2%20par,par%20arbre%20et%20par%20an.

@JvmInline
value class CO2(val value: Double) {

    val carKm: Meter
        get() = Meter((value / CO2_PER_CAR_KM) * 1000)

    val tgvKm: Meter
        get() = Meter((value / CO2_PER_TGV_KM) * 1000)

    val planeMeter: Meter
        get() = Meter((value / CO2_PER_PLANE_KM) * 1000)


    fun tooTreeEquivalent(start: Instant, end: Instant): String {
        val capacity = TreeCapacityForGivenDuration(start, end)
        val result = value / capacity
        return ceil(result).toInt().toString()
    }

    fun TreeCapacityForGivenDuration(start: Instant, end: Instant): Double {
        val durationInDays = Duration.between(start, end).toDays().toDouble()
        val durationInYears = durationInDays / 365.25
        return C02_ABSORBED_YEARLY_BY_TREE * durationInYears
    }


    override fun toString(): String {
        val absBytes = abs(value)
        val unit = "gCO2"
        if (absBytes < 1000) {
            return String.format("%.2f %s", value, unit)
        }
        val exp = (log(absBytes, 10.0) / log(1000.0, 10.0)).toInt()
        val pre = "kMGTPE"[exp - 1]
        return String.format("%.2f %c%s", value / 1000.0.pow(exp), pre, unit)
    }

}