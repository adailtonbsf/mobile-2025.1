package me.dalton.numbergeneratorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.dalton.numbergeneratorapp.ui.theme.NumberGeneratorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NumberGeneratorAppTheme {
                NumberGeneratorScreen()
            }
        }
    }
}