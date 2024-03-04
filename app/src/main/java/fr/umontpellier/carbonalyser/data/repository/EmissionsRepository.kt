package fr.umontpellier.carbonalyser.data.repository

import fr.umontpellier.carbonalyser.data.model.EmissionData

class EmissionsRepository {
    // TODO: Implement the logic to get emission data, e.g. from a database or an API
    fun getEmissionData(): List<EmissionData> {
        // This should be replaced with real data fetching logic
        return listOf(
            EmissionData(name = "App A", co2Emissions = 1.5),
            EmissionData(name = "App B", co2Emissions = 2.3)
            // Add more data here
        )
    }
}