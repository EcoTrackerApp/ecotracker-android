package fr.umontpellier.carbonalyser.ui.components.customComponents


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(
    value: String,
    modifier: Modifier = Modifier,
    isMenuExpanded: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .wrapContentWidth()
            .height(30.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(end = 2.dp),
            maxLines = 1,
        )
        Icon(
            imageVector = if (isMenuExpanded) Icons.Default.ExitToApp else Icons.Default.ArrowDropDown,
            contentDescription = "Dropdown Icon",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
    }
}



@Preview
@Composable
fun CustomTextFieldPreview() {
    CustomTextField(
        value = "CustomTextField",
        modifier = Modifier.height(30.dp)
    )
}