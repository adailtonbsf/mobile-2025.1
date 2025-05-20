package me.daltonbsf.unirun.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.daltonbsf.unirun.R

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onSignUpClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier
                .size(512.dp)
        )
        Text(
            text = "UniRun",
            textAlign = TextAlign.Center,
            fontSize = 72.sp,
            modifier = Modifier.offset(y = (-168).dp)
        )
        Text(
            text = "Corridas acessíveis",
            textAlign = TextAlign.Center,
            fontSize = 26.sp,
            modifier = Modifier.offset(y = (-124).dp)
        )
        Text(
            text = "no campus",
            textAlign = TextAlign.Center,
            fontSize = 26.sp,
            modifier = Modifier.offset(y = (-124).dp)
        )
        Button(
            onClick = { onLoginSuccess() },
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
            modifier = Modifier.padding(top = 64.dp)
        ) {
            Text(text = "Entrar com e-mail institucional", fontSize = 22.sp)
        }
        TextButton(
            onClick = { onSignUpClick() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Cadastrar-se", fontSize = 22.sp)
        }
    }
}