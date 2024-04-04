package fr.umontpellier.carbonalyser.ui.screens

import android.content.Context
import android.content.Context.CONTEXT_RESTRICTED
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import fr.umontpellier.carbonalyser.R
import fr.umontpellier.carbonalyser.android.Connectivity
import fr.umontpellier.carbonalyser.android.PackageNetworkStats
import fr.umontpellier.carbonalyser.android.packageNetworkStatsManager
import fr.umontpellier.carbonalyser.model.ModelOptions
import fr.umontpellier.carbonalyser.model.ModelService
import fr.umontpellier.carbonalyser.ui.components.tiles.*
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme
import fr.umontpellier.carbonalyser.util.format
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun DashboardWeek(context: Context) {
    val end = Instant.now()
    var start by remember { mutableStateOf(end.minus(7, ChronoUnit.DAYS)) }
    var isLoading by remember { mutableStateOf(false) }
    var dataCollection by remember { mutableStateOf(emptyList<PackageNetworkStats>()) }

    // Calculez la durée d'enregistrement en jours
    val recordingDurationInDays = Duration.between(start, end).toDays()

    LaunchedEffect(start) {
        isLoading = true
        dataCollection = context.packageNetworkStatsManager.collect(
            start,
            end,
            arrayOf(Connectivity.WIFI, Connectivity.MOBILE)
        )
        isLoading = false
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        DashboardScreen(
            globalEmission = dataCollection.map { pkg -> ModelService["1byte"]!!.estimate(pkg, ModelOptions()) }
                .sumOf { it.bytesSentCO2 + it.bytesReceivedCO2 } / 1e3,
            lastVisitIncrease = 0.0,
            recordingDuration = recordingDurationInDays.toDouble(),
            consumption = 0.0,
            downloadedData = dataCollection.sumOf { it.bytesReceived } / 1e9,
            uploadedData = dataCollection.sumOf { it.bytesSent } / 1e9,
            carEquivalent = 0.0,
            numberOfCharges = 0.0,
            onDateSelected = { dateTime ->
                val newStart = dateTime.atZone(ZoneId.systemDefault()).toInstant()
                start = newStart
            }
        )
    }
}

@Composable
fun DashboardScreen(
    globalEmission: Double,
    lastVisitIncrease: Double,
    recordingDuration: Double,
    consumption: Double,
    downloadedData: Double,
    uploadedData: Double,
    carEquivalent: Double,
    numberOfCharges: Double,
    onDateSelected: (LocalDateTime) -> Unit,

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

                    DurationScreenTile(
                        logoResId = R.drawable.clock_rotate_left_solid,
                        firstText = "$recordingDuration jours",
                        secondText = "Durée enregistrement",
                        imageResId = R.drawable.image_clock_tile,
                        imageOffSetX = 180,
                        imageOffSetY = 120,
                        recordingDuration = recordingDuration,
                        onDateSelected = onDateSelected
                    )
                }
                item {
                    TileWithDialog(
                        logoResId = R.drawable.bolt_lightning_solid,
                        firstText = "$consumption kW",
                        secondText = "Consommation",
                        imageResId = R.drawable.image_energy_tile,
                        imageOffSetX = 200,
                        imageOffSetY = 120,
                    )
                }
                item {
                    TileWithDialog(
                        logoResId = R.drawable.download_solid,
                        firstText = "${downloadedData.format(2)} GB",
                        secondText = "Données téléchargées",
                        imageResId = R.drawable.image_download_tile,
                        imageOffSetX = 200,
                        imageOffSetY = 170,
                    )
                }
                item {
                    TileWithDialog(
                        logoResId = R.drawable.upload_solid,
                        firstText = "${uploadedData.format(2)} GB",
                        secondText = "Données envoyées",
                        imageResId = R.drawable.image_upload_tile,
                        imageOffSetX = 210,
                        imageOffSetY = 150,
                    )
                }
                item {
                    TileWithDialog(
                        logoResId = R.drawable.car_side_solid,
                        firstText = "$carEquivalent km",
                        secondText = "Équivalent kilométrique",
                        imageResId = R.drawable.image_car_tile,
                        imageOffSetX = 170,
                        imageOffSetY = 140,
                    )
                }
                item {
                    TileWithDialog(
                        logoResId = R.drawable.logo_charger,
                        firstText = "$numberOfCharges",
                        secondText = "Équivalent recharge téléphone",
                        imageResId = R.drawable.image_phoneo_tile,
                        imageOffSetX = 200,
                        imageOffSetY = 75,
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
            numberOfCharges = 64.0,
            onDateSelected = { dateTime ->
                println("clc les preview a un moment")
            },
        )
    }
}
