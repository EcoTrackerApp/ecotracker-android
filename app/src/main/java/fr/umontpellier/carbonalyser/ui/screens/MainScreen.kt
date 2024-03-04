package fr.umontpellier.carbonalyser.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import fr.umontpellier.carbonalyser.data.model.EmissionData

@Composable
fun MainScreen(onCheckNetworkStats: () -> Unit) {
    Button(onClick = onCheckNetworkStats) {
        Text("Vérifier les stats réseau")
    }
}


@Composable
fun EmissionsScreen(emissionsData: List<EmissionData>) {
    // TODO: Layout for emissions data per app
}
