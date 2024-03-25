package fr.umontpellier.carbonalyser.ui.components.tiles

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme
import fr.umontpellier.carbonalyser.util.format

@Composable
fun ValueTile(
    valueName: String,
    globalEmission: Double,
    lastVisitIncrease: Double? = null,
    backGroundColor: Color,
    showMoreText: Boolean = true
) {
    val fontTitleBlueColor = 0xFFc2e9f9
    val fontBlueColor = 0xFF7cd3fb

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
        colors = CardDefaults.cardColors(containerColor = backGroundColor),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
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
                        text = valueName,
                        color = Color(fontTitleBlueColor),
                        fontSize = 14.sp
                    )

                    if (showMoreText) {
                        Text(
                            text = "Voir plus",
                            color = Color(fontBlueColor),
                            fontSize = 12.sp
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${globalEmission.format(2)} kgCO2e",
                        color = Color.White,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (lastVisitIncrease != null) {
                        Text(
                            text = "+ $lastVisitIncrease%",
                            color = Color(fontBlueColor),
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ValueTilePreview() {
    EcoTrackerTheme {
        ValueTile("Emission globale", 37.28, 2.9, Color(0xFF1a74ef))
    }
}
