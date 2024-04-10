package fr.umontpellier.ecotracker.service.netstat

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import kotlinx.coroutines.*

class PkgNetStatService(private val context: Context, private val config: EcoTrackerConfig) {

    val job = Job()
    val scope = CoroutineScope(Dispatchers.IO + job)

    var isNetStatReady = false
    val results = mutableMapOf<Int, AppNetStat>()

    /**
     * Met à jour le [PkgNetStatService] pour la nouvelle configuration donnée.
     */
    fun update() = scope.launch(job) {
        isNetStatReady = false

        val netStat = context.getSystemService(NetworkStatsManager::class.java)
        if (netStat == null) {
            job.cancel("NetworkStatsManager")
            return@launch
        }

        fetchAndStore(netStat, ConnectionType.WIFI)
        fetchAndStore(netStat, ConnectionType.MOBILE)
        job.complete()
    }

    /**
     * Récupère les données auprès du [NetworkStatsManager], il faut noter que [fetchAndStore] est
     * bloquante même si le langage ne l'indique pas, c'est écrit dans la doc de celui-ci.
     */
    @Suppress("RedundantSuspendModifier")
    private suspend fun fetchAndStore(netStat: NetworkStatsManager, connectionType: ConnectionType) {
        val query = netStat.queryDetails(
            connectionType.value,
            null,
            config.interval.first.toEpochMilli(),
            config.interval.second.toEpochMilli()
        )
        while (query.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            query.getNextBucket(bucket)

            val sent = results[bucket.uid]?.sent ?: Bytes(0L)
            val received = results[bucket.uid]?.received ?: Bytes(0L)
            results[bucket.uid] = AppNetStat(
                connectionType,
                Bytes(received.value + bucket.rxBytes),
                Bytes(sent.value + bucket.txBytes)
            )
        }
    }

    data class AppNetStat(val type: ConnectionType, val received: Bytes, val sent: Bytes)

}