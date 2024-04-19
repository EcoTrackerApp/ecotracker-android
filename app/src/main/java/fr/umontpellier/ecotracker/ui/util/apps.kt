package fr.umontpellier.ecotracker.ui.util

import android.content.pm.PackageManager

fun PackageManager.getPackageName(uid: Int): String {
    return getPackagesForUid(uid)
        ?.map { pkg -> getPackageInfo(pkg, PackageManager.GET_META_DATA) }
        ?.joinToString(", ") { info -> getApplicationLabel(info.applicationInfo) }
        ?: "Système"
}