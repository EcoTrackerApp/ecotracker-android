package fr.umontpellier.ecotracker.ui.chart

import android.graphics.Color.parseColor
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
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.pow

@Composable
fun BarConsumptionChart(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modifier: Modifier = Modifier
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
        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    // Get the data sent from the cache in Bytes
                    val monthConsumptionSent = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
                        day to Bytes(perApp.map { (_, data) -> data.sent.value }.sum())
                    }.toMap()

                    // Get the data received from the cache in Bytes
                    val monthConsumptionReceived = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
                        day to Bytes(perApp.map { (_, data) -> data.received.value }.sum())
                    }.toMap()

                    // Create the entries for the chart with sent and received data
                    val entries = monthConsumptionSent.entries.mapIndexed { index, (day, bytesSent) ->
                        val bytesReceived = monthConsumptionReceived[day]?.value ?: 0f
                        BarEntry(index.toFloat(), floatArrayOf(bytesSent.value.toFloat(), bytesReceived.toFloat()))
                    }

                    // Create the data set for the chart
                    val dataSet = BarDataSet(entries, "").apply {
                        colors = listOf(parseColor("#fcae60"), parseColor("#2a89b5"))
                        setDrawValues(false)
                        highLightAlpha = 0
                        // Set the labels for the legend
                        setStackLabels(arrayOf("Reçu", "Envoyé"))

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
                                    return scaleValue(value);
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
                            val instant = monthConsumptionSent.keys.elementAtOrNull(value.toInt())
                            return if (instant != null) {
                                val formatter = DateTimeFormatter.ofPattern("dd")
                                formatter.format(instant.atZone(ZoneId.systemDefault()))
                            } else {
                                ""
                            }
                        }
                    }

                    // Set the formatter for the x axis
                    axisLeft.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return scaleValue(value)
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
            modifier = Modifier.fillMaxSize().then(modifier)
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