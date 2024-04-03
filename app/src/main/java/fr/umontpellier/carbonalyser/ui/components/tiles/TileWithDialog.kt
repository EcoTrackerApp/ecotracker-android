package fr.umontpellier.carbonalyser.ui.components.tiles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import fr.umontpellier.carbonalyser.R
import fr.umontpellier.carbonalyser.ui.theme.EcoTrackerTheme

@Composable
fun TileWithDialog(
    logoResId: Int,
    firstText: String,
    secondText: String,
    imageResId: Int,
    imageOffSetX: Int,
    imageOffSetY: Int,
) {
    val showDialog = remember { mutableStateOf(false) }

    DynamicTile(
        logoResId = logoResId,
        firstText = firstText,
        secondText = secondText,
        imageResId = imageResId,
        imageOffSetX = imageOffSetX,
        imageOffSetY = imageOffSetY,
        showDialog = showDialog
    )
    EnlargedDynamicTileDialog(
        showDialog = showDialog,
        logoResId = logoResId,
        firstText = firstText,
        secondText = secondText,
        imageResId = imageResId,
        imageOffSetX = 100,
        imageOffSetY = 90
    )
}

@Preview
@Composable
fun TileWithDialogPreview() {

    EcoTrackerTheme {
        TileWithDialog(
            logoResId = R.drawable.clock_rotate_left_solid,
            firstText = "4 jours",
            secondText = "Durée enregistrement",
            imageResId = R.drawable.image_clock_tile,
            imageOffSetX = 100,
            imageOffSetY = 90,
        )
    }
}
