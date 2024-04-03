package fr.umontpellier.carbonalyser.ui.components.dataPie


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import fr.umontpellier.carbonalyser.data.model.*
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomDropdownMenu
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomGroupButton
import fr.umontpellier.carbonalyser.ui.components.customComponents.CustomTextField
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme
import fr.umontpellier.carbonalyser.util.GenerateRandomDataPie
import fr.umontpellier.carbonalyser.util.MonthTraduction
import java.time.LocalDateTime
import fr.umontpellier.carbonalyser.data.model.DataColors
import fr.umontpellier.carbonalyser.util.CustomPercentFormatter
import kotlin.math.roundToInt

@Composable
fun LegendItem(label: String, value: Float, total: Float, color: Color) {
    val percentage = (value / total) * 100
    val roundedValue = "%.1f".format(value)
    val formattedPercentage = "%.1f".format(percentage)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(13.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: $roundedValue Gb (${formattedPercentage}%)",
            fontSize = 14.sp
        )
    }
}

fun trimData(
    data: List<DataPie>,
    currentDate: LocalDateTime,
    dataType: DataType,
    selectedOption: String
): List<PieEntry> {
    val preSortedData = when (selectedOption) {
        "Année" -> sortDataByMonth(data, currentDate, dataType)
        "Mois" -> sortDataByDay(data, currentDate, dataType)
        "Jour" -> sortDataByHour(data, currentDate, dataType)
        else -> data // Si aucune des options ci-dessus, on ne change pas les données
    }

    //Tri par valeur d'applications pour chaque `dataName`


    // Tri et regroupement des données pré-triées par `dataName`, puis sommation des valeurs pour chaque `dataName`.
    var sortedAndSummedData = preSortedData
        .sortedBy { it.dataName } // Tri par `dataName`
        .groupBy { it.dataName }
        .map { (name, list) ->
            PieEntry(
                list.sumOf { it.value.toDouble() }.toFloat(), // Somme des valeurs, convertie en Float
                name.toString() // Utilisation du nom d'application comme label
            )
        }


    // Si DataType est "ALL", regrouper les données par nom d'application, indépendamment du type de données
    if (dataType == DataType.ALL) {
        sortedAndSummedData = sortedAndSummedData
            .groupBy { it.label }
            .map { (name, list) ->
                PieEntry(
                    list.sumByDouble { it.value.toDouble() }.toFloat(), // Somme des valeurs, convertie en Float
                    name // Utilisation du nom d'application comme label
                )
            }
    }


    // Trier les données par valeur
    sortedAndSummedData = sortedAndSummedData.sortedByDescending { it.value }

    return sortedAndSummedData
}

