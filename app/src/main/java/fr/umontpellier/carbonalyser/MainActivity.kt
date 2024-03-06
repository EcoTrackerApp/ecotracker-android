package fr.umontpellier.carbonalyser

import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import fr.umontpellier.carbonalyser.android.Connectivity
import fr.umontpellier.carbonalyser.android.hasUsageAccess
import fr.umontpellier.carbonalyser.android.openUsageAccessSettings
import fr.umontpellier.carbonalyser.android.packageNetworkStatsManager
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme
import kotlinx.coroutines.launch
import java.time.Instant

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
                if (!hasUsageAccess) {
                    openUsageAccessSettings()
                }else{
                    launchPrintDataActivity();
                }
            },
        ) {
            Text("Debug Network")
        }
    }

    private fun launchPrintDataActivity() {
        val now = Instant.now()
        val oneMinuteAgo = now.minusSeconds(60)
        lifecycleScope.launch {
            val topAppsOverall = applicationContext.packageNetworkStatsManager
                .collectTopAppsBySentBytes(Instant.ofEpochMilli(0L), now, 5).toTypedArray()

            val topAppsLastMinute = applicationContext.packageNetworkStatsManager
                .collectTopAppsBySentBytes(oneMinuteAgo, now, 5).toTypedArray()

            val intent = Intent(this@MainActivity, PrintDataActivity::class.java)
            intent.putExtra("topAppsData", topAppsOverall)
            intent.putExtra("topAppsLastMinute", topAppsLastMinute)
            startActivity(intent)
        }
    }

}