package fr.umontpellier.carbonalyser.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.umontpellier.carbonalyser.data.service.FakeNetworkStatsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val networkStatsService = FakeNetworkStatsService()

    private val _dailyConsumption = MutableStateFlow(0.0)
    val dailyConsumption: StateFlow<Double> = _dailyConsumption

    private val _totalConsumption = MutableStateFlow(0.0)
    val totalConsumption: StateFlow<Double> = _totalConsumption

    private val _equivalents = MutableStateFlow<List<CarbonEquivalent>>(emptyList())
    val equivalents: StateFlow<List<CarbonEquivalent>> = _equivalents

    init {
        fetchNetworkStats()
    }

    private fun fetchNetworkStats() {
        viewModelScope.launch {
            _dailyConsumption.value = networkStatsService.getDailyConsumption()
            _totalConsumption.value = networkStatsService.getTotalConsumption()
            _equivalents.value = listOf(
                CarbonEquivalent("Voiture", "${calculateCarEquivalent(_totalConsumption.value)} km"),
                CarbonEquivalent("Train", "${calculateTrainEquivalent(_totalConsumption.value)} km")
                // Ajoutez d'autres équivalences ici
            )
        }
    }

    // Fonctions pour calculer les équivalences
    private fun calculateCarEquivalent(totalConsumption: Double): Int {
        return (totalConsumption * 2).toInt()
    }

    private fun calculateTrainEquivalent(totalConsumption: Double): Int {
        return (totalConsumption * 5).toInt()
    }
}