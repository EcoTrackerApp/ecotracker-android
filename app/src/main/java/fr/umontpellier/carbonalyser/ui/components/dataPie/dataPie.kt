package fr.umontpellier.carbonalyser.ui.components.dataPie


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
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
import fr.umontpellier.carbonalyser.data.model.DataColors
import fr.umontpellier.carbonalyser.util.CustomPercentFormatter

fun PieChart.setChart(dataList: List<DataPie>, currentDate: LocalDateTime, dataType: DataType, selectedOption: String) {
    val preSortedData = when (selectedOption) {
        "Année" -> sortDataByMonth(dataList, currentDate, dataType)
        "Mois" -> sortDataByDay(dataList, currentDate, dataType)
        "Jour" -> sortDataByHour(dataList, currentDate, dataType)
        else -> dataList // Si aucune des options ci-dessus, on ne change pas les données
    }

    // Tri et regroupement des données pré-triées par `dataName`, puis sommation des valeurs pour chaque `dataName`.
    val sortedAndSummedData = preSortedData
        .sortedBy { it.dataName } // Tri par `dataName`
        .groupBy { it.dataName }
        .map { (name, list) ->
            PieEntry(
                list.sumOf { it.value.toDouble() }.toFloat(), // Somme des valeurs, convertie en Float
                name.toString() // Utilisation du nom d'application comme label
            )
        }
    // Création de PieDataSet avec les données triées et sommées.
    val pieDataSet = PieDataSet(sortedAndSummedData, "Données par Application").apply {
        colors = listOf(
            DataColors.PastelRed.toArgb(),
            DataColors.PastelGreen.toArgb(),
            DataColors.PastelBlue.toArgb(),
            DataColors.PastelPurple.toArgb(),
            DataColors.PastelOrange.toArgb(),
            DataColors.PastelPink.toArgb(),
            DataColors.PastelBrown.toArgb(),
            DataColors.PastelCyan.toArgb(),
            DataColors.PastelLime.toArgb(),
            DataColors.PastelMagenta.toArgb(),
            DataColors.PastelTeal.toArgb(),
            DataColors.PastelLavender.toArgb(),
            DataColors.PastelMaroon.toArgb(),
            DataColors.PastelOlive.toArgb(),
            DataColors.PastelCoral.toArgb(),
            DataColors.PastelGold.toArgb(),
            DataColors.PastelSkyBlue.toArgb()
        )
        sliceSpace = 2f
        valueTextSize = 17f
        valueTextColor = Color.White.toArgb()
        valueFormatter = CustomPercentFormatter(this@setChart)
    }

    // Configuration supplémentaire du diagramme
    this.data = PieData(pieDataSet)
    setUsePercentValues(true)
    setDrawEntryLabels(false)
    isDrawHoleEnabled = true
    holeRadius = 40f
    transparentCircleRadius = 10f
    legend.isEnabled = true
    description.isEnabled = false

    invalidate() // Rafraîchir le diagramme
}

fun sortDataByMonth(
    data: List<DataPie>,
    currentDate: LocalDateTime,
    dataType: DataType
): List<DataPie> {
    return data.filter { (dataType == DataType.ALL || it.dataType == dataType) && it.date.year == currentDate.year }
}

fun sortDataByDay(
    data: List<DataPie>,
    currentDate: LocalDateTime,
    dataType: DataType
): List<DataPie> {
    return data.filter { (dataType == DataType.ALL || it.dataType == dataType) && it.date.year == currentDate.year && it.date.month == currentDate.month }
}

fun sortDataByHour(
    data: List<DataPie>,
    currentDate: LocalDateTime,
    dataType: DataType
): List<DataPie> {
    return data.filter { (dataType == DataType.ALL || it.dataType == dataType) && it.date.year == currentDate.year && it.date.month == currentDate.month && it.date.dayOfMonth == currentDate.dayOfMonth }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePieChart(data: List<DataPie>) {
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
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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

                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { goToPrevious() },
                    modifier = Modifier.weight(0.2f)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Précédent")
                }
                Text(
                    text = selectedDateText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(
                    onClick = { goToNext() },
                    modifier = Modifier.weight(0.2f)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Suivant")
                }
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

        val randomData = GenerateRandomDataPie.generateRandomDataForYears()
        CreatePieChart(randomData)
    }
}