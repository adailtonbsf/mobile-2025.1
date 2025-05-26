package me.daltonbsf.unirun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CaronasPessoasSwitch(navController: NavController, isPeopleSelected: Boolean = true) {
    Row(
        modifier = Modifier
            .height(50.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0xFFEFEFEF))
    ) {
        var isPeopleSelected by remember { mutableStateOf(isPeopleSelected) }
        listOf("Caronas", "Pessoas").forEach { option ->
            val isSelected = (option == if (isPeopleSelected) "Pessoas" else "Caronas")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable {
                        // isSelected é true se a 'option' atual do loop já é a aba selecionada
                        // Se !isSelected, significa que o usuário clicou em uma aba diferente
                        if (!isSelected) {
                            isPeopleSelected = option == "Pessoas" // Atualiza o estado para a nova seleção
                            if (isPeopleSelected) { // Navega com base no estado atualizado
                                navController.navigate("chats/people")
                            } else {
                                navController.navigate("chats/carona")
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}