package fr.umontpellier.ecotracker.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.koinInject

@Composable
fun Dashboard(
    pkgNetStatService: PkgNetStatService = koinInject()
) {
    Text("Hello from dashboard")
}