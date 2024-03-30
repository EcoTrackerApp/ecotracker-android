package fr.umontpellier.carbonalyser.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class DataColors {
    companion object {

        val PastelRed = Color(0xFFFF6961)
        val PastelGreen = Color(0xFF77DD77)
        val PastelBlue = Color(0xFFAEC6CF)
        val PastelPurple = Color(0xFFB39EB5)
        val PastelOrange = Color(0xFFFFB347)
        val PastelPink = Color(0xFFFFB6C1)
        val PastelBrown = Color(0xFFA17A74)
        val PastelCyan = Color(0xFF7FFFD4)
        val PastelLime = Color(0xFF32CD32)
        val PastelMagenta = Color(0xFFFF00FF)
        val PastelTeal = Color(0xFF008080)
        val PastelLavender = Color(0xFFE6E6FA)
        val PastelMaroon = Color(0xFF800000)
        val PastelOlive = Color(0xFF808000)
        val PastelCoral = Color(0xFFFF7F50)
        val PastelGold = Color(0xFFFFD700)
        val PastelSkyBlue = Color(0xFF87CEEB)

        val colorsList = listOf(
            PastelRed, PastelGreen, PastelBlue, PastelPurple, PastelOrange, PastelPink,
            PastelBrown, PastelCyan, PastelLime, PastelMagenta, PastelTeal, PastelLavender,
            PastelMaroon, PastelOlive, PastelCoral, PastelGold, PastelSkyBlue
        )
        fun getColor(index: Int): Color {
            return colorsList[index % colorsList.size]
        }

        fun getDefaultColor(): Color {
            return Color(0xFF000000)
        }

    }
}