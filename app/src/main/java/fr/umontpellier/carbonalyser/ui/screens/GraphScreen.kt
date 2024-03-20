package fr.umontpellier.carbonalyser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import fr.umontpellier.carbonalyser.ui.components.dataGraph.ConsumptionGraph
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme
import fr.umontpellier.carbonalyser.util.GenerateRandomData.Companion.generateRandomDataForYear

@Composable
fun GraphScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
    ) {
        val randomData = generateRandomDataForYear(2024)
        ConsumptionGraph(randomData.first, randomData.second)

    }
}

@Preview
@Composable
fun GraphScreenPreview() {
    EcoTrackerTheme {
        GraphScreen()
    }
}
