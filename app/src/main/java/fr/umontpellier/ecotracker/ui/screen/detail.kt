package fr.umontpellier.ecotracker.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.ModelService
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.koinInject
import java.time.Instant.now
import java.time.temporal.ChronoUnit

@Composable
fun Detail(
    config: EcoTrackerConfig = koinInject(),
    pkgNetStartService: PkgNetStatService = koinInject(),
    pkgModelService: ModelService = koinInject(),
) {
    Column {
        /**PieConsumptionChart(
        entries = pkgNetStartService.cache.appNetStats.entries.take(10),
        modifier = Modifier
        .fillMaxWidth()
        .height(400.dp)
        )**/

        TextButton(onClick = {
            config.dates = now().minus(31, ChronoUnit.DAYS) to now()
        }) {
            Text(text = "Changer l'intervalle de calcul")
        }
    }
}