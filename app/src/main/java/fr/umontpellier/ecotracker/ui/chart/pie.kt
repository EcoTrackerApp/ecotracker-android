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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

@Composable
fun PieConsumptionChart(
    modifier: Modifier = Modifier,
    pkgNetStatService: PkgNetStatService = koinInject(),
    applimit: Int = 10
) {


    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .padding(16.dp)
    ) {
        Column {

            val monthConsumptionPerApp = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
                day to perApp.map { (app, data) -> app to Bytes(data.total.value) }.toMap()
            }.toMap()

            // Créer une liste de PieEntry pour chaque application
            val pieEntries = monthConsumptionPerApp.values
                .asSequence()
                .flatMap { it.entries }
                .groupBy({ it.key }, { it.value })
                .map { (app, bytesList) ->
                    val totalBytes = bytesList.sumOf { it.value.toDouble() }
                    PieEntry(totalBytes.toFloat(), app)
                }
                .sortedByDescending { it.value }
                .take(applimit)
                .toList()

// Créer un PieDataSet à partir de la liste de PieEntry
            val dataSet = PieDataSet(pieEntries, "Consommation par application")
            dataSet.colors =
                ColorTemplate.PASTEL_COLORS.plus(ColorTemplate.JOYFUL_COLORS).plus(ColorTemplate.COLORFUL_COLORS)
                    .plus(ColorTemplate.LIBERTY_COLORS).toMutableList()
            dataSet.valueTextColor = Color.Black.toArgb()
            dataSet.valueTextSize = 24F
            dataSet.sliceSpace = 2f
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value > 5) "${value.roundToInt()}%" else ""
                }
            }

// Créer un PieData à partir du PieDataSet
            val pieData = PieData(dataSet)

// Ajouter le PieData à votre PieChart
            AndroidView(
                factory = { context ->
                    PieChart(context).apply {
                        this.data = pieData
                        this.setEntryLabelTextSize(10F)
                        this.setEntryLabelColor(Color.Black.toArgb())
                        this.legend.setEntries(emptyList())
                        this.setUsePercentValues(true)
                        this.setDrawEntryLabels(false)
                        this.isDrawHoleEnabled = true
                        this.holeRadius = 40f
                        this.transparentCircleRadius = 10f
                        this.legend.isEnabled = true
                        this.description.isEnabled = false
                    }
                },
                modifier = Modifier.fillMaxSize().then(modifier)
            )
        }
    }
}

@Preview
@Composable
fun PieChartPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        PieConsumptionChart()
    }
}