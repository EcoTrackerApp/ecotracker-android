package fr.umontpellier.ecotracker.ui.chart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.model.ModelService
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun BarConsumptionChart(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modelService: ModelService = koinInject(),
    modifier: Modifier = Modifier
) {
    // Pas nécessaire, à part si on change les valeurs.
    // pkgNetStatService.fetchAndCache()
    val monthConsumption = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
        day to Bytes(perApp.map { (id, data) -> data.total.value }.sum())
    }.toMap()
    val entries = monthConsumption.entries.mapIndexed { index, (day, bytes) ->
        BarEntry(index.toFloat(), bytes.value.toFloat())
    }
    val dataSet = BarDataSet(entries, "Consommation")
    dataSet.colors = ColorTemplate.PASTEL_COLORS.plus(ColorTemplate.JOYFUL_COLORS).toMutableList()
    dataSet.valueTextColor = Color.Black.toArgb()
    dataSet.valueTextSize = 24F
    val barData = BarData(dataSet)

    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .then(modifier)
        ) {
            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        this.data = barData
                        legend.isEnabled = false
                        getAxis(YAxis.AxisDependency.LEFT).textSize = 12f
                        animateXY(1000, 1000)
                        xAxis.setDrawGridLines(false)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun BarChartPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        BarConsumptionChart()
    }
}