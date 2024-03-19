package fr.umontpellier.carbonalyser.ui.components.dataGraph


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomDropdownMenu
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomTextField
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
        val randomSent = Random.nextFloat() * (2 * Random.nextInt(1, currentDate.month.value + 1))
        val randomReceived = Random.nextFloat() * (4 * Random.nextInt(1, currentDate.month.value + 1))

        dataSent[currentDate] = randomSent
        dataReceived[currentDate] = randomReceived

        currentDate = currentDate.plusDays(1)
    }

    return Pair(dataSent, dataReceived)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyData(dataSent: Map<LocalDate, Float>, dataReceived: Map<LocalDate, Float>) {
    val selectedOption = remember { mutableStateOf("Données totales") }
    val options = listOf("Données envoyées", "Données reçus", "Données totales")
    val expanded = remember { mutableStateOf(false) }

    val groupedDataSent = sortDataByMonth(dataSent)
    val groupedDataReceived = sortDataByMonth(dataReceived)

    val chart = remember { mutableStateOf<BarChart?>(null) }
    val animationDuration = 1500

    CarbonalyserTheme {
        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Consommation (Gb)",
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded.value,
                            onExpandedChange = { expanded.value = !expanded.value }
                        ) {
                            CustomTextField(
                                value = selectedOption.value,
                                modifier = Modifier.menuAnchor(),
                                isMenuExpanded = expanded.value
                            )

                            CustomDropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false },
                                options = options,
                                onOptionSelected = { option ->
                                    selectedOption.value = option
                                    expanded.value = false
                                    when (option) {
                                        "Données envoyées" -> {
                                            chart.value?.setChart(groupedDataSent, emptyMap())
                                            chart.value?.animateXY(animationDuration, animationDuration)
                                        }
                                        "Données reçus" -> {
                                            chart.value?.setChart(emptyMap(), groupedDataReceived)
                                            chart.value?.animateXY(animationDuration, animationDuration)
                                        }
                                        "Données totales" -> {
                                            chart.value?.setChart(groupedDataSent, groupedDataReceived)
                                            chart.value?.animateXY(animationDuration, animationDuration)
                                        }
                                    }
                                },
                            )
                        }
                    }
                }


                // Graphique
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    AndroidView(
                        factory = { context ->
                            BarChart(context).apply {
                                chart.value = this
                                setChart(groupedDataSent, groupedDataReceived)
                                legend.isEnabled = false
                                getAxis(YAxis.AxisDependency.LEFT).textSize = 12f
                                animateXY(animationDuration, animationDuration)
                                xAxis.setDrawGridLines(false)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


fun BarChart.setChart(dataSent: Map<Month, Float>, dataReceived: Map<Month, Float>) {
    val barEntry: BarDataSet

    if (dataSent.isNotEmpty() && dataReceived.isNotEmpty()) {
        val mergedData = dataSent.toMutableMap()
        dataReceived.forEach { (date, value) ->
            mergedData[date] = mergedData.getOrDefault(date, 0f) + value
        }

        val barEntriesTotal = mergedData.entries.toList().mapIndexed { index, (_, value) ->
            BarEntry(index.toFloat(), value)
        }
        val maxSentAndReceived = barEntriesTotal.maxOfOrNull { it.y } ?: 0f
        barEntry = BarDataSet(barEntriesTotal, "Données envoyées (GB) et reçues (GB)").apply {
            colors = barEntriesTotal.map { getColorBasedOnData(it.y, maxSentAndReceived).toArgb() }
        }
    } else if (dataSent.isNotEmpty()) {
        val barEntriesSent = dataSent.entries.toList().mapIndexed { index, (_, value) ->
            BarEntry(index.toFloat(), value)
        }
        val maxSent = barEntriesSent.maxOfOrNull { it.y } ?: 0f
        barEntry = BarDataSet(barEntriesSent, "Données envoyées (GB)").apply {
            colors = barEntriesSent.map { getColorBasedOnData(it.y, maxSent).toArgb() }
        }
    } else {
        val barEntriesReceived = dataReceived.entries.toList().mapIndexed { index, (_, value) ->
            BarEntry(index.toFloat(), value)
        }
        val maxReceived = barEntriesReceived.maxOfOrNull { it.y } ?: 0f
        barEntry = BarDataSet(barEntriesReceived, "Données reçues (GB)").apply {
            colors = barEntriesReceived.map { getColorBasedOnData(it.y, maxReceived).toArgb() }
        }
    }

    val barData = BarData(barEntry)
    barData.barWidth = 0.4f

    data = barData
    barEntry.valueTextSize = 12f
    description.isEnabled = false

    val xAxis: XAxis = xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.valueFormatter = MonthAxisValueFormatter()
    xAxis.granularity = 1f
    xAxis.labelCount = dataSent.size

    val yAxisLeft: YAxis = axisLeft
    yAxisLeft.axisMinimum = 0f

    val yAxisRight: YAxis = axisRight
    yAxisRight.isEnabled = false

    invalidate()
}

fun sortDataByMonth(data: Map<LocalDate, Float>): Map<Month, Float> {
    val sortedData = Month.values().associateWith { 0f }.toMutableMap()
    data.forEach { (date, value) ->
        val month = date.month
        sortedData[month] = sortedData[month]!! + value
    }
    return sortedData
}

fun getColorBasedOnData(value: Float, max: Float): Color {
    val ratio = value / max
    return lerp(Color.Green, Color.Red, ratio)
}

@Preview(showBackground = false)
@Composable
fun PreviewMonthlyData() {
    val randomData = generateRandomDataForYear(2024)
    MonthlyData(randomData.first, randomData.second)
}
