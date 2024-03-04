package fr.umontpellier.carbonalyser

import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.umontpellier.carbonalyser.ui.screens.MainScreen
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarbonalyserTheme {
                MainScreen { checkNetworkStats() }
            }
        }
    }

    private fun checkNetworkStats() {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )
        if (mode != AppOpsManager.MODE_ALLOWED) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            return
        }
        val networkStatsManager = getSystemService(NETWORK_STATS_SERVICE) as NetworkStatsManager
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
