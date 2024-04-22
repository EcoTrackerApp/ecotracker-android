package fr.umontpellier.ecotracker.service.model

import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.impl.OneByte
import fr.umontpellier.ecotracker.service.model.unit.CO2
import fr.umontpellier.ecotracker.service.netstat.AndroidNetStartService

interface ModelService {
    val total: CO2
    val received: CO2
    val sent: CO2
}

class DummyModelService : ModelService {
    override val total = CO2(100.0)
    override val sent = CO2(50.0)
    override val received = CO2(50.0)
}

class AndroidModelService(
    private val config: EcoTrackerConfig,
    private val androidNetStartService: AndroidNetStartService,
) : ModelService {

    private val models = mutableMapOf<String, Model>(
        "1byte" to OneByte
    )

    val model: Model
        get() = this[config.model]!!

    val results: LinkedHashMap<Int, Model.AppEmission>
        // TODO: Update this method
        get() = LinkedHashMap()

    override val total: CO2
        get() = CO2(results.map { (_, emission) -> emission.total.value }.sum())

    override val received: CO2
        get() = CO2(results.map { (_, emission) -> emission.received.value }.sum())

    override val sent: CO2
        get() = CO2(results.map { (_, emission) -> emission.sent.value }.sum())

    /**
     * Retourne pour un nom de [model] une instance de [Model].
     */
    operator fun get(model: String) = models[model]

}