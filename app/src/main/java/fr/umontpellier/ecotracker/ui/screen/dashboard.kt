package fr.umontpellier.ecotracker.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
import fr.umontpellier.ecotracker.service.model.unit.CO2
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import fr.umontpellier.ecotracker.ui.component.Alert
import org.koin.compose.koinInject

@Composable
fun Dashboard(
    pkgNetStatService: PkgNetStatService = koinInject()
) {
    DashboardPreview()
}

@Composable
fun Header(co2: CO2) {
    Column(verticalArrangement = Arrangement.spacedBy((-32).dp)) {
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
                    style = TextStyle.Default.copy(
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        drawStyle = Stroke(
                            miter = 10f,
                            width = 0.5f,
                            join = StrokeJoin.Round
                        )
                    )
                )
            }
            Text(text = "prout")
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
            color = Color(0xFFEBFFE1),
            fontSize = TextUnit(18f, TextUnitType.Sp),
            lineHeight = TextUnit(22f, TextUnitType.Sp),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp)
        )
        Text(
            "Continuez comme ça, c'est excellent !",
            textAlign = TextAlign.Center,
            color = Color(0xFFEBFFE1),
            fontWeight = FontWeight.ExtraBold,
            fontSize = TextUnit(9f, TextUnitType.Sp),
            lineHeight = TextUnit(12f, TextUnitType.Sp),
            letterSpacing = TextUnit(-0.25F, TextUnitType.Sp)
        )
    }
}

@Preview
@Composable
fun DashboardPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Header(CO2(10000.0))
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            AverageAlert()
        }
    }
}