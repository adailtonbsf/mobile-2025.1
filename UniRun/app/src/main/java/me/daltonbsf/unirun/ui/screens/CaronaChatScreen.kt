package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.daltonbsf.unirun.ui.components.ChatCard
import me.daltonbsf.unirun.ui.components.ChatSwitchButton
import me.daltonbsf.unirun.viewmodel.AuthViewModel
import me.daltonbsf.unirun.viewmodel.ChatViewModel

@Composable
fun CaronaChatScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel
) {
    val chatList by chatViewModel.chatList.collectAsState()
    val currentUser by authViewModel.user.collectAsState()

    val caronaChats = currentUser?.let { user ->
        chatList.filter { it.type == "group" && it.participants.contains(user.uid) }
    } ?: emptyList()

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        ChatSwitchButton(navController, false)
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(caronaChats) { chat ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("caronaChat/${chat.id}")
                        }
                ) {
                    Row {
                        ChatCard(
                            chat = chat,
                            chatViewModel = chatViewModel
                        )
                    }
                }
            }
        }
    }
}