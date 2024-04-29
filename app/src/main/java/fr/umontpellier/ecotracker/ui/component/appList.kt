package fr.umontpellier.ecotracker.ui.component

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.ecotracker.R
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService


@Composable
fun AppList(appList: Map<Int, Bytes>, pm: PackageManager, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(appList.keys.toList()) { uid ->
            appList[uid]?.let { bytes ->
                AppNameButton(
                    uid = uid,
                    value = bytes,
                    pm = pm,
                    onClick = {
                        //changement de page
                    }
                )
            }
        }
    }
}

@Composable
fun AppNameButton(uid: Int, value: Bytes, pm: PackageManager, onClick: () -> Unit) {

    val appName = getAppLabel(uid, pm)
    val icon: Painter = painterResource(id = R.drawable.application_icon_default)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
        if (appName != null) {
            Text(
                text = appName,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value.toString(),
            fontSize = 16.sp
        )
    }
}

fun aggregateTotalBytesByUid(result: PkgNetStatService.Result, n: Int): Map<Int, Bytes> {
    val aggregatedBytes = mutableMapOf<Int, Bytes>()

    result.appNetStats.forEach { (_, apps) ->
        apps.forEach { (uid, app) ->
            aggregatedBytes.merge(uid, app.total, Bytes::plus)
        }
    }
    return aggregatedBytes
        .toList()
        .sortedByDescending { (_, value) -> value.value }
        .take(n)
        .toMap()
}


fun getAppLabel(uid: Int, pm: PackageManager): String {
    val packageName = pm.getPackagesForUid(uid)?.firstOrNull()
    val appInfo = packageName?.let { pm.getApplicationInfo(it, 0) }
    val appLabel = appInfo?.let { pm.getApplicationLabel(it) }
    return appLabel?.toString() ?: "Unknown Application"
}


@Preview
@Composable
fun PreviexAppname() {
    val systemUid = 1000;
    val dummyPackageManager = PreviewPackageManager();
    AppNameButtonForPreview(systemUid, Bytes(15000), dummyPackageManager, { dummyFun() })
}


@Preview
@Composable
fun PreviewAppList() {
    val dummyPackageManager = PreviewPackageManager();

    val dummyMap = mapOf(
        1000 to Bytes(1024 * 1024 * 500),
        1001 to Bytes(1024 * 1024 * 150),
        1002 to Bytes(1024 * 1024 * 75)
    )
    AppListForPreview(
        appList = dummyMap,
        pm = dummyPackageManager,  // Passer le PackageManager de prévisualisation
        modifier = Modifier.fillMaxWidth()
    )

}

fun dummyFun() {
    println("Im a dummy");
}


@Composable
fun AppNameButtonForPreview(uid: Int, value: Bytes, pm: PreviewPackageManager, onClick: () -> Unit) {
    val packageName = pm.getPackagesForUid(uid)?.firstOrNull()
    val appInfo = packageName?.let { pm.getApplicationInfo(it, 0) }
    val appName = appInfo?.let { pm.getApplicationLabel(it) }.toString()
    val icon: Painter = painterResource(id = R.drawable.application_icon_default)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
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
            text = appName,
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

@Composable
fun AppListForPreview(appList: Map<Int, Bytes>, pm: PreviewPackageManager, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(appList.keys.toList()) { uid ->
            appList[uid]?.let { bytes ->
                AppNameButtonForPreview(
                    uid = uid,
                    value = bytes,
                    pm = pm,  // Assurez-vous que votre AppNameButton utilise PackageManagerProvider
                    onClick = {
                        // Logique à implémenter lors du clic sur l'élément
                    }
                )
            }
        }
    }
}

interface PackageManagerProvider {
    fun getPackagesForUid(uid: Int): Array<String>?
    fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo
    fun getApplicationLabel(appInfo: ApplicationInfo): CharSequence
}

class PreviewPackageManager : PackageManagerProvider {
    override fun getPackagesForUid(uid: Int): Array<String>? {
        return arrayOf("com.example.dummy")
    }

    override fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo {
        // Créer une instance fictive d'ApplicationInfo
        return ApplicationInfo().apply {
            // Vous pourriez simuler des valeurs spécifiques ici
        }
    }

    override fun getApplicationLabel(appInfo: ApplicationInfo): CharSequence {
        // Retourner un label fictif pour l'application
        return "Dummy App"
    }
}
