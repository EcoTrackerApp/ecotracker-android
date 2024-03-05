package fr.umontpellier.carbonalyser.model

import fr.umontpellier.carbonalyser.android.PackageNetworkStats

interface Model {

    fun estimate(connectipkgNetStats: PackageNetworkStats)

}