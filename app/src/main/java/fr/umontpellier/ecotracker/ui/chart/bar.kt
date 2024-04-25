package fr.umontpellier.ecotracker.ui.chart

import android.util.Log
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.model.ModelService
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.ui.component.CustomDropdownMenu
import fr.umontpellier.ecotracker.ui.component.CustomGroupButton
import fr.umontpellier.ecotracker.ui.component.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarConsumptionChart(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modelService: ModelService = koinInject(),
    modifier: Modifier = Modifier
) {
    val animationDuration = 1000
    pkgNetStatService.fetchAndCache();
    val monthConsumption = pkgNetStatService.cache.appNetStats.map { (day, perApp) -> Bytes(perApp.map { (id, data) -> data.total.value }.sum() ) }
    Log.i("ecotracker", "monthConsumption: " + monthConsumption.toString())
    val datasSet = monthConsumption.mapIndexed { index, bytes ->
        BarEntry(index.toFloat(), bytes.value.toFloat())
    }
    val dataSet = BarDataSet(datasSet, "Consommation")
    dataSet.colors = ColorTemplate.PASTEL_COLORS.plus(ColorTemplate.JOYFUL_COLORS).toMutableList()
    dataSet.valueTextColor = Color.Black.toArgb()
    dataSet.valueTextSize = 24F
    val barData = BarData(dataSet)

    // GroupButton
    var selectedIndexDataType by remember { mutableIntStateOf(2) }
    var selectedIndexDataOrigin by remember { mutableIntStateOf(2) }

    // Dropdown
    val selectedOptionDataDate = remember { mutableStateOf("Année") }
    val dataDateOption = listOf("Année", "Mois", "Jour")
    val expanded = remember { mutableStateOf(false) }

    // button Date Function
//    val selectedDateText = when (selectedOptionDataDate.value) {
//        "Année" -> currentDate.value.year.toString()
//        "Mois" -> "%s %d".format(getMonthNameInFrench(currentDate.value.month), currentDate.value.year)
//        "Jour" -> "%d %s %d".format(
//            currentDate.value.dayOfMonth,
//            getMonthNameInFrench(currentDate.value.month),
//            currentDate.value.year
//        )
//
//        else -> "Erreur"
//    }

    val goToPrevious = {
        when (selectedOptionDataDate.value) {
            "Année" -> {

            }

            "Mois" -> {

            }

            "Jour" -> {

            }

            else -> {}
        }
    }

    val goToNext = {
        when (selectedOptionDataDate.value) {
            "Année" -> {

            }

            "Mois" -> {

            }

            "Jour" -> {

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
                .fillMaxSize()
                .padding(8.dp)
        ) {

            // Title
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                                    // Mettre a joue le modele
                                    // fetchAndCache
                                    // Mettre a joue le diagramme
                                }

                                "Mois" -> {

                                }

                                "Jour" -> {

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

                            }

                            1 -> {

                            }

                            2 -> {

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

                            }

                            1 -> {

                            }

                            2 -> {

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
                            this.data = barData
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
//                    text = selectedDateText,
                    text = "2021",
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

@Preview
@Composable
fun BarChartPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        BarConsumptionChart()
    }
}