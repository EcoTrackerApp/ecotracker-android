package fr.umontpellier.carbonalyser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.umontpellier.carbonalyser.ui.components.dataGraph.MonthlyData
import fr.umontpellier.carbonalyser.ui.components.dataGraph.generateRandomDataForYear

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val randomData = generateRandomDataForYear(2024)
            MonthlyData(randomData.first, randomData.second)
        }
    }
}
