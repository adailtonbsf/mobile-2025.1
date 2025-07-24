package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import me.daltonbsf.unirun.model.Chat
import me.daltonbsf.unirun.model.getLastMessageDate
import me.daltonbsf.unirun.ui.components.ChatCard
import me.daltonbsf.unirun.ui.components.ChatSwitchButton
import me.daltonbsf.unirun.viewmodel.AuthViewModel
import me.daltonbsf.unirun.viewmodel.ChatViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedChatIndex by remember { mutableIntStateOf(-1) }
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val userChatList by chatViewModel.chatList.collectAsState()
    val searchResults by authViewModel.searchResults.collectAsState()
    var chatWithTitles by remember { mutableStateOf<List<Pair<Chat, String>>>(emptyList()) }

    LaunchedEffect(userChatList) {
        val titles = userChatList
            .filter { it.type == "private" }
            .map { chat ->
                chat to chatViewModel.getChatTitle(chat)
            }
        chatWithTitles = titles
    }

    LaunchedEffect(searchText) {
        if (searchText.length > 2) {
            isLoading = true
            delay(500) // Debounce para evitar buscas a cada tecla digitada
            authViewModel.searchUsers(searchText)
            isLoading = false
        } else {
            authViewModel.searchUsers("") // Limpa os resultados se a busca for curta
        }
    }

    val filteredList = chatWithTitles
        .filter { (_, title) ->
            title.contains(searchText, ignoreCase = true)
        }
        .sortedByDescending { (chat, _) -> chat.getLastMessageDate() }
        .sortedByDescending { (chat, _) -> chat.isPinned.value }


    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        ChatSwitchButton(navController)
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Pesquisar conversas ou usuários") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpar",
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable { searchText = "" }
                    )
                }
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                itemsIndexed(filteredList) { index, (chat, _) ->
                    Card(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .combinedClickable(
                                onClick = {
                                    val chatId = chat.id
                                    navController.navigate("peopleChat/$chatId")
                                },
                                onLongClick = {
                                    selectedChatIndex = index
                                    showDialog = true
                                }
                            )
                    ) {
                        ChatCard(chat, authViewModel, chatViewModel)
                    }
                }

                if (searchResults.isNotEmpty() && searchText.isNotBlank()) {
                    item {
                        Text(
                            "Usuários encontrados",
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(searchResults) { user ->
                        Card(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .clickable {
                                    chatViewModel.createPrivateChat(user.uid) { chatId ->
                                        navController.navigate("peopleChat/$chatId")
                                    }
                                }
                        ) {
                            Text(
                                text = "${user.name} (@${user.username})",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    if (showDialog && selectedChatIndex >= 0 && selectedChatIndex < filteredList.size) {
        val selectedChat = filteredList[selectedChatIndex].first
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    if (selectedChat.isPinned.value) "Desafixar conversa" else "Fixar conversa",
                )
            },
            text = {
                Text(
                    if (selectedChat.isPinned.value) "Você deseja desfixar esta conversa?"
                    else "Você deseja fixar esta conversa?"
                )
            },
            confirmButton = {
                Button(onClick = {
                    selectedChat.isPinned.value = !selectedChat.isPinned.value
                    showDialog = false
                }) {
                    Text("Sim")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Não")
                }
            }
        )
    }
}