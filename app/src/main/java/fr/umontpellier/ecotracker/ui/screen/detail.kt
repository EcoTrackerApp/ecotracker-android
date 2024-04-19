package fr.umontpellier.ecotracker.ui.screen

import PieConsumptionChart
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.ModelService
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.koinInject
import java.time.Instant
import java.time.Instant.now
import java.time.temporal.ChronoUnit

@Composable
fun Detail(
    config: MutableState<EcoTrackerConfig> = koinInject(),
    pkgNetStatService: PkgNetStatService = koinInject(),
    modelService: ModelService = koinInject(),
) {
    Column {
        PieConsumptionChart(
            entries = modelService.results.entries.take(10),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        TextButton(onClick = {
            config.value = config.value.apply {
                interval = now().minus(31, ChronoUnit.DAYS) to now()
            }
        }) {
            Text(text = "Changer l'intervalle de calcul")
        }
    }
}