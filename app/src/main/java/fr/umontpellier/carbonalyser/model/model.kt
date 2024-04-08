package fr.umontpellier.carbonalyser.model

import fr.umontpellier.carbonalyser.android.PackageNetworkStats
import fr.umontpellier.carbonalyser.utils.Bytes
import fr.umontpellier.carbonalyser.utils.CO2Emissions

interface Model {

    /**
     * Returns a unique ID for that model.
     */
    val id: String

    /**
     * Estimates the emissions of CO2 from a [PackageNetworkStats] using this model.
     */
    fun estimate(pkgNetStats: PackageNetworkStats, options: ModelOptions): ModelResult

    fun convert(consumption: Bytes, option: Boolean): CO2Emissions

    fun convert(consumption: Bytes, option: Boolean): CO2Emissions

}

data class ModelOptions(val green: Boolean = false)

data class ModelResult(val bytesReceivedCO2: CO2Emissions, val bytesSentCO2: CO2Emissions)