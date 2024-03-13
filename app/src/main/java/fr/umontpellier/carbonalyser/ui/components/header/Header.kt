package fr.umontpellier.carbonalyser.ui.components.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.carbonalyser.R
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme

@Composable
fun Header() {
    var activeTab by remember { mutableStateOf("Général") }
    var blueColor = 0xFF1a74ef
    var grayColor = 0xFF96979a

    CarbonalyserTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Application logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = "Nom de l'application",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.aleo_bold)),
                    fontSize = 20.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Général",
                    color = if (activeTab == "Général") Color(blueColor) else Color(grayColor),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = "Général" }
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
                Text(
                    text = "Graphes",
                    color = if (activeTab == "Graphes") Color(blueColor) else Color(grayColor),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = "Graphes" }
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
                Text(
                    text = "Application",
                    color = if (activeTab == "Application") Color(blueColor) else Color(grayColor),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = "Application" }
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
            }
        }
    }
}



@Preview
@Composable
fun HeaderPreview() {
    Header()
}