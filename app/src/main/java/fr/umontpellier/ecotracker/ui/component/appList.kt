package fr.umontpellier.ecotracker.ui.component

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import fr.umontpellier.ecotracker.R
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.PackageService
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject


@Composable
fun AppColumn(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modifier: Modifier = Modifier,
    limit: Int = 10
) {
    val appTotals = pkgNetStatService.cache.appNetStats.flatMap { entry ->
        entry.value.mapNotNull { (uid, netStat) ->
            uid to netStat.total.value
        }
    }.groupBy(keySelector = { it.first }, valueTransform = { it.second })
        .mapValues { (_, values) -> values.sum() }
        .toList()
        .sortedByDescending { it.second }
        .take(limit)

    LazyColumn(
        modifier = modifier
            .padding(14.dp)
    ) {
        items(appTotals) { (uid, totalBytes) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                AppButton(uid, Bytes(totalBytes))
            }
        }
    }
}


@Composable
fun AppButton(
    uid: Int,
    consumption: Bytes,
    packageService: PackageService = koinInject()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val defaultDrawable = ContextCompat.getDrawable(context, R.drawable.application_icon_default)
    val appDrawable = packageService.appIcon(uid) ?: defaultDrawable

    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable(onClick = {
                scope.launch {
                    // pageState.animateScrollToPage(0) // Changer à la page souhaitée
                }
            }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    setImageDrawable(appDrawable)
                    layoutParams = ViewGroup.LayoutParams(
                        context.dpToPx(50), // Convert dp to pixel
                        context.dpToPx(50)
                    )
                }
            },
            update = { imageView ->
                imageView.setImageDrawable(appDrawable)
            },
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = packageService.appLabel(uid),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 1.dp),
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = consumption.toString(),
            fontSize = 16.sp
        )
    }
}

// Helper function to convert dp to pixels
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

@Preview
@Composable
fun ApppButonPeview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        AppButton(1000, Bytes(15078))
    }
}


@Preview
@Composable
fun AppListPreview(modifier: Modifier = Modifier) {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        AppColumn()
    }
}