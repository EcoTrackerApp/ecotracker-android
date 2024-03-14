package fr.umontpellier.carbonalyser.ui.components.mainTiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme

@Composable
fun EmissionSummaryTile(globalEmission: Double, lastVisitIncrease: Double) {
    var blueColor = 0xFF1a74ef
    var fontTitleBlueColor = 0xFFc2e9f9
    var fontBlueColor = 0xFF7cd3fb

    CarbonalyserTheme {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp, bottom = 25.dp, start = 16.dp, end = 16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(blueColor)),
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Emission globale",
                            color = Color(fontTitleBlueColor),
                            fontSize = 14.sp
                        )

                        Text(
                            text = "Voir plus",
                            color = Color(fontBlueColor),
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${globalEmission} kgCO2e",
                            color = Color.White,
                            fontSize = 26.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ $lastVisitIncrease%",
                            color = Color(fontBlueColor),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

}
}

@Preview
@Composable
fun EmissionSummaryTilePreview() {
    EmissionSummaryTile(37.28, 2.9)
}
