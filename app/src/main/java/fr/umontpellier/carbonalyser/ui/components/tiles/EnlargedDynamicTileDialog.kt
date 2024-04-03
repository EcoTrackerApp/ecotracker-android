package fr.umontpellier.carbonalyser.ui.components.tiles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import fr.umontpellier.carbonalyser.R

@Composable
fun EnlargedDynamicTileDialog(
    showDialog: MutableState<Boolean>,
    logoResId: Int,
    firstText: String,
    secondText: String,
    imageResId: Int,
    imageOffSetX: Int,
    imageOffSetY: Int
) {
    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color.LightGray),
                        modifier = Modifier
                            .padding(16.dp)
                            .size(300.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                painter = painterResource(id = logoResId),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(64.dp)
                            )

                            Text(
                                text = firstText,
                                fontSize = 30.sp,
                                color = Color.Black,
                                fontFamily = FontFamily(Font(R.font.aleo_bold))
                            )

                            Text(
                                text = secondText,
                                fontSize = 15.sp,
                                color = Color.Gray
                            )

                            Button(onClick = { showDialog.value = false }) {
                                Text("Retour")
                            }
                        }
                    }
                }
            }
        }
    }
}
