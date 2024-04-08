package fr.umontpellier.carbonalyser.model

import fr.umontpellier.carbonalyser.android.PackageNetworkStats

interface Model {

    /**
     * Returns a unique ID for that model.
     */
    val id: String

    /**
     * Estimates the emissions of CO2 from a [PackageNetworkStats] using this model.
     */
    fun estimate(pkgNetStats: PackageNetworkStats, options: ModelOptions): ModelResult

}

data class ModelOptions(val green: Boolean = false)

data class ModelResult(val pkgval: PackageNetworkStats, val bytesReceivedCO2: Double, val bytesSentCO2: Double)