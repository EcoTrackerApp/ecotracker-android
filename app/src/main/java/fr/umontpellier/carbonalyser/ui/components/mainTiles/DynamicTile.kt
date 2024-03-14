package fr.umontpellier.carbonalyser.ui.components.mainTiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme

@Composable
fun DynamicTile(
    logoResId: Int,
    firstText: String,
    secondText: String,
    imageResId: Int,
    imageOffSetX: Int,
    imageOffSetY: Int
) {
    CarbonalyserTheme {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .padding(16.dp)
                .size(150.dp)
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f) // Garde la hauteur égale à la largeur
                    .padding(16.dp)
            ) {
                // Logo en haut à gauche
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(32.dp)  // Taille du Logo
                        .align(Alignment.TopStart) // Alignement en haut à gauche
                )

                // Premier texte dynamique
                Text(
                    text = firstText,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.aleo_bold)),
                    modifier = Modifier
                        .padding(top = 60.dp, start = 0.dp) // Ajoute une marge au-dessus et à gauche
                )

                // Deuxième texte dynamique plus petit
                Text(
                    text = secondText,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 90.dp, start = 0.dp) // Ajoute une marge au-dessus et à gauche
                )

                // Image transparente positionnée de manière absolue
                Box(
                    modifier = Modifier
                        .size(200.dp) // Taille de l'image
                        .offset(x = with(LocalDensity.current) { imageOffSetX.toDp() }, y = with(LocalDensity.current) { imageOffSetY.toDp() }) // Position de l'image
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
}


@Preview
@Composable
fun DynamicTilePreview() {
    DynamicTile(
        logoResId = R.drawable.clock_rotate_left_solid,
        firstText = "4 jours",
        secondText = "Durée enregistrement",
        imageResId = R.drawable.image_clock_tile,
        imageOffSetX = 100,
        imageOffSetY = 90
    )
}



