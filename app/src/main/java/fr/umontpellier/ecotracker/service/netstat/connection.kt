@file:Suppress("DEPRECATION")

package fr.umontpellier.ecotracker.service.netstat

import android.net.ConnectivityManager

enum class ConnectionType(val value: Int) {
    WIFI(ConnectivityManager.TYPE_WIFI),
    MOBILE(ConnectivityManager.TYPE_MOBILE),
}