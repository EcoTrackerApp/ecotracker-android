package fr.umontpellier.carbonalyser.ui.components.dataPie


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import fr.umontpellier.carbonalyser.data.model.*
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomDropdownMenu
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomGroupButton
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomTextField
import fr.umontpellier.carbonalyser.ui.components.dataGraph.*
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme
import fr.umontpellier.carbonalyser.util.GenerateRandomDataPie
import fr.umontpellier.carbonalyser.util.MonthTraduction
import java.time.LocalDateTime
import java.time.Year


fun PieChart.setChart(dataList: List<DataPie>,
                      currentDate: LocalDateTime,
                      dataType: DataType,
                      selectedOption: String) {
    val sortedData = when (selectedOption) {
        "Année" -> sortDataByMonth(dataList, currentDate, dataType)
        "Mois" -> sortDataByDay(dataList, currentDate, dataType)
        "Jour" -> sortDataByHour(dataList, currentDate, dataType)
        else -> emptyMap()
    }
    val pieEntries = sortedData.map { (key, value) ->
        PieEntry(key.toFloat(), value)
    }

    val pieEntry = PieDataSet(pieEntries, "Données (GB)")
    val pieData = PieData(pieEntry)
    this.data = pieData

    setUsePercentValues(true)
    setDrawEntryLabels(false)
    isDrawHoleEnabled = true

    holeRadius = 50f
    transparentCircleRadius = 10f

    //val dataSet = PieDataSet(pieEntries, chartLabel)
    pieEntry.colors = ColorTemplate.COLORFUL_COLORS.toList()
    pieEntry.sliceSpace = 2f
    pieEntry.valueTextSize = 16f
    pieEntry.valueTextColor = Color.White.toArgb()
    pieEntry.valueFormatter = PercentFormatter(this)
    pieEntry.setDrawValues(true)
    legend.isEnabled = true
    //legend.orientation = Legend.LegendOrientation.VERTICAL
    //legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT

    invalidate() // refresh the chart

}
fun sortDataByMonth(
    data: List<DataPie>,
    currentDate: LocalDateTime,
    dataType: DataType
): Map<Int, Float> {
    val sortedData = mutableMapOf<Int, Float>().apply { (1..12).forEach { put(it, 0f) } }

    data.forEach { (itDate, itDataType, itName, itValue) ->
        if (
            (dataType == DataType.ALL || itDataType == dataType) &&
            itDate.year == currentDate.year
        ) {
            sortedData.compute(itDate.monthValue) { _, oldValue -> oldValue!! + itValue }
        }
    }

    return sortedData
}

fun sortDataByDay(
    data: List<DataPie>,
    currentDate: LocalDateTime,
    dataType: DataType
): Map<Int, Float> {
    val sortedData = mutableMapOf<Int, Float>().apply {
        (1..currentDate.month.length(Year.of(currentDate.year).isLeap)).forEach {
            put(
                it,
                0f
            )
        }
    }
    data.forEach { (itDate, itDataType, itName, itValue) ->
        if (
            (dataType == DataType.ALL || itDataType == dataType) &&
            itDate.year == currentDate.year &&
            itDate.month == currentDate.month
        ) {
            sortedData.compute(itDate.dayOfMonth) { _, oldValue -> oldValue!! + itValue }
        }
    }

    return sortedData
}

