import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.PackageService
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import java.util.stream.Collectors.toList
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.roundToInt


@Composable
fun PieConsumptionChart(
    modifier: Modifier = Modifier,
    pkgNetStatService: PkgNetStatService = koinInject(),
    applimit: Int = 10
) {
    // Modifier la fonction scaleValue pour arrondir les bytes à l'unité
    fun scaleValue(value: Float): String {
        val absBytes = abs(value)
        val unit = "B"
        if (absBytes < 1000) {
            return "${value.roundToInt()} $unit"
        }
        val exp = (log(absBytes.toDouble(), 10.0) / log(1000.0, 10.0)).toInt()
        val pre = "kMGTPE"[exp - 1]
        return String.format("%.1f %c%s", value / 1000.0.pow(exp), pre, unit)
    }

    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .padding(16.dp)
    ) {
        // Ajouter le PieData à votre PieChart
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
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
                            PieEntry(totalBytes.toFloat(), packageService.appLabel(app))
                        }
                        .sortedByDescending { it.value }
                        .take(applimit)
                        .toList()

                    // Créer un PieDataSet à partir de la liste de PieEntry
                    val dataSet = PieDataSet(pieEntries, "Consommation par application")
                    dataSet.colors = listOf(parseColor("#2a89b5"), parseColor("#fcae60")) +
                            ColorTemplate.PASTEL_COLORS.toList() +
                            ColorTemplate.JOYFUL_COLORS.toList()
                    dataSet.valueTextColor = Color.White.toArgb()
                    dataSet.valueTextSize = 15F
                    dataSet.sliceSpace = 2f

                    // Set initial ValueFormatter
                    dataSet.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val total = pieEntries.sumOf { it.value.toDouble() }
                            val percentage = (value / total) * 100
                            return if (percentage >= 4)
                                scaleValue(value)
                            else
                                ""
                        }
                    }

                    // Créer un PieData à partir du PieDataSet
                    val pieData = PieData(dataSet)

                    this.data = pieData
                    this.setEntryLabelTextSize(14F)
                    this.setEntryLabelColor(Color.Black.toArgb())
                    this.legend.setEntries(emptyList())
                    this.setUsePercentValues(false)
                    this.setDrawEntryLabels(false)
                    this.setTransparentCircleColor(Color.White.toArgb())
                    this.setTransparentCircleAlpha(110)
                    this.isDrawHoleEnabled = true
                    this.holeRadius = 40f
                    this.transparentCircleRadius = 45f
                    this.legend.isEnabled = false
                    this.description.isEnabled = false
                    animateXY(1000, 1000)

                    // Ajouter un OnChartValueSelectedListener
                    var showPercentage = false
                    this.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                        override fun onValueSelected(e: Entry?, h: Highlight?) {
                            showPercentage = !showPercentage

                            if (showPercentage) {
                                dataSet.valueFormatter = object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String {
                                        return if (value.toInt() >= 4)
                                            "${value.toInt()} %"
                                        else
                                            ""
                                    }
                                }
                            } else {
                                dataSet.valueFormatter = object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String {
                                        val total = pieEntries.sumOf { it.value.toDouble() }
                                        val percentage = (value / total) * 100
                                        return if (percentage >= 4)
                                            scaleValue(value)
                                        else
                                            ""
                                    }
                                }
                            }
                            this@apply.setUsePercentValues(showPercentage)
                            invalidate()
                        }

                        override fun onNothingSelected() {}
                    })
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
        )
    }
}

@Preview
@Composable
fun PieChartPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        PieConsumptionChart()
    }
}