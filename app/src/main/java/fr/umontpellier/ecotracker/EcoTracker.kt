package fr.umontpellier.ecotracker

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.umontpellier.ecotracker.service.AndroidPackageService
import fr.umontpellier.ecotracker.service.DummyPackageService
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.PackageService
import fr.umontpellier.ecotracker.service.model.AndroidModelService
import fr.umontpellier.ecotracker.service.model.ModelService
import fr.umontpellier.ecotracker.service.netstat.AndroidNetStartService
import fr.umontpellier.ecotracker.service.netstat.DummyPkgNetStatService
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import fr.umontpellier.ecotracker.ui.EcoTrackerConfigSaver
import fr.umontpellier.ecotracker.ui.EcoTrackerLayout
import fr.umontpellier.ecotracker.ui.dialog.UsageAccessDialog
import fr.umontpellier.ecotracker.ui.screen.AppPage
import fr.umontpellier.ecotracker.ui.screen.Apps
import fr.umontpellier.ecotracker.ui.screen.Dashboard
import fr.umontpellier.ecotracker.ui.screen.GlobalChart
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val ecoTrackerModule = module {
    single { EcoTrackerConfig() }

    singleOf(::AndroidNetStartService) {
        bind<PkgNetStatService>()
    }
    singleOf(::AndroidModelService) {
        bind<ModelService>()
    }
    singleOf(::AndroidPackageService) {
        bind<PackageService>()
    }
}

val ecoTrackerPreviewModule = module {
    single { EcoTrackerConfig() }

    singleOf(::DummyPkgNetStatService) {
        bind<PkgNetStatService>()
    }
    singleOf(::AndroidModelService) {
        bind<ModelService>()
    }
    singleOf(::DummyPackageService) {
        bind<PackageService>()
    }
}

/**
 * [Application] représentant EcoTracker, permet de gérer des changements de paramètres
 * ou encore des cas comme la mémoire basse.
 */
class EcoTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@EcoTrackerApp)
            workManagerFactory()

            modules(ecoTrackerModule)
        }
    }
}

/**
 * Activité principale
 */
class EcoTrackerActivity : ComponentActivity() {
    @SuppressLint("ComposableDestinationInComposeScope")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EcoTrackerConfigSaver()
            EcoTrackerLayout {
                UsageAccessDialog {
                    when (it) {
                        0 -> Dashboard()
                        1 -> GlobalChart()
                        2 -> Apps()
                        3 -> AppPage()
                    }
                }
            }
        }
    }
}