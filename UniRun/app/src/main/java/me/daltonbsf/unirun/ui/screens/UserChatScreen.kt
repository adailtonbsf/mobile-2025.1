package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.daltonbsf.unirun.models.userChatList
import me.daltonbsf.unirun.ui.components.ChatCard
import me.daltonbsf.unirun.ui.components.ChatSwitchButton

@ExperimentalFoundationApi
@Composable
fun UserChatScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedChatIndex by remember { mutableIntStateOf(-1) }
    var searchText by remember { mutableStateOf("") }

    val filteredList = userChatList
        .sortedByDescending { it.getLastMessage()?.date }
        .sortedByDescending { it.isPinned.value }
        .filter { it.getName().contains(searchText, ignoreCase = true) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        ChatSwitchButton(navController)
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Pesquisar") },
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
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(filteredList.size) { index ->
                val chat = filteredList[index]
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .combinedClickable (
                            onClick = {
                                navController.navigate("peopleChat/${chat.getName()}")
                                chat.unread.value = false
                            },
                            onLongClick = {
                                selectedChatIndex = index
                                showDialog = true
                            }

                        )
                ) {
                    ChatCard(chat)
                }
            }
        }
    }
    if (showDialog && selectedChatIndex >= 0) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    if (filteredList[selectedChatIndex].isPinned.value) "Desafixar conversa" else "Fixar conversa",
                )
            },
            text = {
                Text(
                    if (filteredList[selectedChatIndex].isPinned.value) "Você deseja desfixar esta conversa?"
                    else "Você deseja fixar esta conversa?"
                )
            },
            confirmButton = {
                Button(onClick = {
                    filteredList[selectedChatIndex].isPinned.value = !filteredList[selectedChatIndex].isPinned.value
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