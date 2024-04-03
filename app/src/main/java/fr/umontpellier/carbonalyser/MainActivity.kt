package fr.umontpellier.carbonalyser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.umontpellier.carbonalyser.ui.components.header.Header
import fr.umontpellier.carbonalyser.ui.screens.ApplicationScreen
import fr.umontpellier.carbonalyser.ui.screens.DashboardScreen
import fr.umontpellier.carbonalyser.ui.screens.GraphScreen
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoTrackerTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        Header(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboardScreen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dashboardScreen") {
                            DashboardScreen(
                                globalEmission = 128.23,
                                lastVisitIncrease = 9.0,
                                recordingDuration = 4.0,
                                consumption = 124.0,
                                downloadedData = 12.6,
                                uploadedData = 6.2,
                                carEquivalent = 12.0,
                                numberOfCharges = 64.0
                            )
                        }
                        composable("graphsScreen") {
                            GraphScreen()
                        }
                        composable("applicationScreen") {
                            ApplicationScreen()
                        }
                    }
                }
            }
        }
    }
}