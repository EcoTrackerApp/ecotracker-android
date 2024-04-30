package fr.umontpellier.ecotracker.service

import android.content.Context
import fr.umontpellier.ecotracker.ui.util.getPackageName

interface PackageService {
    fun appLabel(uid: Int): String
}

class DummyPackageService : PackageService {
    override fun appLabel(uid: Int) = "Dummy App"
}

class AndroidPackageService(val context: Context) : PackageService {
    override fun appLabel(uid: Int): String {
        return context.packageManager.getPackageName(uid)
    }
}