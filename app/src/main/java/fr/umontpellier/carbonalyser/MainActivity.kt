package fr.umontpellier.carbonalyser

import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarbonalyserTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Button(
                        modifier = Modifier.defaultMinSize(),
                        onClick = {
                            val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
                            val mode = appOps.checkOpNoThrow(
                                AppOpsManager.OPSTR_GET_USAGE_STATS,
                                Process.myUid(), packageName
                            )
                            if (mode != AppOpsManager.MODE_ALLOWED) {
                                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                startActivity(intent)
                                return@Button
                            }
                            val networkStatsManager =
                                applicationContext.getSystemService(NetworkStatsManager::class.java)
                            val networkType = ConnectivityManager.TYPE_WIFI
                            val subscriberId = "" // Subscriber id, usually empty for WIFI

                            val networkStats = networkStatsManager.querySummary(
                                networkType,
                                subscriberId,
                                0,
                                System.currentTimeMillis()
                            )

                            Log.i("Carbonalyzer", "Purée")
                            while (networkStats.hasNextBucket()) {
                                val bucket = NetworkStats.Bucket()
                                while (networkStats.getNextBucket(NetworkStats.Bucket())) {
                                    val uid = bucket.uid
                                    val tag = bucket.tag
                                    val state = bucket.state
                                    val metered = bucket.metered
                                    val roaming = bucket.roaming
                                    val defaultNetworkStatus = bucket.defaultNetworkStatus
                                    val startTimeStamp = bucket.startTimeStamp
                                    val endTimeStamp = bucket.endTimeStamp
                                    val rxBytes = bucket.rxBytes
                                    val txBytes = bucket.txBytes
                                    val rxPackets = bucket.rxPackets
                                    val txPackets = bucket.txPackets

                                    Log.i(
                                        "Carbonalyzer", """
                                            UID: $uid
                                            Tag: $tag
                                            State: $state
                                            Metered: $metered
                                            Roaming: $roaming
                                            Default Network Status: $defaultNetworkStatus
                                            Start Time Stamp: $startTimeStamp
                                            End Time Stamp: $endTimeStamp
                                            RX Bytes: $rxBytes
                                            TX Bytes: $txBytes
                                            RX Packets: $rxPackets
                                            TX Packets: $txPackets
                                        """.trimIndent()
                                    )
                                }
                            }
                        },
                    ) {
                        Text("Debug")
                    }
                }
            }
        }
    }
}