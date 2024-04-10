package fr.umontpellier.ecotracker.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EcoTrackerLayout(
    content: @Composable (page: Int) -> Unit
) {
    val pageState = rememberPagerState(pageCount = { 3 })

    Scaffold(bottomBar = {
        BottomAppBar(content = {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                BottomBarIcon(Icons.Default.Home, "Home") {
                    pageState.animateScrollToPage(0)
                }
                BottomBarIcon(Icons.Default.Info, "Detail") {
                    pageState.animateScrollToPage(1)
                }
                BottomBarIcon(Icons.Default.Menu, "Apps") {
                    pageState.animateScrollToPage(2)
                }
            }
        })
    }) {
        HorizontalPager(state = pageState, pageSize = PageSize.Fill) {
            content(it)
        }
    }
}

@Composable
fun BottomBarIcon(icon: ImageVector, description: String, action: suspend () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    IconButton(onClick = {
        coroutineScope.launch {
            action()
        }
    }) {
        Icon(
            icon,
            contentDescription = description,
        )
    }
}
