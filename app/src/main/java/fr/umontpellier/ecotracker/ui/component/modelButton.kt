package fr.umontpellier.ecotracker.ui.component


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.ecotracker.R
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun ModelButton(modifier: Modifier = Modifier) {
    val config: EcoTrackerConfig = koinInject()
    val currentModel = remember { mutableStateOf(config.model) }

    fun toggleModel() {
        currentModel.value = if (currentModel.value == "1byte") "swd" else "1byte"
        config.model = currentModel.value
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { toggleModel() }) {
        Text(
            text = "Modèle : ${currentModel.value}",
            color = Color.White,
            fontSize = 17.sp
        )
        IconButton(
            onClick = { toggleModel() }, modifier = Modifier
                .width(20.dp)
                .padding(start = 4.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.commutateur), contentDescription = "Switch Model", tint = Color.White, modifier = Modifier.size(30.dp))
        }
    }
}

@Preview
@Composable
fun ModelButtonPreview(modifier: Modifier = Modifier) {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        ModelButton()
    }
}