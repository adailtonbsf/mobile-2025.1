package me.daltonbsf.authapp2.ui.view

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import me.daltonbsf.authapp2.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val googleSignInClient = authViewModel.getGoogleSignInClient(context)

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    authViewModel.loginWithGoogle(idToken) { success ->
                        if (success) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Log.e("LoginScreen", "Falha no login com Google.")
                        }
                    }
                }
            } catch (e: ApiException) {
                Log.w("LoginScreen", "Login com Google falhou", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bem-vindo ao AuthApp!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                authViewModel.login(email, password) { success ->
                    if (success) {
                        navController.navigate("home") { popUpTo("login") { inclusive = true } }
                    } else {
                        Log.e("LoginScreen", "Email ou senha inválidos.")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.Medium
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.Medium
        ) {
            Text("Entrar com Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Criar conta",
            modifier = Modifier
                .clickable { navController.navigate("register") }
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Esqueci minha senha",
            modifier = Modifier
                .clickable { navController.navigate("reset_password") }
                .padding(vertical = 8.dp)
        )
    }
}