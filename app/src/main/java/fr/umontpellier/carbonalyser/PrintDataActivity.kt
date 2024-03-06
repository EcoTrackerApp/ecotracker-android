package fr.umontpellier.carbonalyser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class PrintDataActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val topAppsData = intent.getStringArrayExtra("topAppsData") ?: arrayOf()
            val topAppsLastMinute = intent.getStringArrayExtra("topAppsLastMinute") ?: arrayOf()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Top Applications") },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                            }
                        }
                    )
                }
            ) {
                Column(modifier = Modifier.padding(it).padding(16.dp)) {
                    Text(text = "Top 5 Global", style = MaterialTheme.typography.bodyLarge)
                    topAppsData.forEach { appData ->
                        Text(text = appData)
                    }
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    Text(text = "Top 5 Last Minute", style = MaterialTheme.typography.bodyLarge)
                    topAppsLastMinute.forEach { appData ->
                        Text(text = appData)
                    }
                }
            }
        }
    }
}
