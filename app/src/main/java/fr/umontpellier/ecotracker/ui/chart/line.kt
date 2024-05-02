package fr.umontpellier.ecotracker.ui.chart

import android.graphics.Color.parseColor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.pow


@Composable
fun LineConsumptionChart(
    appId: Int,
    pkgNetStatService: PkgNetStatService = koinInject()
) {


    // Function to scale the value in Bytes
    fun scaleValue(value: Float): String {
        val absBytes = abs(value)
        val unit = "B"
        if (absBytes < 1000) {
            return "$value $unit"
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
        Column {
            Text(
                text = "Consommation",
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(20.dp, top = 25.dp, bottom = 25.dp),
                fontSize = 20.sp
            )
        }
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    // Préparation des données
                    val monthConsumptionSent = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
                        day to Bytes(perApp.filter { (app, _) -> app == appId }.map { (_, data) -> data.sent.value }.sum())
                    }.toMap()

                    val monthConsumptionReceived = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
                        day to Bytes(perApp.filter { (app, _) -> app == appId }.map { (_, data) -> data.received.value }.sum())
                    }.toMap()

                    val entriesSent = monthConsumptionSent.entries.mapIndexed { index, (day, bytesList) ->
                        Entry(index.toFloat(), bytesList.value.toFloat())
                    }
                    val entriesReceived = monthConsumptionReceived.entries.mapIndexed { index, (day, bytesList) ->
                        Entry(index.toFloat(), bytesList.value.toFloat())
                    }


                    val dataSetSent = LineDataSet(entriesSent, "Envoyé").apply {
                        color = parseColor("#fcae60")
                        setDrawValues(false)
                        lineWidth = 6f
                    }
                    val dataSetReceived = LineDataSet(entriesReceived, "Reçu").apply {
                        color = parseColor("#2a7bb5")
                        setDrawValues(false)
                        lineWidth = 6f
                    }

                    val lineData = LineData(dataSetSent, dataSetReceived)

                    this.data = lineData

                    // Configuration du graphique
                    this.legend.isEnabled = true
                    this.legend.textSize = 12f
                    this.description.isEnabled = false
                    setScaleEnabled(false)
                    setPinchZoom(false)
                    isDragEnabled = false

                    // Configuration de l'axe X
                    this.axisRight.isEnabled = false

                    this.xAxis.isEnabled = true
                    this.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val instant = monthConsumptionSent.keys.elementAtOrNull(value.toInt())
                            return if (instant != null) {
                                val formatter = DateTimeFormatter.ofPattern("dd/MM")
                                formatter.format(instant.atZone(ZoneId.systemDefault()))
                            } else {
                                ""
                            }
                        }
                    }
                    // Set the formatter for the axisLeft
                    axisLeft.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return scaleValue(value)
                        }
                    }

                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun PieChartPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        LineConsumptionChart(1)
    }
}