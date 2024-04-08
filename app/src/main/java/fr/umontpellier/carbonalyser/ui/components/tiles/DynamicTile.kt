package fr.umontpellier.carbonalyser.ui.components.tiles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import fr.umontpellier.carbonalyser.R
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme

@Composable
fun DynamicTile(
    logoResId: Int,
    firstText: String,
    secondText: String,
    imageResId: Int,
    imageOffSetX: Int,
    imageOffSetY: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color.LightGray),
        modifier = Modifier
            .padding(16.dp)
            .size(150.dp)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopStart)
            )

            Text(
                text = firstText,
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.aleo_bold)),
                modifier = Modifier
                    .padding(top = 60.dp, start = 0.dp)
            )

            Text(
                text = secondText,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 90.dp, start = 0.dp)
            )

            Box(
                modifier = Modifier
                    .size(200.dp) // Taille de l'image
                    .offset(
                        x = with(LocalDensity.current) { imageOffSetX.toDp() },
                        y = with(LocalDensity.current) { imageOffSetY.toDp() }) // Position de l'image
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Transparent Image",
                    alpha = 0.1f
                )
            }
        }
    }
}


@Preview
@Composable
fun DynamicTilePreview() {
    EcoTrackerTheme {
        DynamicTile(
            logoResId = R.drawable.clock_rotate_left_solid,
            firstText = "4 jours",
            secondText = "Durée enregistrement",
            imageResId = R.drawable.image_clock_tile,
            imageOffSetX = 100,
            imageOffSetY = 90
        )
    }
}



