package fr.umontpellier.ecotracker.ui.dialog

import android.app.AppOpsManager
import android.content.Intent
import android.os.Process
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.umontpellier.ecotracker.R
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.koinInject

fun ManagedActivityResultLauncher<Intent, ActivityResult>.openUsageAccessSettings() {
    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    this.launch(intent)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentActivity.UsageAccessDialog(
    pkgNetStatService: PkgNetStatService = koinInject(),
    content: @Composable () -> Unit
) {
    var hasUsageAccessState by remember { mutableStateOf(hasUsageAccess) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        hasUsageAccessState = hasUsageAccess
        pkgNetStatService.fetchAndCache()
    }
    if (!hasUsageAccessState) {
        BasicAlertDialog(
            onDismissRequest = { finishAffinity() }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(375.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(100.dp)
                    )
                    Text(
                        text = "Nous avons besoin de votre permission pour lire vos données réseaux.",
                        modifier = Modifier.padding(16.dp),
                    )
                    Text(
                        text = "Pas de panique ! On ne fait que regardez les quantités de données et n'analysons aucun contenu. Nous ne savons rien sur vous !",
                        modifier = Modifier.padding(16.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { finishAffinity() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Non!")
                        }
                        TextButton(
                            onClick = { requestPermissionLauncher.openUsageAccessSettings() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Ouvrir le menu")
                        }
                    }
                }
            }
        }
    }
    content()
}