fun sortDataByHour(
    data: List<DataPie>,
    currentDate: LocalDateTime,
    dataType: DataType
): Map<Int, Float> {
    val sortedData = mutableMapOf<Int, Float>().apply { (0..23).forEach { put(it, 0f) } }

    data.forEach { (itDate, itDataType, itName, itValue) ->
        if (
            (dataType == DataType.ALL || itDataType == dataType) &&
            itDate.year == currentDate.year &&
            itDate.month == currentDate.month &&
            itDate.dayOfMonth == currentDate.dayOfMonth
        ) {
            sortedData.compute(itDate.hour) { _, oldValue -> oldValue!! + itValue }
        }
    }

    return sortedData
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePieChart(dataEntries: List<PieEntry>, chartLabel: String, data: List<DataPie>) {
    // Chart
    val chart = remember { mutableStateOf<PieChart?>(null) }

    // GroupButton
    var selectedIndexDataType by remember { mutableIntStateOf(2) }

    val dataTypeOption = listOf(DataType.WIFI, DataType.MOBILE_DATA, DataType.ALL)

    // Dropdown
    val selectedOptionDataDate = remember { mutableStateOf("Année") }
    val dataDateOption = listOf("Année", "Mois", "Jour")
    val expanded = remember { mutableStateOf(false) }

    // Date selection
    val currentDate = remember { mutableStateOf(LocalDateTime.now()) }

    val selectedDateText = when (selectedOptionDataDate.value) {
        "Année" -> currentDate.value.year.toString()
        "Mois" -> "%s %d".format(MonthTraduction.getMonthNameInFrench(currentDate.value.month), currentDate.value.year)
        "Jour" -> "%d %s %d".format(
            currentDate.value.dayOfMonth,
            MonthTraduction.getMonthNameInFrench(currentDate.value.month),
            currentDate.value.year
        )

        else -> "Erreur"
    }

    val goToPrevious = {
        when (selectedOptionDataDate.value) {
            "Année" -> {
                if (currentDate.value.year > 2022) {
                    currentDate.value = currentDate.value.minusYears(1)
                    chart.value?.setChart(
                        data,
                        currentDate.value,
                        dataTypeOption[selectedIndexDataType],
                        selectedOptionDataDate.value,
                    )
                } else {
                    println("Date limite atteinte")
                }
            }

            "Mois" -> {
                if (currentDate.value.year > 2022 || (currentDate.value.year == 2022 && currentDate.value.monthValue > 1)) {
                    currentDate.value = currentDate.value.minusMonths(1)
                    chart.value?.setChart(
                        data,
                        currentDate.value,
                        dataTypeOption[selectedIndexDataType],
                        selectedOptionDataDate.value,
                    )
                } else {
                    println("Date limite atteinte")
                }
            }

            "Jour" -> {
                if (currentDate.value.isAfter(LocalDateTime.of(2022, 1, 1, 0, 0))) {
                    currentDate.value = currentDate.value.minusDays(1)
                    chart.value?.setChart(
                        data,
                        currentDate.value,
                        dataTypeOption[selectedIndexDataType],
                        selectedOptionDataDate.value
                    )
                } else {
                    println("Date limite atteinte")
                }
            }

            else -> {}
        }
    }
    val goToNext = {
        when (selectedOptionDataDate.value) {
            "Année" -> {
                currentDate.value = currentDate.value.plusYears(1)
                chart.value?.setChart(
                    data,
                    currentDate.value,
                    dataTypeOption[selectedIndexDataType],
                    selectedOptionDataDate.value
                )
            }

            "Mois" -> {
                currentDate.value = currentDate.value.plusMonths(1)
                chart.value?.setChart(
                    data,
                    currentDate.value,
                    dataTypeOption[selectedIndexDataType],
                    selectedOptionDataDate.value
                )
            }

            "Jour" -> {
                currentDate.value = currentDate.value.plusDays(1)
                chart.value?.setChart(
                    data,
                    currentDate.value,
                    dataTypeOption[selectedIndexDataType],
                    selectedOptionDataDate.value
                )
            }

            else -> {}
        }
    }
    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) { // Row for the title
                Text(
                    text = "Consommation (Gb)",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)

                )
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value },
                    modifier = Modifier.height(30.dp)
                ) {
                    CustomTextField(
                        value = selectedOptionDataDate.value,
                        modifier = Modifier.menuAnchor(),
                        isMenuExpanded = expanded.value
                    )

                    CustomDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        options = dataDateOption,
                        onOptionSelected = { option ->
                            selectedOptionDataDate.value = option
                            expanded.value = false
                            when (option) {
                                "Année" -> {
                                    chart.value?.setChart(
                                        data,
                                        currentDate.value,
                                        dataTypeOption[selectedIndexDataType],
                                        option
                                    )
                                }

                                "Mois" -> {
                                    chart.value?.setChart(
                                        data,
                                        currentDate.value,
                                        dataTypeOption[selectedIndexDataType],
                                        option
                                    )
                                }

                                "Jour" -> {
                                    chart.value?.setChart(
                                        data,
                                        currentDate.value,
                                        dataTypeOption[selectedIndexDataType],
                                        option
                                    )
                                }
                            }
                        },
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                CustomGroupButton(
                    items = listOf("Wifi", "Données mobile", "Total"),
                    selectedIndex = selectedIndexDataType,
                    onSelectedIndexChanged = { index ->
                        selectedIndexDataType = index
                        when (index) {
                            0 -> {
                                chart.value?.setChart(
                                    data,
                                    currentDate.value,
                                    dataTypeOption[selectedIndexDataType],
                                    selectedOptionDataDate.value
                                )
                            }

                            1 -> {
                                chart.value?.setChart(
                                    data,
                                    currentDate.value,
                                    dataTypeOption[selectedIndexDataType],
                                    selectedOptionDataDate.value,
                                )
                            }

                            2 -> {
                                chart.value?.setChart(
                                    data,
                                    currentDate.value,
                                    dataTypeOption[selectedIndexDataType],
                                    selectedOptionDataDate.value,
                                )
                            }
                        }
                    }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { context ->
                        PieChart(context).apply {
                            chart.value = this
                            setChart(
                                data,
                                currentDate.value,
                                dataTypeOption[selectedIndexDataType],
                                selectedOptionDataDate.value
                            )


                            //legend.orientation = Legend.LegendOrientation.VERTICAL
                            //legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT

                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}
@Preview(showBackground = false)
@Composable
fun PreviewPieChart() {
    EcoTrackerTheme {
        val dataEntries = listOf(
            PieEntry(18.5f, "Twitch"),
            PieEntry(26.7f, "YouTube"),
            PieEntry(24.0f, "TikTok"),
            PieEntry(30.8f, "Chrome"),
            PieEntry(15.6f, "Snapchat"),
            PieEntry(10.2f, "WhatsApp"),
            PieEntry(20.9f, "Instagram"),
            PieEntry(22.1f, "Gmail")
        )

        val randomData = GenerateRandomDataPie.generateRandomDataForYear()
        CreatePieChart(dataEntries, "App Usage", randomData)
    }
}