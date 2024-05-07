package fr.umontpellier.ecotracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.ui.chart.PieConsumptionChart
import fr.umontpellier.ecotracker.ui.component.AppColumn
import org.koin.compose.KoinApplication

@Composable
fun DetailsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green.copy(alpha = .45f))
    ) {
        Text(
            "Applications",
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp, start = 16.dp)
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
            PieConsumptionChart()
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                "Toutes les applications: ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AppColumn(limit = Int.MAX_VALUE)
        }
    }
}


@Preview
@Composable
fun DetailsPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        DetailsScreen()
    }
}