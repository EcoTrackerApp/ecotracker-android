package fr.umontpellier.carbonalyser.android

import android.app.AppOpsManager
import android.content.Intent
import android.os.Process
import android.provider.Settings
import androidx.activity.ComponentActivity

fun ComponentActivity.openUsageAccessSettings() {
    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    startActivity(intent)
}

val ComponentActivity.hasUsageAccess: Boolean
    get() {
        val appOps = getSystemService(ComponentActivity.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
