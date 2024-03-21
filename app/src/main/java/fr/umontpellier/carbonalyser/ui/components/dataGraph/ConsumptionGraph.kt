package fr.umontpellier.carbonalyser.ui.components.dataGraph


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomGroupButton
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomTextField
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme
import fr.umontpellier.carbonalyser.util.GenerateRandomData
import fr.umontpellier.carbonalyser.util.GenerateRandomData.Companion.generateRandomDataForYear
import fr.umontpellier.carbonalyser.util.MonthAxisValueFormatter
import java.time.LocalDateTime
import java.time.Month
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumptionGraph(dataSent: Map<LocalDateTime, Float>, dataReceived: Map<LocalDateTime, Float>) {
    // GroupButton
    var selectedIndexNetwork by remember { mutableIntStateOf(0) }
    var selectedIndexData by remember { mutableIntStateOf(0) }

    // Dropdown
    val selectedOption = remember { mutableStateOf("Année") }
    val options = listOf("Année", "Mois", "Jour")
    val expanded = remember { mutableStateOf(false) }

    // Data
    val groupedDataSent = sortDataByMonth(dataSent)
    val groupedDataReceived = sortDataByMonth(dataReceived)

    // Chart
    val chart = remember { mutableStateOf<BarChart?>(null) }
    val animationDuration = 1500

    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp)
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
                        onExpandedChange = { expanded.value = !expanded.value },
                        modifier = Modifier.height(30.dp)
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
                                    "Année" -> {
                                        chart.value?.setChart(groupedDataSent, emptyMap())
                                        chart.value?.animateXY(animationDuration, animationDuration)
                                    }

                                    "Mois" -> {
                                        chart.value?.setChart(emptyMap(), groupedDataReceived)
                                        chart.value?.animateXY(animationDuration, animationDuration)
                                    }

                                    "Jour" -> {
                                        chart.value?.setChart(groupedDataSent, groupedDataReceived)
                                        chart.value?.animateXY(animationDuration, animationDuration)
                                    }
                                }
                            },
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CustomGroupButton(
                    items = listOf("Wifi", "Données mobile", "Total"),
                    selectedIndex = selectedIndexNetwork,
                    onSelectedIndexChanged = { index ->
                        selectedIndexNetwork = index
                        when (index) {
                            0 -> {
                                chart.value?.setChart(groupedDataSent, emptyMap())
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }

                            1 -> {
                                chart.value?.setChart(emptyMap(), groupedDataReceived)
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }

                            2 -> {
                                chart.value?.setChart(groupedDataSent, groupedDataReceived)
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CustomGroupButton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CustomGroupButton(
                    items = listOf("Envoyées", "Reçus", "Totales"),
                    selectedIndex = selectedIndexData,
                    onSelectedIndexChanged = { index ->
                        selectedIndexData = index
                        when (index) {
                            0 -> {
                                chart.value?.setChart(groupedDataSent, emptyMap())
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }

                            1 -> {
                                chart.value?.setChart(emptyMap(), groupedDataReceived)
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }

                            2 -> {
                                chart.value?.setChart(groupedDataSent, groupedDataReceived)
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }
                        }
                    }
                )
            }

            // BarChart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { context ->
                        BarChart(context).apply {
                            chart.value = this
                            setChart(groupedDataSent, groupedDataReceived)
                            legend.isEnabled = false
                            getAxis(YAxis.AxisDependency.LEFT).textSize = 12f
                            //animateXY(animationDuration, animationDuration)
                            xAxis.setDrawGridLines(false)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { /* Action pour aller au mois précédent */ }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Mois précédent")
                }

                Text(
                    text = "Nom du mois sélectionné",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                IconButton(
                    onClick = { /* Action pour aller au mois suivant */ }
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Mois suivant")
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
    barData.barWidth = 0.45f

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

fun sortDataByMonth(data: Map<LocalDateTime, Float>): Map<Month, Float> {
    val sortedData = Month.values().associateWith { 0f }.toMutableMap()
    data.forEach { (date, value) ->
        val month = date.month
        sortedData[month] = sortedData[month]!! + value
    }
    return sortedData
}

fun sortDataByDayForAMouth(data: Map<LocalDateTime, Float>, month: Month): Map<Int, Float> {
    val sortedData = (1..month.length(false)).associateWith { 0f }.toMutableMap()
    data.forEach { (date, value) ->
        if (date.month == month) {
            sortedData[date.dayOfMonth] = value
        }
    }
    return sortedData
}

fun sortDataByHourForADay(data: Map<LocalDateTime, Float>, day: Int): Map<Int, Float> {
    val sortedData = (0..23).associateWith { 0f }.toMutableMap()
    data.forEach { (date, value) ->
        if (date.dayOfMonth == day) {
            sortedData[date.hour] = value
        }
    }
    return sortedData
}


fun getColorBasedOnData(value: Float, max: Float): Color {
    val ratio = value / max
    return lerp(Color.Green, Color.Red, ratio)
}

@Preview(showBackground = false)
@Composable
fun PreviewConsumptionGraph() {
    EcoTrackerTheme {
        val randomData = generateRandomDataForYear(2024)
        ConsumptionGraph(randomData.first, randomData.second)
    }
}
