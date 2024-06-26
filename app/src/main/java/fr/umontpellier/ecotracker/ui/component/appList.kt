package fr.umontpellier.ecotracker.ui.component

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import fr.umontpellier.ecotracker.R
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.PackageService
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import fr.umontpellier.ecotracker.ui.LocalPagerState
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject


@Composable
fun AppColumn(
    modifier: Modifier = Modifier,
    pkgNetStatService: PkgNetStatService = koinInject(),
    applimit: Int = 10,
    buttonSize: Int = 50,
    spaceBtwnItems: Int = 4,
    selectedAppCons: MutableState<Float?>? = null,
) {
    val appTotals = pkgNetStatService.cache.appNetStats.flatMap { entry ->
        entry.value.mapNotNull { (uid, netStat) ->
            uid to netStat.total.value
        }
    }.groupBy(keySelector = { it.first }, valueTransform = { it.second })
        .mapValues { (_, values) -> values.sum() }
        .toList()
        .sortedByDescending { it.second }
        .take(applimit)

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 14.dp)
    ) {
        items(appTotals) { (uid, totalBytes) ->
            val consumptionInFloat = totalBytes.toFloat()

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spaceBtwnItems.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                AppButton(
                    uid = uid,
                    consumption = Bytes(totalBytes),
                    buttonSize = buttonSize,
                    highlighted = selectedAppCons?.value?.equals(consumptionInFloat) ?: false,
                )
            }
        }
    }
}

fun String.truncate(length: Int): String {
    return if (this.length > length) {
        this.substring(0, length) + "..."
    } else {
        this
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppButton(
    uid: Int,
    consumption: Bytes,
    config: EcoTrackerConfig = koinInject(),
    packageService: PackageService = koinInject(),
    buttonSize: Int = 50,
    highlighted: Boolean,

    ) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pageState = LocalPagerState.current
    val defaultDrawable = ContextCompat.getDrawable(context, R.drawable.android_icon)
    val appDrawable = packageService.appIcon(uid) ?: defaultDrawable
    val backgroundColor = if (highlighted) Color.Blue else Color.Black
    val fontSize = if (highlighted) 22.sp else 17.sp


    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable(onClick = {
                scope.launch {
                    config.currentApp = uid
                    pageState.animateScrollToPage(3)
                }
            }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {

        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    setImageDrawable(appDrawable)
                    layoutParams = ViewGroup.LayoutParams(
                        context.dpToPx(50),
                        context.dpToPx(50)
                    )
                }
            },
            update = { imageView ->
                imageView.setImageDrawable(appDrawable)
            },
            modifier = Modifier.size(buttonSize.dp),

            )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = packageService.appLabel(uid).truncate(23),
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 1.dp),
            letterSpacing = (-0.5).sp,
            color = backgroundColor


        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = consumption.toString(),
            color = backgroundColor,
            fontSize = fontSize
        )
    }
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

@Preview
@Composable
fun ApppButonPeview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        AppButton(1000, Bytes(150780), highlighted = false)
    }
}


@Preview
@Composable
fun AppListPreview(modifier: Modifier = Modifier) {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        AppColumn()
    }
}