package fr.umontpellier.carbonalyser

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarbonalyserTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Button(
                        onClick = {
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
                                val bucket = networkStats.getNextBucket(NetworkStats.Bucket())
                                Log.i("Carbonalyzer", "Entry: $bucket")
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text("Debug")
                    }
                }
            }
        }
    }
}