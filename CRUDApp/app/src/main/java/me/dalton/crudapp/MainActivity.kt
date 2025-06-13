package me.dalton.crudapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UserApp(viewModel) }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserApp(viewModel: UserViewModel) {
    val usuarios by viewModel.users.observeAsState(emptyList())
    var nome by remember { mutableStateOf("") }
    var idade by remember { mutableStateOf("") }
    var usuarioEditando by remember { mutableStateOf<User?>(null) }
    var mensagemErro by remember { mutableStateOf<String?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciamento de Usuários") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                if (usuarioEditando == null) "Adicionar um Novo Usuário" else "Editar Usuário",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = idade,
                onValueChange = { idade = it },
                label = { Text("Idade") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        if (nome.isNotEmpty() && idade.isNotEmpty()) {
                            val idadeInt = idade.toInt()
                            if (usuarioEditando == null) {
                                viewModel.addUser(User(name = nome, age = idadeInt))
                            } else {
                                viewModel.updateUser(usuarioEditando!!.copy(name = nome, age = idadeInt))
                                usuarioEditando = null
                            }
                            nome = ""
                            idade = ""
                            mensagemErro = null
                        }
                    } catch (_: NumberFormatException) {
                        mensagemErro = "A idade deve ser um número válido"
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (usuarioEditando == null) "Adicionar Usuário" else "Salvar Alterações")
            }

            mensagemErro?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray, thickness = 1.dp)

            Text(
                "Lista de Usuários",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn {
                items(usuarios) { usuario ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(usuario.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Idade: ${usuario.age}", color = Color.Gray, fontSize = 14.sp)
                            }
                            IconButton(onClick = {
                                nome = usuario.name
                                idade = usuario.age.toString()
                                usuarioEditando = usuario
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar Usuário", tint = Color.Blue)
                            }
                            IconButton(onClick = { viewModel.deleteUser(usuario) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir Usuário", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}