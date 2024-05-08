package fr.umontpellier.ecotracker.ui.component


import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import fr.umontpellier.ecotracker.R
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import org.koin.compose.koinInject

@Composable
fun ModelButton(modifier: Modifier = Modifier) {
    val config: EcoTrackerConfig = koinInject()
    val currentModel = remember { mutableStateOf(config.model) }

    fun toggleModel() {
        currentModel.value = if (currentModel.value == "1byte") "swd" else "1byte"
        config.model = currentModel.value
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Model : ${currentModel.value}")
        IconButton(onClick = { toggleModel() }) {
            Icon(painter = painterResource(id = R.drawable.commutateur), contentDescription = "Switch Model")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewModelButton() {
    ModelButton()
}