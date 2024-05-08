package fr.umontpellier.ecotracker.service.model

import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.impl.OneByte
import fr.umontpellier.ecotracker.service.model.impl.SustainableWebDesign
import fr.umontpellier.ecotracker.service.model.unit.CO2
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import java.time.Instant

interface ModelService {
    val total: CO2
    val received: CO2
    val sent: CO2

    val results: Map<Instant, LinkedHashMap<Int, Model.AppEmission>>
}

class DummyModelService(val pkgNetStatService: PkgNetStatService) : ModelService {
    override val total = CO2(100.0)
    override val sent = CO2(50.0)
    override val received = CO2(50.0)

    override val results: Map<Instant, LinkedHashMap<Int, Model.AppEmission>>
        get() = pkgNetStatService.cache.appNetStats.map { (date, map) ->
            date to map.map { (uid, arr) ->
                uid to OneByte.estimate(
                    arr,
                    EcoTrackerConfig.AppConfig()
                )
            }.sortedByDescending { it.second.total.value }.toMap(LinkedHashMap())
        }.toMap()
}

class AndroidModelService(
    private val config: EcoTrackerConfig,
    private val pkgNetStatService: PkgNetStatService,
) : ModelService {

    private val models = mutableMapOf<String, Model>(
        "1byte" to OneByte,
        "swd" to SustainableWebDesign
    )

    val model: Model
        get() = this[config.model]!!

    override val results: Map<Instant, LinkedHashMap<Int, Model.AppEmission>>
        get() = pkgNetStatService.cache.appNetStats.map { (date, map) ->
            date to map.map { (uid, arr) ->
                uid to model.estimate(
                    arr,
                    config.apps[uid] ?: EcoTrackerConfig.AppConfig()
                )
            }.sortedByDescending { it.second.total.value }.toMap(LinkedHashMap())
        }.toMap()

    override val total: CO2
        get() = CO2(results.map { (_, data) ->
            data.map { (uid, app) -> app.total.value }
                .sum()
        }.sum())

    override val received: CO2
        get() = CO2(results.map { (_, data) ->
            data.map { (uid, app) -> app.received.value }
                .sum()
        }.sum())

    override val sent: CO2
        get() = CO2(results.map { (_, data) ->
            data.map { (uid, app) -> app.sent.value }
                .sum()
        }.sum())

    /**
     * Retourne pour un nom de [model] une instance de [Model].
     */
    operator fun get(model: String) = models[model]

}