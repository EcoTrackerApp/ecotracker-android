package fr.umontpellier.carbonalyser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.umontpellier.carbonalyser.ui.components.dataGraph.ConsumptionGraph
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme
import fr.umontpellier.carbonalyser.util.GenerateRandomData.Companion.generateRandomDataForYear

@Composable
fun GraphScreen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val randomData = generateRandomDataForYear()
        ConsumptionGraph(randomData, 15)
    }
}

@Preview
@Composable
fun GraphScreenPreview() {
    EcoTrackerTheme {
        GraphScreen()
    }
}
