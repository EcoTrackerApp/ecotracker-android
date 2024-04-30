package fr.umontpellier.ecotracker.ui.screen

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import fr.umontpellier.ecotracker.ui.LocalPagerState
import fr.umontpellier.ecotracker.ui.component.AppList
import fr.umontpellier.ecotracker.ui.component.aggregateTotalBytesByUid
import org.koin.compose.koinInject
import org.koin.dsl.koinApplication

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Apps(context: Context) {
    val pkgNetStatService: PkgNetStatService = koinInject()
    val pageState = LocalPagerState.current
    var PkgManager = context.packageManager;
    var modifier = Modifier;
    var mappedAppsList = aggregateTotalBytesByUid(pkgNetStatService.cache, 15);

    AppList(mappedAppsList, PkgManager, Modifier, pageState)

}


