package fr.umontpellier.ecotracker.ui.util

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import fr.umontpellier.ecotracker.R

fun PackageManager.getPackageName(uid: Int): String {
    return getPackagesForUid(uid)
        ?.map { pkg -> getPackageInfo(pkg, PackageManager.GET_META_DATA) }
        ?.map { info -> getApplicationLabel(info.applicationInfo).toString() }
        ?.let { labels ->
            if (labels.size > 3) {
                labels.take(3).joinToString(", ") + "..."
            } else {
                labels.joinToString(", ")
            }
        }
        ?: "Système"
}        // BarChart occupant 2/3 de l'écran en hauteur

fun PackageManager.getAppIcon(uid: Int): Drawable? {
    val packages = getPackagesForUid(uid)

    packages?.firstOrNull()?.let { packageName ->
        val appInfo = getApplicationInfo(packageName, 0)
        return appInfo.loadIcon(this)

    }
    return null
}
