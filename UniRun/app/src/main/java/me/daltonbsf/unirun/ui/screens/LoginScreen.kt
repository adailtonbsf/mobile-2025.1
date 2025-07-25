package me.daltonbsf.unirun.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import me.daltonbsf.unirun.R
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import me.daltonbsf.unirun.data.LoginStatus
import me.daltonbsf.unirun.ui.components.FullScreenLoading
import me.daltonbsf.unirun.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel, onThemeToggle: () -> Unit, isDarkTheme: Boolean) {
    var isLoading by remember { mutableStateOf(false) }
    var showEmailNotVerifiedDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    if (showResetPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showResetPasswordDialog = false },
            title = { Text("Recuperar Senha") },
            text = {
                Column {
                    Text("Digite seu e-mail para enviarmos as instruções de recuperação.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("E-mail") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.resetPassword(resetEmail) { success ->
                            if (success) {
                                Toast.makeText(context, "E-mail de recuperação enviado!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Falha ao enviar e-mail.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showResetPasswordDialog = false
                        resetEmail = ""
                    }
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetPasswordDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "UniRun",
                    textAlign = TextAlign.Center,
                    fontSize = 72.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Corridas acessíveis\nno campus",
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    lineHeight = 30.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail Institucional") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email Icon")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        isLoading = true
                        authViewModel.login(email, password) { status ->
                            isLoading = false
                            when (status) {
                                LoginStatus.SUCCESS -> {
                                    navController.navigate("chats/people") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                                LoginStatus.INVALID_CREDENTIALS -> {
                                    Log.e("LoginScreen", "Login failed for email: $email")
                                    Toast.makeText(
                                        navController.context,
                                        "E-mail ou senha inválidos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                LoginStatus.EMAIL_NOT_VERIFIED -> {
                                    Log.e("LoginScreen", "Login failed for email: $email (not verified)")
                                    showEmailNotVerifiedDialog = true
                                }
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Entrar",
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { showResetPasswordDialog = true }) {
                    Text("Esqueci minha senha")
                }
                TextButton(onClick = { navController.navigate("registration") }) {
                    Text(
                        text = "Não tem uma conta? Registre-se",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        if (isLoading) {
            FullScreenLoading()
        }
        if (showEmailNotVerifiedDialog) {
            AlertDialog(
                onDismissRequest = { showEmailNotVerifiedDialog = false },
                title = { Text("Verificação de E-mail") },
                text = { Text("Por favor, verifique seu e-mail para continuar. Se não recebeu o e-mail, clique em reenviar.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isLoading = true
                            authViewModel.sendEmailVerification { success ->
                                isLoading = false
                                val message = if (success) {
                                    "E-mail de verificação reenviado com sucesso!"
                                } else {
                                    "Falha ao reenviar o e-mail de verificação."
                                }
                                Toast.makeText(navController.context, message, Toast.LENGTH_LONG).show()
                            }
                            showEmailNotVerifiedDialog = false
                            authViewModel.logout()
                        }
                    ) {
                        Text("Reenviar e-mail")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showEmailNotVerifiedDialog = false
                        authViewModel.logout()
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}