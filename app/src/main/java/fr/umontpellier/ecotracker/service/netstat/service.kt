package fr.umontpellier.ecotracker.service.netstat

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.util.Log
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.Instant.now
import java.time.temporal.ChronoUnit

/**
 * Deux implémentation:
 * - [AndroidNetStartService] en production
 * - [Dummy]
 */
interface PkgNetStatService {
    val cacheJob: Job
    val cache: Result

    fun fetchAndCache()

    suspend fun fetch(
        start: Instant, end: Instant,
        precision: Duration = Duration.ofDays(1),
        connections: Array<ConnectionType> = arrayOf(ConnectionType.WIFI, ConnectionType.MOBILE)
    ): Result

    val total: Bytes
        get() = cache.total

    val received: Bytes
        get() = cache.received

    val sent: Bytes
        get() = cache.sent

    /**
     * Une classe de donnée qui contient les données récupérées sur l'intervalle [start], [end]
     * par application.
     */
    data class Result(
        val start: Instant, val end: Instant, val precision: Duration,
        val appNetStats: Map<Instant, Map<Int, App>>
    ) {
        /**
         * Récupère le total des [Bytes] contenu dans ce [Result].
         */
        val total: Bytes
            get() = Bytes(
                appNetStats.map { (key, value) ->
                    value.map { it.value.total.value }
                        .sum()
                }.sum()
            )

        /**
         * Récupère le total des [Bytes] contenu dans ce [Result].
         */
        val received: Bytes
            get() = Bytes(
                appNetStats.map { (key, value) ->
                    value.map { it.value.received.value }
                        .sum()
                }.sum()
            )

        /**
         * Récupère le total des [Bytes] contenu dans ce [Result].
         */
        val sent: Bytes
            get() = Bytes(
                appNetStats.map { (key, value) ->
                    value.map { it.value.sent.value }
                        .sum()
                }.sum()
            )

        data class App(val type: ConnectionType, val received: Bytes, val sent: Bytes) {
            val total: Bytes
                get() = Bytes(received.value + sent.value)
        }
    }
}

class DummyPkgNetStatService : PkgNetStatService {
    override val cacheJob: Job
        get() = Job().apply { complete() }

    override val cache: PkgNetStatService.Result
        get() = PkgNetStatService.Result(
            now().minus(3, ChronoUnit.DAYS), now(), Duration.ofDays(1), mapOf(
                now().minus(3, ChronoUnit.DAYS) to mapOf(
                    1 to PkgNetStatService.Result.App(ConnectionType.MOBILE, Bytes(200), Bytes(500)),
                    2 to PkgNetStatService.Result.App(ConnectionType.MOBILE, Bytes(350), Bytes(400))
                ),
                now().minus(2, ChronoUnit.DAYS) to mapOf(
                    1 to PkgNetStatService.Result.App(ConnectionType.MOBILE, Bytes(550), Bytes(600)),
                    2 to PkgNetStatService.Result.App(ConnectionType.MOBILE, Bytes(650), Bytes(700))
                ),
                now().minus(1, ChronoUnit.DAYS) to mapOf(
                    1 to PkgNetStatService.Result.App(ConnectionType.MOBILE, Bytes(750), Bytes(800)),
                    2 to PkgNetStatService.Result.App(ConnectionType.MOBILE, Bytes(850), Bytes(900))
                )
            )
        )

    override fun fetchAndCache() = Unit
    override suspend fun fetch(
        start: Instant,
        end: Instant,
        precision: Duration,
        connections: Array<ConnectionType>
    ): PkgNetStatService.Result {
        return PkgNetStatService.Result(start, end, precision, emptyMap())
    }
}

/**
 * Représente le [AndroidNetStartService], celui-ci récupère les données auprès d'Android.
 *
 * Pour récupérer les données, deux voies sont possibles:
 *
 * 1. Sans utiliser le cache et en passant par une coroutine gérer au sein du composant:
 * ```kotlin
 * // On récupère le service
 * val pkgNetStat: PkgNetStatService = ...
 * // On récupère les données entre début et fin avec une précision de 1 jour. (Bloquant)
 * val result = pkgNetStat.fetch(debut, fin, Duraton.ofDays(1), arrayOf(ConnectionType.WIFI))
 * ```
 * Attention, l'opération étant bloquante et prenant un certain temps, il n'est pas recommandé de
 * le faire souvent, c'est pour cette raison qu'il existe la deuxième voie.
 *
 * 2. Le [AndroidNetStartService] propose un cache, qui peut-être utilisé par d'autres services:
 * ```kotlin
 * // On récupère le service
 * val pkgNetStat: PkgNetStatService = ..
 * // On utilise le cache
 * pkgNetStat.fetchAndCache()
 * // On peut suivre le statut
 * val isFinished = pkgNetStat.cacheJob.isFinished
 * val totalBytes = pkgNetStat.cache.total
 * ```
 * Les informations des dates, la précision etc... sont récupérées au sein de [EcoTrackerConfig]
 */
class AndroidNetStartService(private val context: Context, private val config: EcoTrackerConfig) : PkgNetStatService {

    /**
     * Contient le [Job] pour le cache
     */
    override var cacheJob = Job()
    override var cache = PkgNetStatService.Result(config.dates.first, config.dates.second, Duration.ofDays(1), mapOf())
    private val scope = CoroutineScope(Dispatchers.IO + cacheJob)

    /**
     * Met à jour le [AndroidNetStartService] pour la nouvelle configuration donnée.
     */
    override fun fetchAndCache() {
        cacheJob = Job()
        scope.launch(cacheJob) {
            try {
                cache = fetch(config.dates.first, config.dates.second, config.precision, ConnectionType.values())
                Log.i("ecotracker", "Fetching and caching completed")
                cacheJob.complete()
            } catch (e: Exception) {
                cacheJob.completeExceptionally(e)
            }

            cacheJob.complete()
        }
    }

    override suspend fun fetch(
        start: Instant, end: Instant,
        precision: Duration,
        connections: Array<ConnectionType>
    ): PkgNetStatService.Result {
        val r: MutableMap<Instant, MutableMap<Int, PkgNetStatService.Result.App>> = mutableMapOf()
        if (end.isBefore(start))
            throw IllegalArgumentException("Fetching data between an invalid interval $start - $end")

        val netStat = context.getSystemService(NetworkStatsManager::class.java)
            ?: throw IllegalStateException("NetworkStatsManager")

        Log.i("ecotracker", "Looking between $start $end")
        for (connection in connections) {
            var s = start
            var e = start.plus(precision)
            while (e.isBefore(end)) {
                Log.i("ecotracker", "Fetching data for $connection between $s and $e")
                val query = netStat.queryDetails(
                    connection.value,
                    null,
                    s.toEpochMilli(),
                    e.toEpochMilli()
                )
                val results = r.getOrDefault(s, mutableMapOf())
                while (query.hasNextBucket()) {
                    val bucket = NetworkStats.Bucket()
                    query.getNextBucket(bucket)

                    val sent = results[bucket.uid]?.sent ?: Bytes(0L)
                    val received = results[bucket.uid]?.received ?: Bytes(0L)
                    results[bucket.uid] = PkgNetStatService.Result.App(
                        connection,
                        Bytes(received.value + bucket.rxBytes),
                        Bytes(sent.value + bucket.txBytes)
                    )
                }
                r[s] = results
                s = e
                e = s.plus(precision)
            }
        }
        return PkgNetStatService.Result(start, end, precision, r)
    }

}