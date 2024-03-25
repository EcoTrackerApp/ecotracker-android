package fr.umontpellier.carbonalyser.model

import fr.umontpellier.carbonalyser.android.PackageNetworkStats

object OneByte : Model {
    private const val CO2_PER_KWH_IN_DC_GREY = 519
    private const val CO2_PER_KWH_NETWORK_GREY = 475
    private const val CO2_PER_KWH_IN_DC_GREEN = 0

    private const val KWH_PER_BYTE_IN_DC = 7.2e-11
    private const val FIXED_NETWORK_WIRED = 4.29e-10
    private const val FIXED_NETWORK_WIFI = 1.52e-10
    private const val FOUR_G_MOBILE = 8.84e-10

    private val KWH_PER_BYTE_FOR_NETWORK = (FIXED_NETWORK_WIRED + FIXED_NETWORK_WIFI + FOUR_G_MOBILE) / 3
    private const val KWH_PER_BYTE_FOR_DEVICES = 1.3e-10 // TODO: use this?

    /**
     * Simple conversion of the code from [CO2.js](https://github.com/thegreenwebfoundation/co2.js/blob/main/src/1byte.js).
     */
    private fun perByte(bytes: Double, green: Boolean): Double {
        if (bytes < 1) {
            return 0.0
        }

        return if (green) {
            val co2ForDC = bytes * KWH_PER_BYTE_IN_DC * CO2_PER_KWH_IN_DC_GREEN
            val co2ForNetwork = bytes * KWH_PER_BYTE_FOR_NETWORK * CO2_PER_KWH_NETWORK_GREY
            co2ForDC + co2ForNetwork
        } else {
            val kwHPerByte = KWH_PER_BYTE_IN_DC + KWH_PER_BYTE_FOR_NETWORK
            bytes * kwHPerByte * CO2_PER_KWH_IN_DC_GREY
        }
    }

    override val id = "1byte"
    override fun estimate(pkgNetStats: PackageNetworkStats, options: ModelOptions): ModelResult {
        val bytesReceivedCO2 = perByte(pkgNetStats.bytesReceived.toDouble(), options.green)
        val bytesSentCO2 = perByte(pkgNetStats.bytesSent.toDouble(), options.green)
        return ModelResult(pkgNetStats, bytesReceivedCO2, bytesSentCO2)
    }
}
