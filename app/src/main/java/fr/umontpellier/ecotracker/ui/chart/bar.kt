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
import com.github.mikephil.charting.charts.BarChart
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

                    Log.i(
                        "ecotrackerLOGBAR",
                        "Sent wifi: ${sentOverWifi} | ${sentOverMobile} $receivedOverWifi $receivedOverMobile"
                    )


                    val entriesWifi = mutableListOf<BarEntry>()
                    val entriesMobile = mutableListOf<BarEntry>()

                    sentOverWifi.keys.forEachIndexed { index, day ->
                        val bytesSentWifi = sentOverWifi[day]?.value ?: 0L
                        val bytesReceivedWifi = receivedOverWifi[day]?.value ?: 0L
                        entriesWifi.add(
                            BarEntry(
                                index * 2f,
                                floatArrayOf(bytesSentWifi.toFloat(), bytesReceivedWifi.toFloat())
                            )
                        )

                        val bytesSentMobile = sentOverMobile[day]?.value ?: 0L
                        val bytesReceivedMobile = receivedOverMobile[day]?.value ?: 0L
                        entriesMobile.add(
                            BarEntry(
                                index * 2f + 1f,
                                floatArrayOf(bytesSentMobile.toFloat(), bytesReceivedMobile.toFloat())
                            )
                        )
                    }

                    val dataSetWifi = BarDataSet(entriesWifi, "").apply {
                        colors = listOf(parseColor("#fcae60"), parseColor("#2a89b5"))
                        stackLabels = arrayOf("\uD83D\uDEDC ⬆\uFE0F", "\uD83D\uDEDC ⬇\uFE0F")
                        setDrawValues(false)
                    }

                    val dataSetMobile = BarDataSet(entriesMobile, "").apply {
                        colors = listOf(parseColor("#dddd60"), parseColor("#2acf93"))
                        stackLabels = arrayOf("\uD83D\uDCF6 ⬆\uFE0F", "\uD83D\uDCF6 ⬇\uFE0F")
                        setDrawValues(false)
                    }


                    data = BarData(dataSetWifi, dataSetMobile)

                    Log.i(
                        "ecotrackerLOGData",
                        "\n Final Data: \n  ${data} | "
                    )


                    groupBars(-0.6f, 0.25f, 0.05f)

                    setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                        override fun onValueSelected(e: Entry?, h: Highlight?) {
                            data.dataSets.forEach { dataSet ->
                                dataSet.valueFormatter = object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String {
                                        return Bytes(value.toLong()).toString()
                                    }
                                }
                                dataSet.valueTextSize = 8f
                                dataSet.setDrawValues(true)
                            }
                            invalidate()
                        }

                        override fun onNothingSelected() {
                            data.dataSets.forEach { dataSet ->
                                dataSet.setDrawValues(false)
                            }
                            invalidate()
                        }
                    })

                    legend.isEnabled = true
                    legend.textSize = 15f
                    description.isEnabled = false
                    animateXY(1000, 1000)

                    getAxis(YAxis.AxisDependency.LEFT).textSize = 12f
                    xAxis.setDrawGridLines(false)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setLabelCount(sentOverMobile.keys.size, false)



                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val instant = sentOverMobile.keys.elementAtOrNull(value.toInt() / 2)
                            return if (instant != null) {
                                val formatter = DateTimeFormatter.ofPattern("dd/MM")
                                formatter.format(instant.atZone(ZoneId.systemDefault()))
                            } else {
                                ""
                            }
                        }
                    }


                    axisLeft.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return Bytes(value.toLong()).toString()
                        }
                    }

                    axisLeft.axisMinimum = 0f
                    axisRight.isEnabled = false

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