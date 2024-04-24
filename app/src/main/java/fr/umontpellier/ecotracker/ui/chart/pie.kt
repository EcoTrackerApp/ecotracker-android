import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.model.ModelService
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun PieConsumptionChart(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modelService: ModelService = koinInject(),
    modifier: Modifier = Modifier
) {
    val dataSet = PieDataSet(listOf(PieEntry(1F, "Prout")), "")
    dataSet.colors = ColorTemplate.PASTEL_COLORS.plus(ColorTemplate.JOYFUL_COLORS).toMutableList()
    dataSet.valueTextColor = Color.Black.toArgb()
    dataSet.valueTextSize = 24F
    val pieData = PieData(dataSet)

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                this.data = pieData
                this.setEntryLabelTextSize(10F)
                this.setEntryLabelColor(Color.Black.toArgb())
                this.legend.setEntries(emptyList())
            }
        },
        modifier = Modifier.then(modifier)
    )
}

@Preview
@Composable
fun PieChartPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        PieConsumptionChart()
    }
}