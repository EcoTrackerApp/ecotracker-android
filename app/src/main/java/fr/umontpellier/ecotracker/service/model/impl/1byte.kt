package fr.umontpellier.ecotracker.service.model.impl

import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.Model
import fr.umontpellier.ecotracker.service.model.unit.CO2
import fr.umontpellier.ecotracker.service.netstat.ConnectionType
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService

object OneByte : Model {
    private const val CO2_PER_KWH_IN_DC_GREY = 0.0599
    private const val CO2_PER_KWH_IN_DC_GREEN = 0.0

    private const val KWH_PER_BYTE_IN_DC = 7.2e-11
    private const val FIXED_NETWORK_WIFI = 1.52e-10
    private const val FOUR_G_MOBILE = 8.84e-10

    private fun apply(value: Double, isGreen: Boolean, connection: ConnectionType): Double {
        val factor = if (isGreen) CO2_PER_KWH_IN_DC_GREEN else CO2_PER_KWH_IN_DC_GREY
        return value * (KWH_PER_BYTE_IN_DC + when (connection) {
            ConnectionType.WIFI -> FIXED_NETWORK_WIFI
            ConnectionType.MOBILE -> FOUR_G_MOBILE
        }) * factor * 1000.0 // Pour passer de kgCO2 -> gCO2
    }

    override fun estimate(
        netStatResult: PkgNetStatService.Result.App,
        config: EcoTrackerConfig.AppConfig
    ): Model.AppEmission {
        val received = netStatResult.data.sumOf { apply(it.received.value.toDouble(), config.isGreen, it.connection) }
        val sent = netStatResult.data.sumOf { apply(it.sent.value.toDouble(), config.isGreen, it.connection) }
        return Model.AppEmission(CO2(received), CO2(sent))
    }
}