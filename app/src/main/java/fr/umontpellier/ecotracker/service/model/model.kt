package fr.umontpellier.ecotracker.service.model

import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.unit.CO2
import fr.umontpellier.ecotracker.service.netstat.AndroidNetStartService
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService

/**
 * Représente un modèle de conversion de ([AndroidNetStartService.AppNetStat], [EcoTrackerConfig.AppConfig])
 * vers [CO2].
 */
interface Model {

    /**
     * Propose une estimation à partir des données de [AndroidNetStartService.AppNetStat] ainsi que
     * de la configuration spécifique à l'application [EcoTrackerConfig.AppConfig].
     */
    fun estimate(
        netStatResult: PkgNetStatService.Result.App,
        config: EcoTrackerConfig.AppConfig
    ): AppEmission

    /**
     * Représente le résultat d'un modèle post-estimation.
     */
    data class AppEmission(val received: CO2, val sent: CO2) {
        val total: CO2
            get() = CO2(sent.value + received.value)
    }

}