package fr.umontpellier.ecotracker.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun CustomGroupButton(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChanged: (Int) -> Unit,
    cornerRadius: Dp = 8.dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        items.forEachIndexed { index, item ->
            OutlinedButton(
                onClick = { onSelectedIndexChanged(index) },
                shape = when (index) {
                    0 -> RoundedCornerShape(
                        topStart = cornerRadius,
                        topEnd = 0.dp,
                        bottomStart = cornerRadius,
                        bottomEnd = 0.dp
                    )

                    items.size - 1 -> RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = cornerRadius,
                        bottomStart = 0.dp,
                        bottomEnd = cornerRadius
                    )

                    else -> RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                },
                border = BorderStroke(
                    1.dp, if (selectedIndex == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Black
                    }
                ),
                colors = if (selectedIndex == index) {
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color.White)
                },
                modifier = when (index) {
                    0 -> Modifier.height(30.dp)
                        .weight(1f).fillMaxWidth()

                    else -> Modifier
                        .height(30.dp).weight(1f).fillMaxWidth()
                },
                contentPadding = PaddingValues(0.dp)

            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = if (selectedIndex == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Black.copy(alpha = 0.9f)
                    }
                )
            }
        }

    }
}

@Preview
@Composable
fun CustomGroupButtonPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    CustomGroupButton(
        items = listOf("Item 1", "Item 2", "Item 3"),
        selectedIndex = selectedIndex,
        onSelectedIndexChanged = { newIndex -> selectedIndex = newIndex }
    )
}