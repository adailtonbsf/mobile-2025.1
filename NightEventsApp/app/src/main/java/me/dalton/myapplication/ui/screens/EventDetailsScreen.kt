package me.dalton.myapplication.ui.screens

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import me.dalton.myapplication.models.eventList

@Composable
fun EventDetailsScreen(eventId: String?) {
    val event = eventList.find { it.id.toString() == eventId }
    event?.let {
        Card(
            
        ) {  }
    }
}