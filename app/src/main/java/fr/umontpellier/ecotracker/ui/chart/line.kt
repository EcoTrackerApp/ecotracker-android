package fr.umontpellier.ecotracker.ui.chart

import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import fr.umontpellier.ecotracker.service.model.ModelService
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.koinInject
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.github.mikephil.charting.data.Entry
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import org.koin.compose.KoinApplication

@Composable
fun LineConsumptionChart(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .padding(16.dp)
    ) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    // Get the data sent from the cache in Bytes
                    val monthConsumptionSent = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
                        day to Bytes(perApp.map { (_, data) -> data.sent.value }.sum())
                    }.toMap()

                    // Get the data received from the cache in Bytes
                    val monthConsumptionReceived = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
                        day to Bytes(perApp.map { (_, data) -> data.received.value }.sum())
                    }.toMap()

                    // Create the entries for the chart with sent and received data
                    val entriesSent = monthConsumptionSent.entries.mapIndexed { index, (day, bytesList) ->
                        Entry(index.toFloat(),  bytesList.value.toFloat())
                    }
                    val entriesReceived = monthConsumptionReceived.entries.mapIndexed { index, (day, bytesList) ->
                        Entry(index.toFloat(),  bytesList.value.toFloat())
                    }

                    // Create the data set for the chart with sent and received data
                    val dataSetSent = LineDataSet(entriesSent, "Envoyé").apply {
                        color = parseColor("#fcae60")
                        setDrawValues(false)
                    }
                    val dataSetReceived = LineDataSet(entriesReceived, "Reçu").apply {
                        color = parseColor("#2a7bb5")
                        setDrawValues(false)
                    }

                    // Add the data sets to the chart
                    data = LineData(dataSetReceived, dataSetSent)

                    // chart configuration
                    legend.isEnabled = true // Enable the legend
                    legend.textSize = 12f // Set the text size for the legend
                    description.isEnabled = false // Disable the description

                    // axis configuration
                    xAxis.position = XAxis.XAxisPosition.BOTTOM // Set the position of the x axis
                    // Set the formatter for the x axis
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val instant = monthConsumptionSent.keys.elementAtOrNull(value.toInt())
                            return if (instant != null) {
                                val formatter = DateTimeFormatter.ofPattern("dd")
                                formatter.format(instant.atZone(ZoneId.systemDefault()))
                            } else {
                                ""
                            }
                        }
                    }
                    axisLeft.axisMinimum = 0f // Set the minimum value for the left axis
                    axisRight.isEnabled = false // Disable the right axis

                    // Disable the zoom and drag
                    setScaleEnabled(false)
                    setPinchZoom(false)
                    isDragEnabled = false
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
        LineConsumptionChart()
    }
}