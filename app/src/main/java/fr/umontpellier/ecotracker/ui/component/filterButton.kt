package fr.umontpellier.ecotracker.ui.component

import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import fr.umontpellier.ecotracker.service.netstat.ConnectionType
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService

enum class ConnectionTypeFilter(val filter: (PkgNetStatService.Result.App.Connection) -> Boolean) {
    Wifi({ it.connection == ConnectionType.WIFI }),
    Mobile({ it.connection == ConnectionType.MOBILE }),
    Aucun({ true })
}

@Composable
fun FilterButton(value: ConnectionTypeFilter, setValue: (ConnectionTypeFilter) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Button(onClick = { expanded = true }) {
        Text(text = "Filtre: ${value.name}")
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        ConnectionTypeFilter.values().forEach { filterType ->
            DropdownMenuItem(onClick = {
                setValue(filterType)
                expanded = false
            }, text = { Text(text = filterType.name) })
        }
    }
}