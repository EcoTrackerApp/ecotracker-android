package fr.umontpellier.ecotracker.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Alert(
    gradient: Brush,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(gradient)
            .then(modifier)
    ) {
        content?.invoke()
    }
}

@Preview
@Composable
fun AlertPreview() {
    Alert(
        horizontalGradient(
            0F to Color(0xFFABCF9A), 0.72F to Color(0xFF7E9972),
            1F to Color(0xFF57694E)
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text("Text", textAlign = TextAlign.Center, color = Color(0xFFEBFFE1))
    }
}