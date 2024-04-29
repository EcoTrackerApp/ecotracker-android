package fr.umontpellier.ecotracker.ui.chart

import android.graphics.Color.parseColor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun BarConsumptionChart(
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
        Box(
            modifier = Modifier
                .weight(1f)
                .then(modifier)
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

                        // Create the entries for the chart with sent data
                        val entriesSent = monthConsumptionSent.entries.mapIndexed { index, (day, bytes) ->
                            BarEntry(index.toFloat(), bytes.value.toFloat())
                        }

                        // Create the entries for the chart with received data
                        val entriesReceived = monthConsumptionReceived.entries.mapIndexed { index, (day, bytes) ->
                            BarEntry(index.toFloat(), bytes.value.toFloat())
                        }

                        // Create the data set for the chart with sent data
                        val dataSetSent = BarDataSet(entriesSent, "Envoyé").apply {
                            colors = listOf(parseColor("#fcae60"));
                            setDrawValues(false);
                        }

                        // Create the data set for the chart with received data
                        val dataSetReceived = BarDataSet(entriesReceived, "Reçu").apply {
                            colors = listOf(parseColor("#2a7bb5"));
                            setDrawValues(false);
                        }

                        // Add the data sets to the chart
                        data = BarData(dataSetReceived, dataSetSent)

                        // Add a listener to show the values on the chart
                        val chartClickListener = object : OnChartValueSelectedListener {
                            override fun onValueSelected(e: Entry?, h: Highlight?) {
                                dataSetSent.setDrawValues(true)
                                dataSetReceived.setDrawValues(true)
                                invalidate()
                            }

                            override fun onNothingSelected() {
                                dataSetSent.setDrawValues(false)
                                dataSetReceived.setDrawValues(false)
                                invalidate()
                            }
                        }
                        setOnChartValueSelectedListener(chartClickListener)

                        // chart configuration
                        legend.isEnabled = true // Enable the legend
                        legend.textSize = 12f // Set the text size for the legend
                        legend.form = Legend.LegendForm.CIRCLE // Set the form/shape of the legend
                        description.isEnabled = false // Disable the description
                        //animateXY(1000, 1000) // Enable the animation

                        // axis configuration
                        getAxis(YAxis.AxisDependency.LEFT).textSize = 12f //  Set the text size for the left axis
                        xAxis.setDrawGridLines(false) // Disable the x axis grid lines
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
}

@Preview
@Composable
fun BarChartPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        BarConsumptionChart()
    }
}