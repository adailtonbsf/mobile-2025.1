package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.daltonbsf.unirun.model.caronaChatList
import me.daltonbsf.unirun.ui.components.CaronasPessoasSwitch
import me.daltonbsf.unirun.ui.components.ChatCard

@Composable
fun CaronaChatScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        CaronasPessoasSwitch(navController, false)
        LazyColumn {
            items(caronaChatList.size) { index ->
                val chat = caronaChatList[index]
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("caronaChat/${chat.getName()}")
                        }
                ) {
                    Row {
                        ChatCard(chat)
                    }
                }
            }
        }
    }
}