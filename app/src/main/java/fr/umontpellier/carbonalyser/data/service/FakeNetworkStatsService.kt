package fr.umontpellier.carbonalyser.data.service

class FakeNetworkStatsService {
    fun getDailyConsumption(): Double {
        // Retourne une valeur factice pour la consommation journalière en GB
        return 1.2 // Exemple : 1.2 GB
    }

    fun getTotalConsumption(): Double {
        // Retourne une valeur factice pour la consommation totale en GB
        return 5.4 // Exemple : 5.4 GB
    }
}
