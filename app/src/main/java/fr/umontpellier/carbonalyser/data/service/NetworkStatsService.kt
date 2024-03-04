package fr.umontpellier.carbonalyser.data.service

import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Process
import android.provider.Settings
import android.util.Log

class NetworkStatsService(private val context: Context) {

    fun checkAndLogNetworkStats() {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), context.packageName
        )
        if (mode != AppOpsManager.MODE_ALLOWED) {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            return
        }
        val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val networkType = ConnectivityManager.TYPE_WIFI
        val subscriberId = ""

        try {
            val networkStats: NetworkStats = networkStatsManager.querySummary(
                networkType,
                subscriberId,
                0,
                System.currentTimeMillis()
            )

            while (networkStats.hasNextBucket()) {
                val bucket = NetworkStats.Bucket()
                networkStats.getNextBucket(bucket)
                val uid = bucket.uid
                val rxBytes = bucket.rxBytes
                val txBytes = bucket.txBytes

                Log.i(
                    "NetworkStats",
                    "UID: $uid, RX Bytes: $rxBytes, TX Bytes: $txBytes"
                )
            }
        } catch (e: SecurityException) {
            Log.e("NetworkStats", "Permission Denied", e)
        }
    }
}
