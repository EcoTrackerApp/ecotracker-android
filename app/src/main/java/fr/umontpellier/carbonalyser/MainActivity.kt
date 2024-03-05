package fr.umontpellier.carbonalyser

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
import fr.umontpellier.carbonalyser.android.hasUsageAccess
import fr.umontpellier.carbonalyser.android.openUsageAccessSettings
import fr.umontpellier.carbonalyser.android.packageNetworkStatsManager
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                lifecycleScope.launch {
                    fetchNetworkStats()
                }
            },
        ) {
            Text("Debug Network")
        }
    }

    private suspend fun fetchNetworkStats() {
        withContext(Dispatchers.IO) {
            if (!hasUsageAccess) {
                openUsageAccessSettings()
            }

            try {
                applicationContext.packageNetworkStatsManager
                    .collectWifi(Instant.ofEpochMilli(0L), Instant.now())
                    .forEach { println(it) }
            } catch (e: SecurityException) {
                Log.e("NetworkStats", "Permission Denied", e)
            }
        }
    }

}