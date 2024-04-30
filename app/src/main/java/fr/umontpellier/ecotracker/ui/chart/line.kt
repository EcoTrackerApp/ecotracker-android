package fr.umontpellier.ecotracker.ui.chart

import android.graphics.Color.parseColor
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import fr.umontpellier.ecotracker.ecoTrackerPreviewModule
import fr.umontpellier.ecotracker.service.model.unit.Bytes
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject


@Composable
fun LineConsumptionChart(
    pkgNetStatService: PkgNetStatService = koinInject(),
    modifier: Modifier = Modifier
) {
    // Préparation des données
    val monthConsumptionSent = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
        day to Bytes(perApp.map { (_, data) -> data.sent.value }.sum())
    }.toMap()

    val monthConsumptionReceived = pkgNetStatService.cache.appNetStats.map { (day, perApp) ->
        day to Bytes(perApp.map { (_, data) -> data.received.value }.sum())
    }.toMap()

    val entriesSent = monthConsumptionSent.entries.mapIndexed { index, (day, bytesList) ->
        Entry(index.toFloat(), bytesList.value.toFloat())
    }
    val entriesReceived = monthConsumptionReceived.entries.mapIndexed { index, (day, bytesList) ->
        Entry(index.toFloat(), bytesList.value.toFloat())
    }

    val dataSetSent = LineDataSet(entriesSent, "Envoyé").apply {
        color = parseColor("#fcae60")
        setDrawValues(false)
        lineWidth = 6f
    }
    val dataSetReceived = LineDataSet(entriesReceived, "Reçu").apply {
        color = parseColor("#2a7bb5")
        setDrawValues(false)
        lineWidth = 10f
    }

    val lineData = LineData(dataSetSent, dataSetReceived)

    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .padding(16.dp)
    ) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    this.data = lineData

                    // Configuration du graphique
                    this.legend.isEnabled = true
                    this.legend.textSize = 12f
                    this.description.isEnabled = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
@Preview
@Composable
fun PieChartPreview() {
    KoinApplication(application = { modules(ecoTrackerPreviewModule) }) {
        LineConsumptionChart()
    }
}