fun PieChart.setChart(sortedAndSummedData: List<PieEntry>) {


    // Associer des couleurs spécifiques à chaque application
    val colorMap: Map<String, Color> = sortedAndSummedData.associate { entry ->
        entry.label to DataColors.getColor(sortedAndSummedData.indexOf(entry))
    }

    // Création de la liste de couleurs en utilisant colorMap
    val colors = sortedAndSummedData.map { entry ->
        colorMap[entry.label]?.toArgb() ?: DataColors.getDefaultColor().toArgb()
    }


    // Convertir la liste d'entiers en MutableList<Int>
    val mutableColors: MutableList<Int> = colors.toMutableList()


    // Création de PieDataSet avec les données triées et sommées.
    val pieDataSet = PieDataSet(sortedAndSummedData, "Données par Application").apply {

        this.colors = mutableColors

        // Utilisation des couleurs spécifiques


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
    legend.isEnabled = false
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

    //var sortedDatas = remember { mutableStateOf<PieEntry?>(null) }

    var sortedDatas = remember { mutableStateOf<List<PieEntry>>(emptyList()) }

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
                    sortedDatas.value = trimData(
                        data,
                        currentDate.value,
                        dataTypeOption[selectedIndexDataType],
                        selectedOptionDataDate.value
                    )
                    chart.value?.setChart(sortedDatas.value)
                    //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
                } else {
                    println("Date limite atteinte")
                }
            }

            "Mois" -> {
                if (currentDate.value.year > 2022 || (currentDate.value.year == 2022 && currentDate.value.monthValue > 1)) {
                    currentDate.value = currentDate.value.minusMonths(1)
                    sortedDatas.value = trimData(
                        data,
                        currentDate.value,
                        dataTypeOption[selectedIndexDataType],
                        selectedOptionDataDate.value
                    )
                    chart.value?.setChart(sortedDatas.value)
                    //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
                } else {
                    println("Date limite atteinte")
                }
            }

            "Jour" -> {
                if (currentDate.value.isAfter(LocalDateTime.of(2022, 1, 1, 0, 0))) {
                    currentDate.value = currentDate.value.minusDays(1)
                    sortedDatas.value = trimData(
                        data,
                        currentDate.value,
                        dataTypeOption[selectedIndexDataType],
                        selectedOptionDataDate.value
                    )
                    chart.value?.setChart(sortedDatas.value)
                    //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
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
                sortedDatas.value = trimData(
                    data,
                    currentDate.value,
                    dataTypeOption[selectedIndexDataType],
                    selectedOptionDataDate.value
                )
                chart.value?.setChart(sortedDatas.value)
                //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)

            }

            "Mois" -> {
                currentDate.value = currentDate.value.plusMonths(1)
                sortedDatas.value = trimData(
                    data,
                    currentDate.value,
                    dataTypeOption[selectedIndexDataType],
                    selectedOptionDataDate.value
                )
                chart.value?.setChart(sortedDatas.value)
                //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
            }

            "Jour" -> {
                currentDate.value = currentDate.value.plusDays(1)
                sortedDatas.value = trimData(
                    data,
                    currentDate.value,
                    dataTypeOption[selectedIndexDataType],
                    selectedOptionDataDate.value
                )
                chart.value?.setChart(sortedDatas.value)
                //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
            }

            else -> {}
        }
    }


    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.85f)
                .padding(
                    top = 3.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ) // Réduisez la valeur de padding en haut
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
                                        sortedDatas.value = trimData(
                                            data,
                                            currentDate.value,
                                            dataTypeOption[selectedIndexDataType],
                                            selectedOptionDataDate.value
                                        )
                                        chart.value?.setChart(sortedDatas.value)

                                        //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
                                    }

                                    "Mois" -> {
                                        sortedDatas.value = trimData(
                                            data,
                                            currentDate.value,
                                            dataTypeOption[selectedIndexDataType],
                                            selectedOptionDataDate.value
                                        )
                                        chart.value?.setChart(sortedDatas.value)
                                        //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
                                    }

                                    "Jour" -> {
                                        sortedDatas.value = trimData(
                                            data,
                                            currentDate.value,
                                            dataTypeOption[selectedIndexDataType],
                                            selectedOptionDataDate.value
                                        )
                                        chart.value?.setChart(sortedDatas.value)
                                        //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
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
                                    sortedDatas.value = trimData(
                                        data,
                                        currentDate.value,
                                        dataTypeOption[selectedIndexDataType],
                                        selectedOptionDataDate.value
                                    )
                                    chart.value?.setChart(sortedDatas.value)
                                    //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
                                }

                                1 -> {
                                    sortedDatas.value = trimData(
                                        data,
                                        currentDate.value,
                                        dataTypeOption[selectedIndexDataType],
                                        selectedOptionDataDate.value
                                    )
                                    chart.value?.setChart(sortedDatas.value)
                                    //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
                                }

                                2 -> {
                                    sortedDatas.value = trimData(
                                        data,
                                        currentDate.value,
                                        dataTypeOption[selectedIndexDataType],
                                        selectedOptionDataDate.value
                                    )
                                    chart.value?.setChart(sortedDatas.value)
                                    //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)
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
                                sortedDatas.value = trimData(
                                    data,
                                    currentDate.value,
                                    dataTypeOption[selectedIndexDataType],
                                    selectedOptionDataDate.value
                                )
                                chart.value?.setChart(sortedDatas.value)
                                //sortedDatas = updateLegend(data, selectedOptionDataDate.value, dataTypeOption[selectedIndexDataType], currentDate.value)

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

        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 3.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ) // Réduisez la valeur de padding en haut
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Légende",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )


                // Ajout de la légende pour chaque nom d'application dans le graphe
                sortedDatas.value.let { sortedData ->

                    val total = sortedData.sumOf { it.value.toDouble() }.toFloat()
                    sortedData.forEach { entry ->
                        LegendItem(entry.label, entry.value, total, DataColors.getColor(sortedData.indexOf(entry)))
                    }
                }


            }
        }
    }

}


@Preview(showBackground = false)
@Composable
fun PreviewPieChart() {
    EcoTrackerTheme {
        val randomData = GenerateRandomDataPie.generateRandomDataForYears()
        CreatePieChart(randomData)

    }
}
