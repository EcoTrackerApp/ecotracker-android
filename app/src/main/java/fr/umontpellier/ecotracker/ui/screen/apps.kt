package fr.umontpellier.ecotracker.ui.screen

import PieConsumptionChart
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.ecotracker.ui.component.AppColumn

@Composable
fun Apps() {
    val selectedApp = remember { mutableStateOf<Float?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue.copy(alpha = 0.5f))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                "Applications",
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                color = Color.White,
                modifier = Modifier.padding(top = 10.dp, start = 16.dp)
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(horizontal = 12.dp, vertical = 2.dp),
            shape = RoundedCornerShape(10.dp),
            //shadowElevation = 4.dp,  // Utilisation de shadowElevation pour l'effet d'élévation
            color = Color.White
        ) {
            PieConsumptionChart(applimit = 10, selectedAppCons = selectedApp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                "Details ",
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
                .padding(bottom = 16.dp)
                .weight(1f)
        ) {
            AppColumn(
                applimit = 10,
                buttonSize = 25,
                spaceBtwnItems = 1,
                selectedAppCons = selectedApp
            )
        }
    }
}

