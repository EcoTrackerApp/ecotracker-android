package fr.umontpellier.carbonalyser.ui.components.graph


import android.view.GestureDetector
import android.view.MotionEvent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
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
import fr.umontpellier.carbonalyser.model.Model
import fr.umontpellier.carbonalyser.model.ModelResult
import fr.umontpellier.carbonalyser.ui.components.custom.*
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme
import fr.umontpellier.carbonalyser.util.DayAxisValueFormatter
import fr.umontpellier.carbonalyser.util.GenerateRandomData.Companion.generateRandomDataForYear
import fr.umontpellier.carbonalyser.util.MonthAxisValueFormatter
import fr.umontpellier.carbonalyser.util.MonthTraduction.Companion.getMonthNameInFrench
import java.time.LocalDateTime
import java.time.Year
import kotlin.math.pow

enum class DataType {
    WIFI,
    MOBILE,
    ALL
}

enum class DataOrigin {
    SEND,
    RECEIVED,
    ALL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumptionGraph(data: List<ModelResult>, dataDayLimit: Int) {
    // Chart
    val chart = remember { mutableStateOf<BarChart?>(null) }
    val animationDuration = 1000

    // GroupButton
    var selectedIndexDataType by remember { mutableIntStateOf(2) }
    var selectedIndexDataOrigin by remember { mutableIntStateOf(2) }
    val dataTypeOption = listOf(DataType.WIFI, DataType.MOBILE, DataType.ALL)
    val dataOriginOption = listOf(DataOrigin.SEND, DataOrigin.RECEIVED, DataOrigin.ALL)

    // Dropdown
    val selectedOptionDataDate = remember { mutableStateOf("Année") }
    val dataDateOption = listOf("Année", "Mois", "Jour")
    val expanded = remember { mutableStateOf(false) }

    // Date selection
    val currentDate = remember { mutableStateOf(LocalDateTime.now()) }

    val selectedDateText = when (selectedOptionDataDate.value) {
        "Année" -> currentDate.value.year.toString()
        "Mois" -> "%s %d".format(getMonthNameInFrench(currentDate.value.month), currentDate.value.year)
        "Jour" -> "%d %s %d".format(
            currentDate.value.dayOfMonth,
            getMonthNameInFrench(currentDate.value.month),
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
                        dataOriginOption[selectedIndexDataOrigin],
                        selectedOptionDataDate.value,
                        dataDayLimit
                    )
                    chart.value?.animateXY(animationDuration, animationDuration)
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
                        dataOriginOption[selectedIndexDataOrigin],
                        selectedOptionDataDate.value,
                        dataDayLimit
                    )
                    chart.value?.animateXY(animationDuration, animationDuration)
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
                        dataOriginOption[selectedIndexDataOrigin],
                        selectedOptionDataDate.value,
                        dataDayLimit
                    )
                    chart.value?.animateXY(animationDuration, animationDuration)
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
                    dataOriginOption[selectedIndexDataOrigin],
                    selectedOptionDataDate.value,
                    dataDayLimit
                )
                chart.value?.animateXY(animationDuration, animationDuration)
            }

            "Mois" -> {
                currentDate.value = currentDate.value.plusMonths(1)
                chart.value?.setChart(
                    data,
                    currentDate.value,
                    dataTypeOption[selectedIndexDataType],
                    dataOriginOption[selectedIndexDataOrigin],
                    selectedOptionDataDate.value,
                    dataDayLimit
                )
                chart.value?.animateXY(animationDuration, animationDuration)
            }

            "Jour" -> {
                currentDate.value = currentDate.value.plusDays(1)
                chart.value?.setChart(
                    data,
                    currentDate.value,
                    dataTypeOption[selectedIndexDataType],
                    dataOriginOption[selectedIndexDataOrigin],
                    selectedOptionDataDate.value,
                    dataDayLimit
                )
                chart.value?.animateXY(animationDuration, animationDuration)
            }

            else -> {}
        }
    }

    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {

            // Title
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consommation de données",
                    fontSize = 16.sp,
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
                                        dataOriginOption[selectedIndexDataOrigin],
                                        option,
                                        dataDayLimit
                                    )
                                    chart.value?.animateXY(animationDuration, animationDuration)
                                }

                                "Mois" -> {
                                    chart.value?.setChart(
                                        data,
                                        currentDate.value,
                                        dataTypeOption[selectedIndexDataType],
                                        dataOriginOption[selectedIndexDataOrigin],
                                        option,
                                        dataDayLimit
                                    )
                                    chart.value?.animateXY(animationDuration, animationDuration)
                                }

                                "Jour" -> {
                                    chart.value?.setChart(
                                        data,
                                        currentDate.value,
                                        dataTypeOption[selectedIndexDataType],
                                        dataOriginOption[selectedIndexDataOrigin],
                                        option,
                                        dataDayLimit
                                    )
                                    chart.value?.animateXY(animationDuration, animationDuration)
                                }
                            }
                        },
                    )
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                                    dataOriginOption[selectedIndexDataOrigin],
                                    selectedOptionDataDate.value,
                                    dataDayLimit
                                )
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }

                            1 -> {
                                chart.value?.setChart(
                                    data,
                                    currentDate.value,
                                    dataTypeOption[selectedIndexDataType],
                                    dataOriginOption[selectedIndexDataOrigin],
                                    selectedOptionDataDate.value,
                                    dataDayLimit
                                )
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }

                            2 -> {
                                chart.value?.setChart(
                                    data,
                                    currentDate.value,
                                    dataTypeOption[selectedIndexDataType],
                                    dataOriginOption[selectedIndexDataOrigin],
                                    selectedOptionDataDate.value,
                                    dataDayLimit
                                )
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
                    selectedIndex = selectedIndexDataOrigin,
                    onSelectedIndexChanged = { index ->
                        selectedIndexDataOrigin = index
                        when (index) {
                            0 -> {
                                chart.value?.setChart(
                                    data,
                                    currentDate.value,
                                    dataTypeOption[selectedIndexDataType],
                                    dataOriginOption[selectedIndexDataOrigin],
                                    selectedOptionDataDate.value,
                                    dataDayLimit
                                )
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }

                            1 -> {
                                chart.value?.setChart(
                                    data,
                                    currentDate.value,
                                    dataTypeOption[selectedIndexDataType],
                                    dataOriginOption[selectedIndexDataOrigin],
                                    selectedOptionDataDate.value,
                                    dataDayLimit
                                )
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }

                            2 -> {
                                chart.value?.setChart(
                                    data,
                                    currentDate.value,
                                    dataTypeOption[selectedIndexDataType],
                                    dataOriginOption[selectedIndexDataOrigin],
                                    selectedOptionDataDate.value,
                                    dataDayLimit
                                )
                                chart.value?.animateXY(animationDuration, animationDuration)
                            }
                        }
                    }
                )
            }

            // BarChart
            val gestureDetector =
                GestureDetector(LocalContext.current, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onFling(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        velocityX: Float,
                        velocityY: Float
                    ): Boolean {
                        if (e1 != null) {
                            if (e1.x - e2.x > 0) {
                                goToNext()
                            } else if (e1.x - e2.x < 0) {
                                goToPrevious()
                            }
                        }
                        return super.onFling(e1, e2, velocityX, velocityY)
                    }
                })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { context ->
                        BarChart(context).apply {
                            chart.value = this
                            setChart(
                                data,
                                currentDate.value,
                                dataTypeOption[selectedIndexDataType],
                                dataOriginOption[selectedIndexDataOrigin],
                                selectedOptionDataDate.value,
                                dataDayLimit
                            )
                            animateXY(animationDuration, animationDuration)
                            setOnTouchListener { _, event ->
                                if (event.action == MotionEvent.ACTION_UP) {
                                    performClick()
                                }
                                gestureDetector.onTouchEvent(event)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }


            Text(
                text = "( Avec une limite de $dataDayLimit Gb par mois )",
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                color = Color.Gray,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth()
            )

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

fun getColorBasedOnData(
    value: Float,
    currentDate: LocalDateTime,
    selectedOptionDataDate: String,
    dataDayLimit: Int
): Color {
    val daysInMonth = currentDate.month.length(Year.of(currentDate.year).isLeap)// Nombre de jours dans le mois actuel

    val adjustedLimit = when (selectedOptionDataDate) {
        "Jour" -> dataDayLimit / daysInMonth.toFloat() // Limite quotidienne divisée par le nombre de jours dans le mois
        "Mois" -> dataDayLimit // Limite mensuelle
        "Année" -> (dataDayLimit * daysInMonth).toFloat() // Limite annuelle multipliée par le nombre de jours dans le mois
        else -> 1f // Valeur par défaut pour éviter la division par zéro
    }

    val ratio = value / adjustedLimit.toFloat() // Calcul du ratio en fonction de la limite ajustée

    // Appliquer une transformation non linéaire au ratio pour réduire l'impact des valeurs plus petites
    val adjustedRatio = ratio.pow(2) // Vous pouvez ajuster l'exposant selon vos besoins

    // Assure que le ratio ajusté est compris entre 0 et 1
    val clampedAdjustedRatio = adjustedRatio.coerceIn(0f, 1f)

    // Détermine la couleur en utilisant une interpolation linéaire entre le vert et le rouge
    return lerp(Color.Green, Color.Red, clampedAdjustedRatio)
}


fun BarChart.setChart(
    dataList: List<ModelResult>,
    currentDate: LocalDateTime,
    dataType: DataType,
    dataOrigin: DataOrigin,
    selectedOption: String,
    dataDayLimit: Int
) {
    val sortedData = when (selectedOption) {
        "Année" -> sortDataByMonth(dataList, currentDate, dataType, dataOrigin)
        "Mois" -> sortDataByDay(dataList, currentDate, dataType, dataOrigin)
        "Jour" -> sortDataByHour(dataList, currentDate, dataType, dataOrigin)
        else -> emptyMap()
    }


    val barEntries = sortedData.map { (key, value) ->
        BarEntry(key.toFloat(), value)
    }

    val barEntry = BarDataSet(barEntries, "Données").apply {
        colors = barEntries.map { getColorBasedOnData(it.y, currentDate, selectedOption, dataDayLimit).toArgb() }
        valueFormatter = CustomValueFormatter()
    }

    val barData = BarData(barEntry)
    data = barData
    barData.barWidth = 0.45f
    barEntry.valueTextSize = 10f
    description.isEnabled = false

    val xAxis: XAxis = xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM

    when (selectedOption) {
        "Année" -> {
            xAxis.valueFormatter = MonthAxisValueFormatter()
        }

        "Mois" -> {
            xAxis.valueFormatter = DayAxisValueFormatter()
        }

        "Jour" -> {
            xAxis.valueFormatter = null
        }
    }

    xAxis.granularity = 1f
    xAxis.labelCount = dataList.size

    val yAxisLeft: YAxis = axisLeft
    yAxisLeft.axisMinimum = 0f
    yAxisLeft.valueFormatter = CustomYAxisFormatter()

    val yAxisRight: YAxis = axisRight
    yAxisRight.isEnabled = false

    isDragEnabled = false
    isScaleXEnabled = false
    isScaleYEnabled = false
    !isPinchZoomEnabled
    isDoubleTapToZoomEnabled = false

    legend.isEnabled = false
    getAxis(YAxis.AxisDependency.LEFT).textSize = 10f
    xAxis.setDrawGridLines(false)

    invalidate()
}


fun sortDataByMonth(
    data: List<ModelResult>,
    currentDate: LocalDateTime,
    dataType: DataType,
    dataOrigin: DataOrigin
): Map<Int, Float> {
    val sortedData = mutableMapOf<Int, Float>().apply { (1..12).forEach { put(it, 0f) } }

    /**data.forEach { (itDate, itDataType, itOrigin, itValue) ->
    if ((dataOrigin == DataOrigin.ALL || itOrigin == dataOrigin) &&
    (dataType == DataType.ALL || itDataType == dataType) &&
    itDate.year == currentDate.year
    ) {
    sortedData.compute(itDate.monthValue) { _, oldValue -> oldValue!! + itValue }
    }
    }**/

    return sortedData
}

fun sortDataByDay(
    data: List<ModelResult>,
    currentDate: LocalDateTime,
    dataType: DataType,
    dataOrigin: DataOrigin
): Map<Int, Float> {
    val sortedData = mutableMapOf<Int, Float>().apply {
        (1..currentDate.month.length(Year.of(currentDate.year).isLeap)).forEach {
            put(
                it,
                0f
            )
        }
    }

    /**data.forEach { (pkgNetStats, received, sent) ->
    if ((dataOrigin == DataOrigin.ALL || pkgNetStats.connectivity.name == dataType.name) &&
    (dataType == DataType.ALL || itDataType == dataType) &&
    itDate.year == currentDate.year &&
    itDate.month == currentDate.month
    ) {
    sortedData.compute(itDate.dayOfMonth) { _, oldValue -> oldValue!! + itValue }
    }
    }**/

    return sortedData
}

fun sortDataByHour(
    data: List<ModelResult>,
    currentDate: LocalDateTime,
    dataType: DataType,
    dataOrigin: DataOrigin
): Map<Int, Float> {
    val sortedData = mutableMapOf<Int, Float>().apply { (0..23).forEach { put(it, 0f) } }

    /**data.forEach { (itDate, itDataType, itOrigin, itValue) ->
    if ((dataOrigin == DataOrigin.ALL || itOrigin == dataOrigin) &&
    (dataType == DataType.ALL || itDataType == dataType) &&
    itDate.year == currentDate.year &&
    itDate.month == currentDate.month &&
    itDate.dayOfMonth == currentDate.dayOfMonth
    ) {
    sortedData.compute(itDate.hour) { _, oldValue -> oldValue!! + itValue }
    }
    }**/

    return sortedData
}

@Preview(showBackground = false)
@Composable
fun PreviewConsumptionGraph() {
    EcoTrackerTheme {
        val randomData = generateRandomDataForYear()
        ConsumptionGraph(randomData, 15)
    }
}