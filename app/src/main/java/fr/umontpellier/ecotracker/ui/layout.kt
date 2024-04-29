package fr.umontpellier.ecotracker.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.koinInject

@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EcoTrackerLayout(
    config: EcoTrackerConfig = koinInject(),
    pkgNetStartService: PkgNetStatService = koinInject(),
    content: @Composable (page: Int) -> Unit = {}
) {
    val pageState = rememberPagerState(pageCount = { 3 })
    var isLoading by remember { mutableStateOf(pkgNetStartService.cacheJob.isActive) }

    LaunchedEffect(pkgNetStartService.cacheJob) {
        pkgNetStartService.cacheJob.invokeOnCompletion {
            // Log the completion status
            println("Job completed: ${pkgNetStartService.cacheJob.isCompleted}")
            // Refresh UI when job completes
            isLoading = false
        }
    }

    Scaffold(bottomBar = {
        if (isLoading) {
            return@Scaffold
        }

        BottomAppBar(content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BottomBarIndicator(pageState, id = 0)
                BottomBarIndicator(pageState, id = 1)
                BottomBarIndicator(pageState, id = 2)
            }
        }, containerColor = Color.Transparent, modifier = Modifier.height(40.dp))
    }) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            return@Scaffold
        }

        HorizontalPager(state = pageState, pageSize = PageSize.Fill) {
            content(it)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomBarIndicator(state: PagerState, id: Int) {
    Box(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(5.dp)
            )
            .width(if (state.currentPage == id) 25.dp else 10.dp)
            .height(10.dp)
            .background(Color.Black.copy(alpha = if (state.currentPage == id) 1F else 0.1F))
    )
}