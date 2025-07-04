package me.dalton.msgapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import me.dalton.msgapp.ui.view.ChatScreen
import me.dalton.msgapp.ui.view.RoomSelector
import me.dalton.msgapp.ui.view.notifyNewMessage
import me.dalton.msgapp.viewmodel.MsgViewModel

class MainActivity : ComponentActivity() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MsgAppTheme {
                MsgAppRoot()
            }
        }
    }
}

@Composable
fun MsgAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF42A5F5)
        ),
        content = content
    )
}

@Composable
@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun MsgAppRoot(vm: MsgViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Login anônimo do Firebase
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val user by produceState(initialValue = firebaseAuth.currentUser) {
        if (value == null) {
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task -> value = firebaseAuth.currentUser }
        }
    }
    val userId = user?.uid ?: "joao"
    var userName by remember { mutableStateOf("Usuário-${userId.takeLast(4)}") }
    var currentRoom by remember { mutableStateOf("geral") }
    var lastNotifiedId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentRoom) {
        vm.switchRoom(currentRoom)
    }

    Column {
        RoomSelector(onRoomSelected = { if (it.isNotBlank()) currentRoom = it })
        ChatScreen(
            username = userName,
            userId = userId,
            messages = vm.messages.collectAsState().value,
            onSend = { text -> vm.sendMessage(userId, userName, text) },
            currentRoom = currentRoom,
            lastNotifiedId = lastNotifiedId,
            onNotify = { msg ->
                notifyNewMessage(context, msg)
                lastNotifiedId = msg.id
            }
        )
    }
}