package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.daltonbsf.unirun.model.peopleChatList
import me.daltonbsf.unirun.ui.components.ChatSwitchButton
import me.daltonbsf.unirun.ui.components.ChatCard

@Composable
fun PeopleChatScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        ChatSwitchButton(navController)
        LazyColumn {
            items(peopleChatList.size) { index ->
                val chat = peopleChatList[index]
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("peopleChat/${chat.getName()}")
                        }
                ) {
                    ChatCard(chat)
                }
            }
        }
    }
}