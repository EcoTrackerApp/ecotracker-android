package fr.umontpellier.carbonalyser.ui.components.tile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.umontpellier.carbonalyser.data.model.CarbonEquivalent

@Composable
fun EquivalentTile(equivalent: CarbonEquivalent) {
    Card(modifier = Modifier.padding(4.dp).fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = equivalent.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = equivalent.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
