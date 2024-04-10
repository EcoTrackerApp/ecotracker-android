package fr.umontpellier.ecotracker.service.model

import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.unit.CO2
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService

class ModelService(
    val pkgNetStatService: PkgNetStatService,
    val config: EcoTrackerConfig
) {

    private val models = mutableMapOf<String, Model>()
    private val results: Map<Int, Model.AppEmission>
        get() = pkgNetStatService.results
            .map { (uid, netStat) ->
                uid to get("1byte")!!.estimate(
                    netStat,
                    config.apps[uid] ?: EcoTrackerConfig.AppConfig()
                )
            }
            .toMap()

    private val total: CO2
        get() = CO2(results.map { (_, emission) -> emission.total.value }.sum())

    private val received: CO2
        get() = CO2(results.map { (_, emission) -> emission.received.value }.sum())

    private val sent: CO2
        get() = CO2(results.map { (_, emission) -> emission.sent.value }.sum())

    /**
     * Retourne pour un nom de [model] une instance de [Model].
     */
    operator fun get(model: String) = models[model]

    /**
     * Estime pour un [uid] donné, les émissions de [CO2] de l'application.
     */
    fun estimate(model: String, uid: Int) = pkgNetStatService.results[uid]?.run {
        models[model]?.estimate(this, config.apps[uid] ?: return null)
    }

}