package fr.umontpellier.ecotracker.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.ecotracker.R
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.PackageService
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject


@Composable
fun AppColumn(pkgNetStatService: PkgNetStatService = koinInject(), modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        val appIds = pkgNetStatService.cache.appNetStats.entries
            .flatMap { it.value.keys }
        items(appIds) { uid ->
            AppButton(uid)
        }
    }
}

@Composable
fun AppButton(
    uid: Int,
    pkgNetStatService: PkgNetStatService = koinInject(),
    packageService: PackageService = koinInject()
) {
    val scope = rememberCoroutineScope()
    val icon: Painter = painterResource(id = R.drawable.application_icon_default)

    val value = Bytes(pkgNetStatService.cache.appNetStats
        .map { (_, data) -> data[uid] }
        .filterNotNull()
        .sumOf { data -> data.total.value })
    

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                scope.launch {
                    //pageState.animateScrollToPage(0) // Changer à la page souhaitée
                }
            })
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = icon,
            contentDescription = "Icon",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = packageService.appLabel(uid),
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value.toString(),
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun AppListPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        AppColumn()
    }
}