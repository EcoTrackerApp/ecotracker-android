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
    fun estimate(connectipkgNetStats: PackageNetworkStats): ModelResult

}

data class ModelResult(val bytesSentEmission: Long, val bytesReceivedEmission: Long)