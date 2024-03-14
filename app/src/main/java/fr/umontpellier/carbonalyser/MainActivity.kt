package fr.umontpellier.carbonalyser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.umontpellier.carbonalyser.ui.theme.CarbonalyserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarbonalyserTheme {
            }
        }
    }
}
