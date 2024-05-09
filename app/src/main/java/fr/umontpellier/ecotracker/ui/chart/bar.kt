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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.ConnectionType
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BarConsumptionChart(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .padding(16.dp)
    ) {
        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    // Get the data sent from the cache in Bytes
                    val sentOverWifi = pkgNetStatService.cache.appNetStats
                        .map { (day, perApp) ->
                            day to Bytes(perApp
                                .flatMap { it.value.data }
                                .filter { it.connection == ConnectionType.WIFI }
                                .sumOf { data -> data.sent.value })
                        }
                        .toMap()
                    val sentOverMobile = pkgNetStatService.cache.appNetStats
                        .map { (day, perApp) ->
                            day to Bytes(perApp
                                .flatMap { it.value.data }
                                .filter { it.connection == ConnectionType.MOBILE }
                                .sumOf { data -> data.sent.value })
                        }
                        .toMap()

                    // Get the data received from the cache in Bytes
                    val receivedOverWifi = pkgNetStatService.cache.appNetStats
                        .map { (day, perApp) ->
                            day to Bytes(perApp
                                .flatMap { it.value.data }
                                .filter { it.connection == ConnectionType.WIFI }
                                .sumOf { data -> data.received.value })
                        }
                        .toMap()
                    val receivedOverMobile = pkgNetStatService.cache.appNetStats
                        .map { (day, perApp) ->
                            day to Bytes(perApp
                                .flatMap { it.value.data }
                                .filter { it.connection == ConnectionType.MOBILE }
                                .sumOf { data -> data.received.value })
                        }
                        .toMap()
                    Log.i("ecotracker", "${sentOverWifi} ${sentOverMobile} $receivedOverWifi $receivedOverMobile")

                    // Create the entries for the chart with sent and received data
                    val entries = sentOverWifi.entries.mapIndexed { index, (day, bytesSent) ->
                        val bytesReceivedWifi = receivedOverWifi[day]?.value ?: 0L
                        val bytesSentMobile = sentOverMobile[day]?.value ?: 0L
                        val bytesReceivedMobile = receivedOverMobile[day]?.value ?: 0L
                        BarEntry(
                            index.toFloat(),
                            floatArrayOf(
                                bytesSent.value.toFloat(),
                                bytesReceivedWifi.toFloat(),
                                bytesSentMobile.toFloat(),
                                bytesReceivedMobile.toFloat()
                            )
                        )
                    }

                    // Create the data set for the chart
                    val dataSet = BarDataSet(entries, "").apply {
                        colors = listOf(
                            parseColor("#fcae60"),
                            parseColor("#2a89b5"),
                            parseColor("#dddd60"),
                            parseColor("#2acf93")
                        )
                        setDrawValues(false)
                        highLightAlpha = 0
                        // Set the labels for the legend
                        stackLabels = arrayOf("Reçu Wifi", "Envoyé", "Reçu Mobile", "Envoyé")

                        // Set the corner radius for the bars
                        barShadowColor = Color.Black.toArgb()
                        barBorderWidth = 1f
                        barBorderColor = Color.Black.toArgb()
                    }

                    // Add the data set to the chart
                    data = BarData(dataSet)

                    // Add a listener to show the values on the chart
                    val chartClickListener = object : OnChartValueSelectedListener {
                        override fun onValueSelected(e: Entry?, h: Highlight?) {
                            val textSize = 12f // Size of the text

                            dataSet.valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return Bytes(value.toLong()).toString()
                                }
                            }

                            dataSet.valueTextSize = textSize
                            dataSet.setDrawValues(true)

                            invalidate()
                        }

                        override fun onNothingSelected() {
                            dataSet.setDrawValues(false)
                            invalidate()
                        }
                    }
                    setOnChartValueSelectedListener(chartClickListener)
                    // chart configuration
                    legend.isEnabled = true // Enable the legend
                    legend.textSize = 15f // Set the text size for the legend
                    legend.form = Legend.LegendForm.CIRCLE // Set the form/shape of the legend
                    description.isEnabled = false // Disable the description
                    animateXY(1000, 1000) // Enable the animation

                    // axis configuration
                    getAxis(YAxis.AxisDependency.LEFT).textSize = 12f //  Set the text size for the left axis
                    xAxis.setDrawGridLines(false) // Disable the x axis grid lines
                    xAxis.position = XAxis.XAxisPosition.BOTTOM // Set the position of the x axis

                    // Value formatter
                    // Set the formatter for the x axis
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val instant = sentOverWifi.keys.elementAtOrNull(value.toInt())
                            return if (instant != null) {
                                val formatter = DateTimeFormatter.ofPattern("dd/MM")
                                formatter.format(instant.atZone(ZoneId.systemDefault()))
                            } else {
                                ""
                            }
                        }
                    }

                    // Set the formatter for the x axis
                    axisLeft.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return Bytes(value.toLong()).toString()
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
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
        )
    }
}

@Preview
@Composable
fun BarChartPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        BarConsumptionChart()
    }
}