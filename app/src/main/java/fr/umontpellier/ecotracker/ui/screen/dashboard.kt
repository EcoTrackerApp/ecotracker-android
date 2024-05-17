package fr.umontpellier.ecotracker.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.ecotracker.R
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.ModelService
import fr.umontpellier.ecotracker.service.model.unit.Usage
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import fr.umontpellier.ecotracker.ui.component.Alert
import fr.umontpellier.ecotracker.ui.component.ModelButton
import fr.umontpellier.ecotracker.ui.dialog.ChangeDateDialog
import fr.umontpellier.ecotracker.ui.util.dateFormatter
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import java.time.Duration

@Composable
fun Dashboard(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modelService: ModelService = koinInject(),
) {
    var pageIndex by remember { mutableStateOf(0) }

    Log.i("ecotracker", pkgNetStatService.cache.toString())
    Box(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.padding(bottom = 32.dp)) {
            Header()
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                AverageAlert()
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Dans le détail",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                BytesReceived()
                BytesSent()
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {

                Text(
                    text = "Concrètement, qu'est-ce que ça veut dire?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Row {
                    Button(
                        onClick = {
                            pageIndex = if (pageIndex == 0) {
                                2
                            } else {
                                (pageIndex - 1).coerceAtLeast(0)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.DarkGray
                        )
                    ) { Text("<", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)) }

                    Box {
                        when (pageIndex) {
                            0 -> Equivalent(
                                image = R.drawable.car,
                                text = "C'est ${modelService.total.carKm} en voiture"
                            )

                            1 -> Equivalent(image = R.drawable.train, text = "ou ${modelService.total.tgvKm} en train")
                            2 -> Equivalent(
                                image = R.drawable.plane,
                                text = "ou  ${modelService.total.planeMeter} en avion"
                            )

                        }
                    }

                    Button(
                        onClick = { pageIndex = (pageIndex + 1).coerceAtMost(2) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.DarkGray
                        )

                    ) { Text(">", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)) }
                }

                Equivalent(
                    image = R.drawable.tree,
                    text = "Il faut ${
                        modelService.total.tooTreeEquivalent(
                            pkgNetStatService.cache.start,
                            pkgNetStatService.cache.end
                        )
                    } arbres pour absorber vos emissions"
                )
            }
        }
        // Positionne le bouton en haut à droite
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            ModelButton(
                modifier = Modifier.padding(top = 10.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun Header(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modelService: ModelService = koinInject()
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy((-60).dp)) {
        Image(
            painterResource(R.drawable.header),
            "Header",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Column {
                Text(
                    text = "Vous avez émis ",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    text = modelService.total.toString(),
                    color = Color.Green,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
            }
            Column(modifier = Modifier
                .padding(top = 6.dp)
                .clickable { showDialog = true }) {

                Text(
                    text = "du ${dateFormatter.format(pkgNetStatService.cache.start)}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp
                )
                Text(
                    text = "au ${dateFormatter.format(pkgNetStatService.cache.end)}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp
                )
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier
                            .width(14.dp)
                    )
                }
            }
        }
    }
    ChangeDateDialog(showDialog = showDialog) { showDialog = false }
}

@Composable
fun AverageAlert(config: EcoTrackerConfig = koinInject(), pkgNetStatService: PkgNetStatService = koinInject()) {
    val days = Duration.between(config.dates.first, config.dates.second).toDays()
    val usage = pkgNetStatService.total.usage(days)
    val gradient = when (usage) {
        Usage.LOW -> horizontalGradient(
            colors = listOf(
                Color(0xFFD0E8CF),
                Color(0xFF98C387),
                Color(0xFF6C985B)
            )
        )

        Usage.MEDIUM -> horizontalGradient(
            colors = listOf(
                Color(0xFFD0E8CF),
                Color(0xFF98C387),
                Color(0xFF6C985B)
            )
        )

        Usage.HIGH -> horizontalGradient(
            colors = listOf(
                Color(0xFFFFABAB),
                Color(0xFFE57373),
                Color(0xFFAF4448)
            )
        )
    }
    val text = when (usage) {
        Usage.LOW -> "Vous êtes éco-responsable !" to "Vous consommez moins que le français moyen en 2021."
        Usage.MEDIUM -> "Vous êtes dans la moyenne !" to "Continuez comme ça !"
        Usage.HIGH -> "Vous êtes au dessus de la moyenne." to "Vous consommez plus que le français moyen en 2021!"
    }

    Alert(
        gradient,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text.first,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xBAFFFFFF),
            fontSize = TextUnit(18f, TextUnitType.Sp),
            lineHeight = TextUnit(22f, TextUnitType.Sp),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp)
        )
        Text(
            text.second,
            textAlign = TextAlign.Center,
            color = Color(0xBAFFFFFF),
            fontWeight = FontWeight.ExtraBold,
            fontSize = TextUnit(9f, TextUnitType.Sp),
            lineHeight = TextUnit(12f, TextUnitType.Sp),
            letterSpacing = TextUnit(-0.25F, TextUnitType.Sp)
        )
    }
}

@Composable
fun BytesSent(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modelService: ModelService = koinInject()
) {
    Alert(
        horizontalGradient(
            0F to Color(0xFFFF9877),
            0.72F to Color(0xFFC96353),
            1F to Color(0xFF932E2E)
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Vos émissions Mobile et Wifi",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color(0xBAFFFFFF),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = (-0.5F).sp
        )
        Text(
            "${pkgNetStatService.sent} (${modelService.sent})",
            textAlign = TextAlign.Center,
            color = Color(0xBAFFFFFF),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 25.sp,
            letterSpacing = (-0.25F).sp
        )
    }
}

@Composable
fun BytesReceived(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modelService: ModelService = koinInject()
) {
    Alert(
        horizontalGradient(
            0F to Color(0xFF2E6393),
            0.72F to Color(0xFF4DA3AF),
            1F to Color(0xFF77FFD6)
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Vos récéptions Mobile et Wifi",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color(0xBAFFFFFF),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp)
        )
        Text(
            "${pkgNetStatService.received} (${modelService.received})",
            textAlign = TextAlign.Center,
            color = Color(0xBAFFFFFF),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 25.sp,
            letterSpacing = TextUnit(-0.25F, TextUnitType.Sp)
        )
    }
}

@Composable
fun Equivalent(image: Int, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(id = image), text, modifier = Modifier.width(48.dp), contentScale = ContentScale.Crop)
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = Color.Black.copy(alpha = 0.4F),
            letterSpacing = (-0.5).sp
        )
    }
}

@Preview
@Composable
fun DashboardPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        Dashboard()
    }
}