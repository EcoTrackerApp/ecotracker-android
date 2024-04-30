package fr.umontpellier.ecotracker.service

import android.content.Context
import android.graphics.drawable.Drawable
import fr.umontpellier.ecotracker.ui.util.getAppIcon
import fr.umontpellier.ecotracker.ui.util.getPackageName

interface PackageService {
    fun appLabel(uid: Int): String

    fun appIcon(uid: Int): Drawable?
}

class DummyPackageService : PackageService {
    override fun appLabel(uid: Int) = "Dummy App"
    override fun appIcon(uid: Int): Drawable? {
        TODO("Not yet implemented")
    }
}

class AndroidPackageService(val context: Context) : PackageService {
    override fun appLabel(uid: Int): String {
        return context.packageManager.getPackageName(uid)
    }

    override fun appIcon(uid: Int): Drawable? {
        return context.packageManager.getAppIcon(uid)
    }


}