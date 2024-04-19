package fr.umontpellier.ecotracker.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.umontpellier.ecotracker.R
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.model.ModelService
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.model.unit.CO2
import fr.umontpellier.ecotracker.service.model.unit.Meter
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import fr.umontpellier.ecotracker.ui.component.Alert
import fr.umontpellier.ecotracker.ui.util.dateFormatter
import org.koin.compose.koinInject
import java.time.Instant

@Composable
fun Dashboard(
    config: MutableState<EcoTrackerConfig> = koinInject(),
    pkgNetStatService: PkgNetStatService = koinInject(),
    modelService: ModelService = koinInject(),
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.padding(bottom = 32.dp)) {
        Header(modelService.total, start = config.value.interval.first, end = config.value.interval.second)
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
                modifier = Modifier.padding(horizontal = 8.dp),
                letterSpacing = (-0.5).sp
            )
            BytesReceived(pkgNetStatService.received, modelService.received)
            BytesSent(pkgNetStatService.sent, modelService.sent)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Concrètement, qu'est-ce que ça veut dire?",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 8.dp),
                letterSpacing = (-0.5).sp
            )
            Equivalent(image = R.drawable.car, text = "C'est ${modelService.total.carKm} en voiture...")
            Equivalent(image = R.drawable.train, text = "ou bien ${modelService.total.tgvKm} en train")
        }
    }
}

@Composable
fun Header(co2: CO2, start: Instant, end: Instant) {
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
                    text = co2.toString(),
                    color = Color.Green,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
            }
            Column {
                Text(
                    text = "du ${dateFormatter.format(start)}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 24.sp
                )
                Text(
                    text = "au ${dateFormatter.format(end)}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun AverageAlert() {
    Alert(
        horizontalGradient(
            0F to Color(0xFFABCF9A),
            0.72F to Color(0xFF7E9972),
            1F to Color(0xFF57694E)
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Vous êtes dans la moyenne !",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xBAFFFFFF),
            fontSize = TextUnit(18f, TextUnitType.Sp),
            lineHeight = TextUnit(22f, TextUnitType.Sp),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp)
        )
        Text(
            "Continuez comme ça, c'est excellent !",
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
fun BytesSent(bytes: Bytes, co2: CO2) {
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
            "Vos émissions 4G et Wifi",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color(0xBAFFFFFF),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = (-0.5F).sp
        )
        Text(
            "$bytes ($co2)",
            textAlign = TextAlign.Center,
            color = Color(0xBAFFFFFF),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 25.sp,
            letterSpacing = (-0.25F).sp
        )
    }
}

@Composable
fun BytesReceived(bytes: Bytes, co2: CO2) {
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
            "Vos récéptions 4G et Wifi",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color(0xBAFFFFFF),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp)
        )
        Text(
            "$bytes ($co2)",
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
    Column(verticalArrangement = Arrangement.spacedBy(48.dp), modifier = Modifier.padding(bottom = 32.dp)) {
        Header(CO2(10000.0), start = Instant.now(), end = Instant.now())
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
                modifier = Modifier.padding(horizontal = 8.dp),
                letterSpacing = (-0.5).sp
            )
            BytesReceived(bytes = Bytes(2000), co2 = CO2(100.0))
            BytesSent(bytes = Bytes(2000), co2 = CO2(100.0))
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Concrètement, qu'est-ce que ça veut dire?",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                letterSpacing = (-0.5).sp,
            )
            Equivalent(image = R.drawable.car, text = "C'est ${Meter(100.0)} en voiture...")
            Equivalent(image = R.drawable.train, text = "ou bien ${Meter(100.0)} en train")
        }
    }
}