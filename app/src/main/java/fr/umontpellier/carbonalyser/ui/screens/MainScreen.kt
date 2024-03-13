package fr.umontpellier.carbonalyser.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.umontpellier.carbonalyser.data.model.CarbonEquivalent
import fr.umontpellier.carbonalyser.data.model.EmissionData
import fr.umontpellier.carbonalyser.data.model.MainViewModel
import fr.umontpellier.carbonalyser.ui.components.tile.EquivalentTile

@Composable
@Preview
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val dailyConsumption = viewModel.dailyConsumption.collectAsState()
    val totalConsumption = viewModel.totalConsumption.collectAsState()
    val equivalents = viewModel.equivalents.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Consommation journalière : ${dailyConsumption.value} GB", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Consommation totale : ${totalConsumption.value} GB", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(4.dp)) {
            items(equivalents.value) { equivalent ->
                EquivalentTile(equivalent = equivalent)
            }
        }
    }
}



@Composable
fun EmissionsScreen(emissionsData: List<EmissionData>) {
    // TODO: Layout for emissions data per app
}
