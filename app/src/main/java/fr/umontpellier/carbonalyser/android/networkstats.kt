package fr.umontpellier.carbonalyser.android

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import fr.umontpellier.carbonalyser.utils.Bytes
import java.time.Instant


@Suppress("DEPRECATION")
enum class Connectivity(val type: Int) {
    WIFI(ConnectivityManager.TYPE_WIFI),
    MOBILE(ConnectivityManager.TYPE_MOBILE)
}

/**
 * Represents network statistics for a package (app) within a specified time frame.
 * @property packages List of [PackageInfo] objects representing packages.
 * @property networkBucket [NetworkStats.Bucket] containing network statistics.
 */
class PackageNetworkStats(
    val connectivity: Connectivity,
    val packages: List<PackageInfo>,
    val networkBucket: NetworkStats.Bucket
) {
    /**
     * Start time of the network statistics collection interval.
     */
    val start: Instant
        get() = Instant.ofEpochMilli(networkBucket.startTimeStamp)

    /**
     * End time of the network statistics collection interval.
     */
    val end: Instant
        get() = Instant.ofEpochMilli(networkBucket.endTimeStamp)

    /**
     * Total number of bytes sent during the network statistics collection interval.
     */
    val bytesSent: Bytes
        get() = Bytes(networkBucket.txBytes)

    /**
     * Total number of bytes received during the network statistics collection interval.
     */
    val bytesReceived: Bytes
        get() = Bytes(networkBucket.rxBytes)

    /**
     * Total number of packets sent during the network statistics collection interval.
     */
    val packetsSent: Long
        get() = networkBucket.txPackets

    /**
     * Total number of packets received during the network statistics collection interval.
     */
    val packetsReceived: Long
        get() = networkBucket.rxPackets

    /**
     * Generates a string representation of the network statistics.
     */
    override fun toString(): String {
        return "PackageNetworkStats(" +
                "uid=${networkBucket.uid}, " +
                "packages=${packages.map { it.packageName }}, " +
                "connectivity=${connectivity}, " +
                "start=${start.formatted}, " +
                "end=${end.formatted}, " +
                "bytesSent=$bytesSent, " +
                "bytesReceived=$bytesReceived, " +
                "packetsSent=$packetsSent, " +
                "packetsReceived=$packetsReceived)"
    }
}

/**
 * Manages the collection of network statistics for packages (apps).
 * @property context Application context.
 */
class PackageNetworkStatsManager(private val context: Context) {

    /**
     * Collects network statistics for packages within the specified time frame.
     *
     * @param start Start time of the collection period.
     * @param end End time of the collection period.
     * @return List of [PackageNetworkStats] objects containing network statistics for each package.
     */
    suspend fun collect(start: Instant, end: Instant, connectivities: Array<Connectivity>): List<PackageNetworkStats> {
        val buffer = mutableListOf<PackageNetworkStats>()
        connectivities.forEach {
            collect(start, end, it, buffer)
        }
        return buffer
    }

    /**
     * Collects network statistics for a specific network type.
     *
     * @param start Start time of the collection period.
     * @param end End time of the collection period.
     * @param connectivity Type of network connectivity (e.g., [ConnectivityManager.TYPE_WIFI]).
     * @param buffer Mutable list to store collected [PackageNetworkStats] objects.
     */
    @Suppress("RedundantSuspendModifier", "DEPRECATION")
    private suspend fun collect(
        start: Instant,
        end: Instant,
        connectivity: Connectivity,
        buffer: MutableList<PackageNetworkStats>
    ) {
        val networkStatsManager = context.getSystemService(NetworkStatsManager::class.java)
        val wifiNetworkSummary =
            networkStatsManager.querySummary(
                connectivity.type,
                null,
                start.toEpochMilli(),
                end.toEpochMilli()
            )
        while (wifiNetworkSummary.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            if (!wifiNetworkSummary.getNextBucket(bucket))
                continue
            buffer.add(
                PackageNetworkStats(
                    connectivity,
                    context.packageManager.getPackagesForUid(bucket.uid)
                        ?.map { context.packageManager.getPackageInfo(it, PackageManager.GET_META_DATA) }
                        .orEmpty(),
                    bucket
                )
            )
        }
    }
}

/**
 * Extension property to easily access [PackageNetworkStatsManager] from [Context].
 */
inline val Context.packageNetworkStatsManager: PackageNetworkStatsManager
    get() = PackageNetworkStatsManager(this)