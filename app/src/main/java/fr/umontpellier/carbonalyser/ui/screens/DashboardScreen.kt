package fr.umontpellier.carbonalyser.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import fr.umontpellier.carbonalyser.R
import fr.umontpellier.carbonalyser.ui.components.tiles.DynamicTile
import fr.umontpellier.carbonalyser.ui.components.tiles.ValueTile
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme

@Composable
fun DashboardScreen(
    globalEmission: Double,
    lastVisitIncrease: Double,
    recordingDuration: Double,
    consumption: Double,
    downloadedData: Double,
    uploadedData: Double,
    carEquivalent: Double,
    numberOfCharges: Double
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            ValueTile(
                valueName = "Emission globale",
                globalEmission = globalEmission,
                lastVisitIncrease = lastVisitIncrease,
                backGroundColor = Color(0xFF1a74ef),
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),

                ) {
                item {
                    DynamicTile(
                        logoResId = R.drawable.clock_rotate_left_solid,
                        firstText = "$recordingDuration jours",
                        secondText = "Durée enregistrement",
                        imageResId = R.drawable.image_clock_tile,
                        imageOffSetX = 180,
                        imageOffSetY = 120
                    )
                }
                item {
                    DynamicTile(
                        logoResId = R.drawable.bolt_lightning_solid,
                        firstText = "$consumption kWh",
                        secondText = "Consommation",
                        imageResId = R.drawable.image_energy_tile,
                        imageOffSetX = 200,
                        imageOffSetY = 120
                    )
                }
                item {
                    DynamicTile(
                        logoResId = R.drawable.download_solid,
                        firstText = "$downloadedData GB",
                        secondText = "Données téléchargées",
                        imageResId = R.drawable.image_download_tile,
                        imageOffSetX = 200,
                        imageOffSetY = 170
                    )
                }
                item {
                    DynamicTile(
                        logoResId = R.drawable.upload_solid,
                        firstText = "$uploadedData GB",
                        secondText = "Données envoyées",
                        imageResId = R.drawable.image_upload_tile,
                        imageOffSetX = 210,
                        imageOffSetY = 150
                    )
                }
                item {
                    DynamicTile(
                        logoResId = R.drawable.car_side_solid,
                        firstText = "$carEquivalent km",
                        secondText = "Équivalent kilométrique",
                        imageResId = R.drawable.image_car_tile,
                        imageOffSetX = 170,
                        imageOffSetY = 140
                    )
                }
                item {
                    DynamicTile(
                        logoResId = R.drawable.logo_charger,
                        firstText = "$numberOfCharges",
                        secondText = "Équivalent recharge téléphone",
                        imageResId = R.drawable.image_phoneo_tile,
                        imageOffSetX = 200,
                        imageOffSetY = 75
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun DashboardScreenPreview() {
    EcoTrackerTheme {
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
}
