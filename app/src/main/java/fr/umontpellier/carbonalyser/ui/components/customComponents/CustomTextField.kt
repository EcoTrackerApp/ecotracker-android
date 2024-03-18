package fr.umontpellier.carbonalyser.ui.components.customComponents


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isMenuExpanded: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = value,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            imageVector = if (isMenuExpanded) Icons.Default.PlayArrow else Icons.Default.ArrowDropDown,
            contentDescription = "Dropdown Icon",
            tint = Color.DarkGray,
            modifier = Modifier.size(24.dp)
        )
    }
}


@Preview
@Composable
fun CustomTextFieldPreview() {
    CustomTextField(
        value = "CustomTextField",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth()
    )
}