package me.dalton.cruditemapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dalton.cruditemapp.ui.theme.CRUDItemAPPTheme
import me.dalton.cruditemapp.ui.view.ItemScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CRUDItemAPPTheme {
                Scaffold(
                    modifier = Modifier.fillMaxWidth(),
                    content = { paddingValues ->
                        ItemScreen(
                            modifier = Modifier
                                .padding(paddingValues)
                                .padding(horizontal = 16.dp)
                        )
                    }
                )
            }
        }
    }
}
