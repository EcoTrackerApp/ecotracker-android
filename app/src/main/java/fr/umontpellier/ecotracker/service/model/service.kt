package fr.umontpellier.ecotracker.service.model

import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.impl.OneByte
import fr.umontpellier.ecotracker.service.model.unit.CO2
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService

class ModelService(
    private val pkgNetStatService: PkgNetStatService,
    private val config: EcoTrackerConfig
) {

    private val models = mutableMapOf<String, Model>(
        "1byte" to OneByte
    )
    private val results: Map<Int, Model.AppEmission>
        get() = pkgNetStatService.results
            .map { (uid, netStat) ->
                uid to get(config.model)!!.estimate(
                    netStat,
                    config.apps[uid] ?: EcoTrackerConfig.AppConfig()
                )
            }
            .toMap()

    val total: CO2
        get() = CO2(results.map { (_, emission) -> emission.total.value }.sum())

    val received: CO2
        get() = CO2(results.map { (_, emission) -> emission.received.value }.sum())

    val sent: CO2
        get() = CO2(results.map { (_, emission) -> emission.sent.value }.sum())

    val model: Model
        get() = this[config.model]!!

    /**
     * Retourne pour un nom de [model] une instance de [Model].
     */
    operator fun get(model: String) = models[model]

}