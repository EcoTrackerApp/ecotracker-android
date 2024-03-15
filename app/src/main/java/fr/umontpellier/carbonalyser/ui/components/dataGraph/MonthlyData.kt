package fr.umontpellier.carbonalyser.ui.components.dataGraph


import androidx.compose.foundation.background
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
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme
import fr.umontpellier.carbonalyser.util.MonthAxisValueFormatter
import java.time.LocalDate
import java.time.Month
import kotlin.random.Random

fun generateRandomDataForYear(year: Int): Pair<Map<LocalDate, Float>, Map<LocalDate, Float>> {
    val startDate = LocalDate.of(year, 1, 1)
    val endDate = LocalDate.of(year, 12, 31)

    val dataSent = mutableMapOf<LocalDate, Float>()
    val dataReceived = mutableMapOf<LocalDate, Float>()

    var currentDate = startDate
    while (currentDate <= endDate) {
        val randomSent = Random.nextFloat() * 3
        val randomReceived = Random.nextFloat() * 10

        dataSent[currentDate] = randomSent
        dataReceived[currentDate] = randomReceived

        currentDate = currentDate.plusDays(1)
    }

    return Pair(dataSent, dataReceived)
}

@Composable
fun MonthlyData(year: Int) {
    val (dataSent, dataReceived) = generateRandomDataForYear(year)
    val groupedDataSent = sortDataByMonth(dataSent)
    val groupedDataReceived = sortDataByMonth(dataReceived)

    val groupedData = mutableListOf<Pair<Int, Pair<Float, Float>>>()
    groupedDataSent.forEach { (month, value) ->
        val receivedValue = groupedDataReceived[month] ?: 0f
        groupedData.add(month.value to Pair(value, receivedValue))
    }

    CarbonalyserTheme {
        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f, true)
                .padding(16.dp)
        ) {
            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        setChart(groupedData)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun BarChart.setChart(groupedData: List<Pair<Int, Pair<Float, Float>>>) {
    val barEntriesSent = mutableListOf<BarEntry>()
    val barEntriesReceived = mutableListOf<BarEntry>()
    val labels = mutableListOf<String>()

    groupedData.forEachIndexed { index, (month, data) ->
        val total = data.first + data.second
        barEntriesSent.add(BarEntry(index.toFloat() - 0.2f, data.first))
        barEntriesReceived.add(BarEntry(index.toFloat() + 0.2f, data.second))
        labels.add(month.toString())
    }

    val dataSetSent = BarDataSet(barEntriesSent, "Données envoyées (GB)")
    dataSetSent.color = android.graphics.Color.BLUE

    val dataSetReceived = BarDataSet(barEntriesReceived, "Données reçues (GB)")
    dataSetReceived.color = android.graphics.Color.GREEN

    val barData = BarData(dataSetSent, dataSetReceived)
    barData.barWidth = 0.4f

    data = barData

    description.isEnabled = false

    val xAxis: XAxis = xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.valueFormatter = IndexAxisValueFormatter(labels)
    xAxis.setGranularity(1f)
    xAxis.labelCount = labels.size

    val yAxisLeft: YAxis = axisLeft
    yAxisLeft.axisMinimum = 0f

    val yAxisRight: YAxis = axisRight
    yAxisRight.isEnabled = false

    invalidate()
}

fun sortDataByMonth(data: Map<LocalDate, Float>): Map<Month, Float> {
    val sortedData = sortedMapOf<Month, Float>()
    data.forEach { (date, value) ->
        val month = date.month
        val currentValue = sortedData[month] ?: 0f
        sortedData[month] = currentValue + value
    }
    return sortedData
}


@Preview(showBackground = false)
@Composable
fun PreviewMonthlyData() {
    MonthlyData(2024)
}
