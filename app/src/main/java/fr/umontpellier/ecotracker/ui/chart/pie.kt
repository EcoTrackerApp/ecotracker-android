import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import fr.umontpellier.ecotracker.service.model.Model
import fr.umontpellier.ecotracker.ui.util.getPackageName
import org.koin.compose.koinInject

@Composable
fun PieConsumptionChart(
    context: Context = koinInject(),
    entries: List<Map.Entry<Int, Model.AppEmission>>,
    modifier: Modifier = Modifier
) {
    val dataSet = PieDataSet(entries.map {
        PieEntry(
            it.value.total.value.toFloat(),
            context.packageManager.getPackageName(it.key)
        )
    }, "")
    dataSet.colors = ColorTemplate.PASTEL_COLORS.plus(ColorTemplate.JOYFUL_COLORS).toMutableList()
    dataSet.valueTextColor = Color.Black.toArgb()
    dataSet.valueTextSize = 24F
    val pieData = PieData(dataSet)

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                this.data = pieData
                this.setEntryLabelTextSize(10F)
                this.setEntryLabelColor(Color.Black.toArgb())
                this.legend.setEntries(emptyList())
            }
        },
        modifier = Modifier.then(modifier)
    )
}