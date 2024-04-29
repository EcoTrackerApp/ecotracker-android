package fr.umontpellier.ecotracker.ui.util

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp

@Immutable
data class Shadow(
    @Stable val offsetX: Dp,
    @Stable val offsetY: Dp,
    @Stable val radius: Dp,
    @Stable val color: Color,
)

fun Modifier.withShadow(
    shadow: Shadow,
    shape: Shape,
) = drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        paint.asFrameworkPaint().apply {
            this.color = Color.Transparent.toArgb()
            setShadowLayer(
                shadow.radius.toPx(),
                shadow.offsetX.toPx(),
                shadow.offsetY.toPx(),
                shadow.color.toArgb(),
            )
        }
        val outline = shape.createOutline(size, layoutDirection, this)
        canvas.drawOutline(outline, paint)
    }
}