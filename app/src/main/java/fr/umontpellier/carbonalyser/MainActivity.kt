package fr.umontpellier.carbonalyser

import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarbonalyserTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DebugButton()
                }
            }
        }
    }

    @Composable
    fun DebugButton() {
        Button(
            onClick = {
                val packageManager = applicationContext.packageManager
                val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
                val mode = appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(), packageName
                )
                if (mode != AppOpsManager.MODE_ALLOWED) {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    startActivity(intent)
                    return@Button
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
                        val packages = getAppNameFromUID(applicationContext, uid)

                        Log.i(
                            "NetworkStats",
                            "App: ${packages}, RX Bytes: $rxBytes, TX Bytes: $txBytes"
                        )
                    }
                } catch (e: SecurityException) {
                    Log.e("NetworkStats", "Permission Denied", e)
                }
            },
        ) {
            Text("Debug Network")
        }
    }

    fun getAppNameFromUID(context: Context, uid: Int): String {
        val packageManager = context.packageManager
        val packages = packageManager.getPackagesForUid(uid)

        if (packages != null && packages.isNotEmpty()) {
            val packageName = packages[0]
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                return packageInfo.applicationInfo.loadLabel(packageManager).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        // Si aucune application correspondante n'a été trouvée, vous pouvez renvoyer une chaîne vide ou un message d'erreur
        return ""
    }
}