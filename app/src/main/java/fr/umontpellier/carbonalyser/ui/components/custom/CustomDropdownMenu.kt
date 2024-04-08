package fr.umontpellier.carbonalyser.ui.components.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = option,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                },
                onClick = {
                    onOptionSelected(option)
                    onDismissRequest()
                },
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(57.dp),
                contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
            )
        }
    }
}

@Preview
@Composable
fun CustomDropdownMenuPreview() {
    CustomDropdownMenu(
        expanded = true,
        onDismissRequest = {},
        options = listOf("Option 1", "Option 2", "Option 3"),
        onOptionSelected = {}
    )
}
