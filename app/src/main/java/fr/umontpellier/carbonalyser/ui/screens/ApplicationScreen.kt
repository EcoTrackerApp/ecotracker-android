package fr.umontpellier.carbonalyser.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.umontpellier.carbonalyser.ui.components.dataPie.CreatePieChart
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme
import fr.umontpellier.carbonalyser.util.GenerateRandomDataPie

@Composable
fun ApplicationScreen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val randomData = GenerateRandomDataPie.generateRandomDataForYears()
        CreatePieChart(randomData)
    }
}

@Preview
@Composable
fun ApplicationScreenPreview() {
    EcoTrackerTheme {
        ApplicationScreen()
    }
}
