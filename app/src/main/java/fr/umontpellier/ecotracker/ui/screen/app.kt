package fr.umontpellier.ecotracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.PackageService
import fr.umontpellier.ecotracker.ui.chart.LineConsumptionChart
import org.koin.compose.koinInject

@Composable
fun AppPage(
    config: EcoTrackerConfig = koinInject(),
    packageService: PackageService = koinInject(),
) {
    val currentApp = config.currentApp
    if (currentApp == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Sélectionnez une application depuis le menu à gauche!",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color.Black.copy(alpha = 0.4F),
                letterSpacing = (-0.5).sp
            )
        }
        return
    }
    val appConfig = config.apps.getOrPut(currentApp) { EcoTrackerConfig.AppConfig() }
    var isGreenState by remember { mutableStateOf(appConfig.isGreen) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffbe5683))
    ) {
        Text(
            packageService.appLabel(currentApp),
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            //shadowElevation = 4.dp,  // Utilisation de shadowElevation pour l'effet d'élévation
            color = Color.White
        ) {
            LineConsumptionChart(appId = currentApp)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            //shadowElevation = 4.dp,  // Utilisation de shadowElevation pour l'effet d'élévation
            color = Color.White
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Énergie verte",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 1.dp),
                    letterSpacing = (-0.5).sp
                )
                Checkbox(checked = isGreenState, onCheckedChange = {
                    appConfig.isGreen = it
                    isGreenState = it
                })
            }
        }
    }
}