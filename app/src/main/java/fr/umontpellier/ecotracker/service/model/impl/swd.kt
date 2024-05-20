package fr.umontpellier.ecotracker.service.model.impl

import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.Model
import fr.umontpellier.ecotracker.service.model.unit.CO2
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService

object SustainableWebDesign : Model {
    //Plus d'informations sur le modele: https://sustainablewebdesign.org/calculating-digital-emissions/


    // Taux de conversion kWh/GB
    private const val KWH_PER_GB = 0.81

    // Facteur carbone (intensité carbone) en grammes par kilowattheure (g/kWh) (valeur par defaut)
    // TODO: Implementer la localisation
    private const val CARBON_INTENSITY_GLOBAL = 442.0
    private const val CARBON_INTENSITY_EUROPE = 310.0
    private const val CARBON_INTENSITY_FRANCE = 71.0
    //https://ember-climate.org/data/data-tools/data-explorer/

    // Émissions de CO2 pour les énergies renouvelables en g/kWh
    private const val CARBON_FACTOR_RENEWABLE = 50.0


    override fun estimate(
        netStatResult: PkgNetStatService.Result.App,
        config: EcoTrackerConfig.AppConfig
    ): Model.AppEmission {
        val bytesReceivedCO2 = perByte(netStatResult.received.value.toDouble(), config.isGreen)
        val bytesSentCO2 = perByte(netStatResult.sent.value.toDouble(), config.isGreen)
        return Model.AppEmission(CO2(bytesReceivedCO2), CO2(bytesSentCO2))
    }


    private fun perByte(bytes: Double, green: Boolean): Double {
        if (bytes < 1) {
            return 0.0
        }
        return KhwtoCo2(bytesToKwh(bytes), green)
    }

    fun bytesToKwh(byteAmount: Double): Double {
        //e1 et e2 sont normalement la valeur en bytes du trafic d'un nouveau visieur et d'un visiteur regulier, a voir comment transpoer pour une application
        val e1 = byteAmount * KWH_PER_GB * 0.75
        val e2 = byteAmount * KWH_PER_GB * 0.25 * 0.02
        return (e1 + e2) / 1.0e9
    }

    fun KhwtoCo2(kwHAmount: Double, green: Boolean): Double {
        if (green) {
            return kwHAmount * CARBON_FACTOR_RENEWABLE
        } else
            return kwHAmount * CARBON_INTENSITY_GLOBAL
    }


}